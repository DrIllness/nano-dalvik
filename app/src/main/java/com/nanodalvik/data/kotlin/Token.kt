package com.nanodalvik.data.kotlin

sealed class TokenV2(
        val sourcePosition: SourcePosition
) {
    class Identifier(val value: String, pos: SourcePosition) : TokenV2(pos)
    class NumberLiteral(val value: Int, pos: SourcePosition) : TokenV2(pos)
    class EOF(pos: SourcePosition): TokenV2(pos)
}

sealed class Token() {
    data class Identifier(val value: String) : Token()
    data class NumberLiteral(val value: Int) : Token()
}