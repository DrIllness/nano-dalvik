package com.nanodalvik.data.kotlin

import com.nanodalvik.data.Op

data class Program(
        val commands: List<Op>,
        val errorReporter: ErrorReporter
)