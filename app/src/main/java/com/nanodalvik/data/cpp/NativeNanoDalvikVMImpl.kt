package com.nanodalvik.data.cpp

import com.nanodalvik.data.NanoDalvikVM
import com.nanodalvik.data.kotlin.LogEntry
import com.nanodalvik.data.kotlin.SourcePosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NativeNanoDalvikVMImpl : NanoDalvikVM {
    init {
        System.loadLibrary("nanodalvik_vm")
    }

    override fun startUp() {
        startUpNative()
    }

    override fun isProgramLoaded(): Boolean {
        return true
    }

    override fun loadProgram(code: String) {
        loadProgramNative(code)
    }

    override suspend fun executeProgram() {
        //
    }

    override suspend fun executeNextOp() {
        executeNextOpNative()
    }

    override suspend fun clear() {
        //
    }

    override fun observeOutput(): Flow<List<LogEntry>> {
        return flow {
            //
        }
    }

    override fun observeStackState(): Flow<List<Int>> {
        return flow {
            //
        }
    }

    override fun observeSourcePosition(): Flow<Pair<Int, SourcePosition>> {
        return flow {
            //
        }
    }

    private external fun loadProgramNative(code: String)
    private external fun startUpNative()
    private external fun executeNextOpNative()


}