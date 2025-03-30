package com.nanodalvik.data.kotlin

class ErrorReporter {
    private val _errors = mutableListOf<String>()
    val errors: List<String> get() = _errors

    fun report(message: String) {
        _errors.add(message)
    }

    fun hasErrors(): Boolean = _errors.isNotEmpty()
}