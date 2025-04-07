package com.nanodalvik

import android.app.Application
import com.nanodalvik.data.NanoDalvikVM
import com.nanodalvik.data.kotlin.ExecutionEngine
import com.nanodalvik.data.kotlin.Lexer
import com.nanodalvik.data.kotlin.LogEntry
import com.nanodalvik.data.kotlin.NanoDalvikVMKotlinImpl
import kotlinx.coroutines.flow.Flow

class NanoDalvikApp: Application() {
    private lateinit var dalvikVM: NanoDalvikVM

    override fun onCreate() {
        super.onCreate()
        initDalvikVMKotlin()
    }

    private fun initDalvikVMKotlin() {
        val lexer = Lexer()
        val executionEngine = ExecutionEngine()
        dalvikVM = NanoDalvikVMKotlinImpl(lexer, executionEngine)
    }

    suspend fun runProgram(code: String) {
        dalvikVM.clear()

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

}