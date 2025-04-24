package com.nanodalvik.data.kotlin

// class to scan raw data and output it into a list of tokens
class Lexer {
    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
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

        fun wordToToken(word: String, currentLine: Int, start: Int, end: Int): Token {
            return word.toString().toIntOrNull()?.let { l ->
                Token.NumberLiteral(l, SourcePosition(currentLine, start, end))
            } ?: word.toString().let { w ->
                if (w.startsWith(":") && w.length > 1) {
                    Token.Label(
                            w.drop(1), SourcePosition(currentLine, start, end)
                    )
                } else {
                    Token.Identifier(
                            w, SourcePosition(currentLine, start, end)
                    )
                }
            }
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

        tokens.add(Token.EOF(SourcePosition(currentLine, currentIndex, currentIndex)))

        return tokens
    }

}