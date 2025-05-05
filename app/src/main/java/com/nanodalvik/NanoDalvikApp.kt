package com.nanodalvik

import android.app.Application
import com.nanodalvik.data.NanoDalvikVM
import com.nanodalvik.data.cpp.NativeNanoDalvikVMImpl
import com.nanodalvik.data.kotlin.ExecutionEngine
import com.nanodalvik.data.kotlin.Lexer
import com.nanodalvik.data.kotlin.LogEntry
import com.nanodalvik.data.kotlin.NanoDalvikVMKotlinImpl
import com.nanodalvik.data.kotlin.SourcePosition
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NanoDalvikApp : Application() {
    private lateinit var dalvikVM: NanoDalvikVM

    override fun onCreate() {
        super.onCreate()
        //initDalvikVMKotlin()
        initDalvikNative()
    }

    private fun initDalvikVMKotlin() {
        val lexer = Lexer()
        val executionEngine = ExecutionEngine()
        dalvikVM = NanoDalvikVMKotlinImpl(lexer, executionEngine)
    }
    private fun initDalvikNative() {
        dalvikVM = NativeNanoDalvikVMImpl()
        dalvikVM.startUp()
    }

    suspend fun runProgram(code: String) {
        dalvikVM.clear()
        dalvikVM.startUp()

        dalvikVM.loadProgram(code)
        dalvikVM.executeProgram()
    }

    suspend fun nextStep(code: String) {
        if (!(dalvikVM.isProgramLoaded())) {
            dalvikVM.loadProgram(code)
        }

        dalvikVM.executeNextOp()
    }

    fun observeOutput(): Flow<List<LogEntry>> = dalvikVM.observeOutput()

    fun observeStackState(): Flow<List<Int>> = dalvikVM.observeStackState()

    fun observeExecutionPosition(): Flow<Pair<Int, SourcePosition>> =
        dalvikVM.observeSourcePosition()

}