package com.nanodalvik.data.kotlin

// class to scan raw data and output it into a list of tokens
class Lexer {
    fun tokenize(input: String): List<TokenV2> {
        val tokens = mutableListOf<TokenV2>()
        var currentIndex = 0

        fun outOfBounds(): Boolean {
            return currentIndex >= input.length
        }

        fun currentChar(): Char {
            return input[currentIndex]
        }

        fun isSeparator(c: Char): Boolean {
            return c == ' ' || c == '\n' || c == '\t'
        }

        fun isNewLineChar(c: Char): Boolean {
            return c == '\n'
        }

        fun wordToToken(word: String, currentLine: Int, start: Int, end: Int): TokenV2 {
            return word.toString().toIntOrNull()?.let { l ->
                TokenV2.NumberLiteral(l, SourcePosition(currentLine, start, end))
            } ?: TokenV2.Identifier(
                    word.toString(), SourcePosition(currentLine, start, end)
            )
        }

        var isReading = false
        var readingStartAt = 0
        var readingEndAt = 0
        var currentLine = 0

        var word = StringBuilder()
        var c: Char

        while (!outOfBounds()) {
            c = currentChar()
            if (isSeparator(c)) {
                if (isNewLineChar(c)) currentLine++

                if (isReading) {
                    isReading = false
                    readingEndAt = currentIndex
                    val token =
                        wordToToken(word.toString(), currentLine, readingStartAt, readingEndAt)
                    tokens.add(token)
                    word.clear()
                }

                currentIndex++
                continue
            }

            if (!isReading) {
                isReading = true
                readingStartAt = currentIndex
            }
            word.append(c)
            currentIndex++
        }

        if (isReading) {
            val token = wordToToken(word.toString(), currentLine, readingStartAt, readingEndAt)
            tokens.add(token)
        }

        tokens.add(TokenV2.EOF(SourcePosition(currentLine, currentIndex, currentIndex)))

        return tokens
    }

}