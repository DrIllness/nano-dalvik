package com.nanodalvik.data

import com.nanodalvik.data.kotlin.LogEntry
import kotlinx.coroutines.flow.Flow

interface NanoDalvikVM {

    fun startUp()

    suspend fun execute(code: String, args: List<String>)

    fun shutDown()

    fun observeOutput(): Flow<List<LogEntry>>

    fun observeStackState(): Flow<List<Int>>
}