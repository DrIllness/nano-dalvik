package com.nanodalvik.data.kotlin

import com.nanodalvik.data.Op

class Parser(private val tokens: List<Token>, private val reporter: ErrorReporter) {
    private var current = 0

    fun parse(): Program {
        val instructions = mutableListOf<Op>()

        while (!isAtEnd()) {
            val token = advance()
            when (token) {
                is Token.Identifier -> {
                    when (token.value) {
                        "PUSH" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(Op.Push(next.value))
                            } else {
                                reporter.report("PUSH requires a numeric operand")
                            }
                        }

                        "POP" -> instructions.add(Op.Pop)
                        "ADD" -> instructions.add(Op.Add)
                        "PRINT" -> instructions.add(Op.Print)
                        "HALT" -> instructions.add(Op.Halt)
                        else -> reporter.report("Unknown instruction: ${token.value}")
                    }
                }

                is Token.NumberLiteral -> {
                    reporter.report("Unexpected number literal: ${token.value}")
                }
            }
        }

        return Program(instructions, reporter)
    }

    private fun isAtEnd(): Boolean = current >= tokens.size
    private fun peek(): Token? = tokens.getOrNull(current)
    private fun advance(): Token = tokens[current++]

}