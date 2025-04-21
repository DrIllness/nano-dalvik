package com.nanodalvik.data.kotlin

import com.nanodalvik.data.Op

class Parser(private val tokens: List<TokenV2>, private val reporter: ErrorReporter) {
    private var current = 0

    fun parse(): Program {
        val instructions = mutableListOf<Pair<Op, SourcePosition>>()

        while (!isAtEnd()) {
            val token = advance()
            when (token) {
                is TokenV2.Identifier -> {
                    when (token.value.uppercase()) {
                        "PUSH" -> {
                            val next = peek()
                            if (next is TokenV2.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Op.Push(next.value),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report("PUSH requires a numeric operand")
                            }
                        }

                        "POP" -> instructions.add(Pair(Op.Pop, token.sourcePosition))
                        "ADD" -> instructions.add(Pair(Op.Add, token.sourcePosition))
                        "PRINT" -> instructions.add(Pair(Op.Print, token.sourcePosition))
                        "HALT" -> instructions.add(Pair(Op.Halt, token.sourcePosition))
                        else -> reporter.report("Unknown instruction: ${token.value}")
                    }
                }

                is TokenV2.NumberLiteral -> {
                    reporter.report("Unexpected number literal: ${token.value}")
                }

                is TokenV2.EOF -> {
                    //
                }
            }
        }

        return Program(instructions, reporter)
    }

    private fun isAtEnd(): Boolean = current >= tokens.size
    private fun peek(): TokenV2? = tokens.getOrNull(current)
    private fun advance(): TokenV2 = tokens[current++]

}