package com.nanodalvik.data.kotlin

import com.nanodalvik.data.NanoDalvikVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class NanoDalvikVMKotlinImpl(
        private val lexer: Lexer,
        private val executionEngine: ExecutionEngine
): NanoDalvikVM {
    private val _output = MutableSharedFlow<List<LogEntry>>(replay = 0)
    private val _stackState = MutableSharedFlow<List<Int>>(replay = 0)
    private val _ipToSourcePosition = MutableSharedFlow<Pair<Int, SourcePosition>>(replay = 0)
    private val logsToEmit = mutableListOf<LogEntry>()

    override fun startUp() {
        println("Dalvik VM started")
    }

    override fun isProgramLoaded(): Boolean = executionEngine.hasNextOp()

    override fun loadProgram(code: String) {
        val tokens = lexer.tokenize(code)
        val program = Parser(tokens, ErrorReporter()).parse()
        val loadingError = executionEngine.loadProgram(program)

        if (loadingError != null) {
            logsToEmit.addAll(loadingError.errors.map { line -> LogEntry.ErrorLogEntry(line) })
            return
        }
    }

    override suspend fun executeProgram() {
        while (executionEngine.hasNextOp()) {
            processNextOp()
        }
    }

    override suspend fun executeNextOp() {
        if (executionEngine.hasNextOp()) {
            processNextOp()
        } else {
            clear()
        }
    }

    private suspend fun processNextOp() {
        val executionResult = executionEngine.executeNextOp()
        logsToEmit.addAll(executionResult.output.map { line -> LogEntry.OutputLogEntry(line) })
        _output.emit(logsToEmit)
        _stackState.emit(executionResult.stackState)
        _ipToSourcePosition.emit(Pair(executionResult.ipCounter, executionResult.nextOpSourcePosition))
    }

    override suspend fun clear() {
        executionEngine.unloadProgram()
        _output.emit(emptyList())
        _stackState.emit(emptyList())
    }

    override fun observeOutput(): Flow<List<LogEntry>> = _output
    override fun observeStackState(): Flow<List<Int>> = _stackState
    override fun observeSourcePosition(): Flow<Pair<Int, SourcePosition>> = _ipToSourcePosition
}