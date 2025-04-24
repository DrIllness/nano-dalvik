package com.nanodalvik.data.kotlin

sealed class Token(
        val sourcePosition: SourcePosition
) {
    class Identifier(val value: String, pos: SourcePosition) : Token(pos)
    class Label(val value: String, pos: SourcePosition) : Token(pos)
    class NumberLiteral(val value: Int, pos: SourcePosition) : Token(pos)
    class EOF(pos: SourcePosition): Token(pos)
}