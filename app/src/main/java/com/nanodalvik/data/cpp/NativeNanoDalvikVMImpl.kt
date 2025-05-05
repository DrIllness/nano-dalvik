package com.nanodalvik.data.cpp

import com.nanodalvik.data.NanoDalvikVM
import com.nanodalvik.data.kotlin.LogEntry
import com.nanodalvik.data.kotlin.SourcePosition
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class NativeNanoDalvikVMImpl : NanoDalvikVM {
    init {
        System.loadLibrary("nanodalvik_vm")
    }

    private var logsToEmit = mutableListOf<LogEntry>()
    private val _output = MutableSharedFlow<List<LogEntry>>(replay = 0)
    private val _stackState = MutableSharedFlow<List<Int>>(replay = 0)
    private val _ipToSourcePosition = MutableSharedFlow<Pair<Int, SourcePosition>>(replay = 0)

    override fun startUp() {
        startUpNative()
    }

    override fun isProgramLoaded(): Boolean {
        return false
    }

    override fun loadProgram(code: String) {
        loadProgramNative(code)
    }

    override suspend fun executeProgram() {
        executeProgramNative()
    }

    override suspend fun executeNextOp() {
        executeNextOpNative()
    }

    fun executionResult(result: NativeOPExecutionResult) {
        logsToEmit.add(LogEntry.OutputLogEntry(result.output ?: ""))
        GlobalScope.launch {
            _output.emit(logsToEmit)
        }
    }

    override suspend fun clear() {
        clearNative()
    }

    override fun observeOutput(): Flow<List<LogEntry>> = _output

    override fun observeStackState(): Flow<List<Int>> = _stackState

    override fun observeSourcePosition(): Flow<Pair<Int, SourcePosition>> = _ipToSourcePosition

    private external fun loadProgramNative(code: String)
    private external fun startUpNative()
    private external fun clearNative()
    private external fun executeNextOpNative()
    private external fun executeProgramNative()

}

class NativeOPExecutionResult(
        val ip: Int,
        val output: String?,
        val error: String?
)