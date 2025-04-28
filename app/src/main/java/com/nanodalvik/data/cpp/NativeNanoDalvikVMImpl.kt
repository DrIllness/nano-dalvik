package com.nanodalvik.data.cpp

import com.nanodalvik.data.NanoDalvikVM
import com.nanodalvik.data.kotlin.LogEntry
import com.nanodalvik.data.kotlin.SourcePosition
import kotlinx.coroutines.flow.Flow

class NativeNanoDalvikVMImpl : NanoDalvikVM {
    init {
        System.loadLibrary("nanodalvik_vm")
    }

    override fun startUp() {
        TODO("Not yet implemented")
    }

    override fun isProgramLoaded(): Boolean {
        TODO("Not yet implemented")
    }

    override fun loadProgram(code: String) {
        loadProgramNative(code)
        nativeTestHello()
    }

    override suspend fun executeProgram() {
        TODO("Not yet implemented")
    }

    override suspend fun executeNextOp() {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    override fun observeOutput(): Flow<List<LogEntry>> {
        TODO("Not yet implemented")
    }

    override fun observeStackState(): Flow<List<Int>> {
        TODO("Not yet implemented")
    }

    override fun observeSourcePosition(): Flow<Pair<Int, SourcePosition>> {
        TODO("Not yet implemented")
    }

    private external fun loadProgramNative(code: String)
    private external fun nativeTestHello()

}