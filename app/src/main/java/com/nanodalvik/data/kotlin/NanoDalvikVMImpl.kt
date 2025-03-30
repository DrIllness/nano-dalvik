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


    override fun startUp() {
        println("Dalvik VM started")
    }

    override suspend fun execute(code: String, args: List<String>) {
        _output.emit(emptyList())
        _stackState.emit(emptyList())

        val tokens = lexer.tokenize(code)
        val program = Parser(tokens, ErrorReporter()).parse()
        val result = executionEngine.execute(program)
        val logsToEmit = mutableListOf<LogEntry>()
        logsToEmit.addAll(result.output.map { line -> LogEntry.OutputLogEntry(line) })
        logsToEmit.addAll(result.errors.map { line -> LogEntry.ErrorLogEntry(line) })

        _output.emit(logsToEmit)
        _stackState.emit(result.stackState)
    }

    override fun shutDown() {
        println("Dalvik VM shut down")
    }

    override fun observeOutput(): Flow<List<LogEntry>> = _output
    override fun observeStackState(): Flow<List<Int>> = _stackState
}