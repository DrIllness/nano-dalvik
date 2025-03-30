package com.nanodalvik.data.kotlin

// class to scan raw data and output it into a list of tokens
class Lexer {

    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val words = input.split(Regex("\\s+"))

        for (word in words) {
            val trimmed = word.trim()
            if (trimmed.isEmpty()) continue

            val intVal = trimmed.toIntOrNull()
            if (intVal != null) {
                tokens.add(Token.NumberLiteral(intVal))
            } else {
                tokens.add(Token.Identifier(trimmed.uppercase()))
            }
        }

        return tokens
    }

}