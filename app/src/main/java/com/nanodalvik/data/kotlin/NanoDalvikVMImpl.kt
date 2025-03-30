package com.nanodalvik.data.kotlin

import com.nanodalvik.data.NanoDalvikVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class NanoDalvikVMKotlinImpl(
        private val lexer: Lexer,
        private val executionEngine: ExecutionEngine
): NanoDalvikVM {
    private val _output = MutableSharedFlow<String>(replay = 0)
    private val _errors = MutableSharedFlow<String>(replay = 0)

    override fun startUp() {
        println("Dalvik VM started")
    }

    override suspend fun execute(code: String, args: List<String>) {
        val tokens = lexer.tokenize(code)
        val program = Parser(tokens, ErrorReporter()).parse()
        val result = executionEngine.execute(program)
        result.output.forEach { line ->
            _output.emit(line)
        }
        result.errors.forEach { err ->
            _errors.emit(err)
        }
    }

    override fun shutDown() {
        println("Dalvik VM shut down")
    }

    override fun observeErrorOutput(): Flow<String> = _errors

    override fun observeOutput(): Flow<String> = _output
}