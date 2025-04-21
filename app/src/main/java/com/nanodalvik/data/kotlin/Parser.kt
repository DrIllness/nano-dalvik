package com.nanodalvik.data.kotlin

import com.nanodalvik.data.Op

class Parser(private val tokens: List<Token>, private val reporter: ErrorReporter) {
    private var current = 0

    fun parse(): Program {
        val instructions = mutableListOf<Pair<Op, SourcePosition>>()

        while (!isAtEnd()) {
            val token = advance()
            when (token) {
                is Token.Identifier -> {
                    when (token.value.uppercase()) {
                        "PUSH" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
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
                        "JMP" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Op.Jump(next.value),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report("JUMP requires a numeric operand")
                            }
                        }
                        "JNZ" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Op.JumpNotZero(next.value),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report("JUMP requires a numeric operand")
                            }
                        }
                        "JZ" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Op.JumpZero(next.value),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report("JUMP requires a numeric operand")
                            }
                        }

                        else -> reporter.report("Unknown instruction: ${token.value}")
                    }
                }

                is Token.NumberLiteral -> {
                    reporter.report("Unexpected number literal: ${token.value}")
                }

                is Token.EOF -> {
                    //
                }
            }
        }

        return Program(instructions, reporter)
    }

    private fun hasNextNumberLiteral() = peek() is Token.NumberLiteral
    private fun isAtEnd(): Boolean = current >= tokens.size
    private fun peek(): Token? = tokens.getOrNull(current)
    private fun advance(): Token = tokens[current++]

}