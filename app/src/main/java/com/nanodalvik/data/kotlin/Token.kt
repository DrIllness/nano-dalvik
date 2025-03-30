package com.nanodalvik.data.kotlin

sealed class Token {
    data class Identifier(val value: String) : Token()
    data class NumberLiteral(val value: Int) : Token()
}