package com.nanodalvik.data.kotlin

sealed class LogEntry(val str: String) {
    class ErrorLogEntry(err: String) : LogEntry(err)
    class OutputLogEntry(output: String) : LogEntry(output)
}