package com.nanodalvik.data

import com.nanodalvik.data.kotlin.LogEntry
import kotlinx.coroutines.flow.Flow

interface NanoDalvikVM {

    fun startUp()

    fun isProgramLoaded(): Boolean

    fun loadProgram(code: String)

    suspend fun executeProgram()

    suspend fun executeNextOp()

    suspend fun clear()

    fun observeOutput(): Flow<List<LogEntry>>

    fun observeStackState(): Flow<List<Int>>
}