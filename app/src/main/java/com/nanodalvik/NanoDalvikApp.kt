package com.nanodalvik

import android.app.Application
import com.nanodalvik.data.Mode
import com.nanodalvik.data.NanoDalvikVM
import com.nanodalvik.data.cpp.NativeNanoDalvikVMImpl
import com.nanodalvik.data.kotlin.ExecutionEngine
import com.nanodalvik.data.kotlin.Lexer
import com.nanodalvik.data.kotlin.LogEntry
import com.nanodalvik.data.kotlin.NanoDalvikVMKotlinImpl
import com.nanodalvik.data.kotlin.SourcePosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class NanoDalvikApp : Application() {
    private lateinit var kotlinDalvikVM: NanoDalvikVM
    private lateinit var nativeDalvikVM: NanoDalvikVM
    private var _currentVMMode = Mode.NATIVE
    val currentMode: Mode
        get() = _currentVMMode

    val currentVM: NanoDalvikVM
        get() = if (_currentVMMode == Mode.KOTLIN) kotlinDalvikVM else nativeDalvikVM

    override fun onCreate() {
        super.onCreate()
        initDalvikVMKotlin()
        initDalvikNative()
    }

    suspend fun switchVMMode() {
        currentVM.clear()
        _currentVMMode = if (_currentVMMode == Mode.KOTLIN) Mode.NATIVE else Mode.KOTLIN
    }

    private fun initDalvikVMKotlin() {
        val lexer = Lexer()
        val executionEngine = ExecutionEngine()
        kotlinDalvikVM = NanoDalvikVMKotlinImpl(lexer, executionEngine)
    }

    private fun initDalvikNative() {
        nativeDalvikVM = NativeNanoDalvikVMImpl()
        nativeDalvikVM.startUp()
    }

    suspend fun runProgram(code: String) {
        currentVM.clear()
        currentVM.startUp()

        currentVM.loadProgram(code)
        currentVM.executeProgram()
    }

    suspend fun nextStep(code: String) {
        if (!(currentVM.isProgramLoaded())) {
            currentVM.loadProgram(code)
        }

        currentVM.executeNextOp()
    }

    fun observeOutput(): Flow<List<LogEntry>> =
        merge(kotlinDalvikVM.observeOutput(), nativeDalvikVM.observeOutput())

    fun observeStackState(): Flow<List<Int>> = merge(
            kotlinDalvikVM.observeStackState(),
            nativeDalvikVM.observeStackState()
    )

    fun observeExecutionPosition(): Flow<Pair<Int, SourcePosition>> =
        merge(kotlinDalvikVM.observeSourcePosition(), nativeDalvikVM.observeSourcePosition())

}