package com.nanodalvik.data

import kotlinx.coroutines.flow.Flow

interface NanoDalvikVM {

    fun startUp()

    suspend fun execute(code: String, args: List<String>)

    fun shutDown()

    fun observeErrorOutput(): Flow<String>

    fun observeOutput(): Flow<String>
}