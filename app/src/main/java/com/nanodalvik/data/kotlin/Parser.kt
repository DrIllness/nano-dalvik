package com.nanodalvik.data.kotlin

import com.nanodalvik.data.JumpTarget
import com.nanodalvik.data.Op
import com.nanodalvik.data.Op.*
import com.nanodalvik.data.kotlin.ExecutionEngine.Companion.MEMORY_CAPACITY

class Parser(private val tokens: List<Token>, private val reporter: ErrorReporter) {
    private var current = 0

    fun parse(): Program {
        val instructions = mutableListOf<Pair<Op, SourcePosition>>()
        val labelMap = mutableMapOf<String, Int>()

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
                                                Push(next.value),
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

                        "POP" -> instructions.add(Pair(Pop, token.sourcePosition))
                        "ADD" -> instructions.add(Pair(Add, token.sourcePosition))
                        "SUB" -> instructions.add(Pair(Substract, token.sourcePosition))
                        "MUL" -> instructions.add(Pair(Multiply, token.sourcePosition))
                        "DIV" -> instructions.add(Pair(Divide, token.sourcePosition))
                        "MOD" -> instructions.add(Pair(Modulo, token.sourcePosition))
                        "NEG" -> instructions.add(Pair(Negate, token.sourcePosition))
                        "PRINT" -> instructions.add(Pair(Print, token.sourcePosition))
                        "HALT" -> instructions.add(Pair(Halt, token.sourcePosition))
                        "JMP" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Jump(JumpTarget.Address(next.value)),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else if (next is Token.Label) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Jump(JumpTarget.Label(next.value)),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report("JUMP requires a numeric operand OR label")
                            }
                        }

                        "JNZ" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                JumpNotZero(JumpTarget.Address(next.value)),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else if (next is Token.Label) {
                                advance()
                                instructions.add(
                                        Pair(
                                                JumpNotZero(JumpTarget.Label(next.value)),
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
                                                JumpZero(JumpTarget.Address(next.value)),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else if (next is Token.Label) {
                                advance()
                                instructions.add(
                                        Pair(
                                                JumpZero(JumpTarget.Label(next.value)),
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

                        "DROP" -> {
                            instructions.add(Pair(Drop, token.sourcePosition))
                        }

                        "SWAP" -> {
                            instructions.add(Pair(Swap, token.sourcePosition))
                        }

                        "OVER" -> {
                            instructions.add(Pair(Over, token.sourcePosition))
                        }

                        "DUP" -> {
                            instructions.add(Pair(Duplicate, token.sourcePosition))
                        }

                        "CLEARMEM" -> {
                            instructions.add(Pair(ClearMemory, token.sourcePosition))
                        }

                        "STORE" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Store(next.value),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report(
                                        "STORE requires a numeric operand >= 0 and <= ${MEMORY_CAPACITY}"
                                )
                            }
                        }

                        "LOAD" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Load(next.value),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report(
                                        "LOAD requires a numeric operand >= 0 and <= ${MEMORY_CAPACITY}"
                                )
                            }
                        }

                        "CALL" -> {
                            val next = peek()
                            if (next is Token.NumberLiteral) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Call(JumpTarget.Address(next.value)),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else if (next is Token.Label) {
                                advance()
                                instructions.add(
                                        Pair(
                                                Call(JumpTarget.Label(next.value)),
                                                SourcePosition(
                                                        token.sourcePosition.line,
                                                        token.sourcePosition.tokenStart,
                                                        next.sourcePosition.tokenEnd //OP with operand
                                                )
                                        )
                                )
                            } else {
                                reporter.report(
                                        "CALL requires an address of a function that is being called"
                                )
                            }
                        }

                        "RET" -> instructions.add(Pair(Return, token.sourcePosition))

                        else -> reporter.report("Unknown instruction: ${token.value}")
                    }
                }

                is Token.NumberLiteral -> {
                    reporter.report("Unexpected number literal: ${token.value}")
                }

                is Token.EOF -> {
                    //
                }

                is Token.Label -> {
                    val next = peek()
                    if (next is Token.Identifier) {
                        labelMap[token.value] = instructions.size
                    } else {
                        reporter.report("Labels must precede identifier")
                    }
                }
            }
        }

        for (instruction in instructions) {
            val op = instruction.first
            when (op) {
                is Jump -> {
                    if (op.idx is JumpTarget.Address) {
                        continue
                    }

                    if (op.idx is JumpTarget.Label) {
                        val name = (op.idx as? JumpTarget.Label)?.name
                        val idx = labelMap[name]
                        if (idx == null) {
                            reporter.report(
                                    "Unresolved label ${name} at line:${instruction.second.line}"
                            )
                            continue
                        } else {
                            op.idx = JumpTarget.Address(idx)
                        }
                    }
                }

                is JumpNotZero -> {
                    if (op.idx is JumpTarget.Address) {
                        continue
                    }

                    if (op.idx is JumpTarget.Label) {
                        val name = (op.idx as? JumpTarget.Label)?.name
                        val idx = labelMap[name]
                        if (idx == null) {
                            reporter.report(
                                    "Unresolved label ${name} at line:${instruction.second.line}"
                            )
                            continue
                        } else {
                            op.idx = JumpTarget.Address(idx)
                        }
                    }
                }

                is JumpZero -> {
                    if (op.idx is JumpTarget.Address) {
                        continue
                    }

                    if (op.idx is JumpTarget.Label) {
                        val name = (op.idx as? JumpTarget.Label)?.name
                        val idx = labelMap[name]
                        if (idx == null) {
                            reporter.report(
                                    "Unresolved label ${name} at line:${instruction.second.line}"
                            )
                            continue
                        } else {
                            op.idx = JumpTarget.Address(idx)
                        }
                    }
                }

                is Call -> {
                    if (op.addr is JumpTarget.Address) {
                        continue
                    }

                    if (op.addr is JumpTarget.Label) {
                        val name = (op.addr as? JumpTarget.Label)?.name
                        val idx = labelMap[name]
                        if (idx == null) {
                            reporter.report(
                                    "Unresolved label ${name} at line:${instruction.second.line}"
                            )
                            continue
                        } else {
                            op.addr = JumpTarget.Address(idx)
                        }
                    }
                }

                else -> Unit
            }
        }

        return Program(instructions, reporter)
    }

    private fun isAtEnd(): Boolean = current >= tokens.size
    private fun peek(): Token? = tokens.getOrNull(current)
    private fun advance(): Token = tokens[current++]

}