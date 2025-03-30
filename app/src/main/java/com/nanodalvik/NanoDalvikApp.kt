package com.nanodalvik

import android.app.Application
import com.nanodalvik.data.NanoDalvikVM
import com.nanodalvik.data.kotlin.ExecutionEngine
import com.nanodalvik.data.kotlin.Lexer
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

    suspend fun run(code: String) {
        dalvikVM.execute(code, emptyList())
    }

    fun observeOutput(): Flow<String> = dalvikVM.observeOutput()

    fun observerErrors(): Flow<String> = dalvikVM.observeErrorOutput()

}