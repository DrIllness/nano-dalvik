package com.nanodalvik.data.kotlin

import com.nanodalvik.data.Op
import java.util.ArrayDeque
import java.util.Deque

// class to run the Program
class ExecutionEngine {
    private lateinit var stack: Deque<Int>
    private var ip: Int = 0
    private var program: Program? = null

    fun unloadProgram() {
        program = null
    }

    fun loadProgram(program: Program): ExecutionResult? {
        ip = 0
        this.program = program
        stack = ArrayDeque<Int>(STACK_CAPACITY)

        if (program.errorReporter.hasErrors()) {
            return ExecutionResult(
                    errors = program.errorReporter.errors,
                    output = emptyList(),
                    stackState = emptyList()
            )
        }

        return null
    }

    fun hasNextOp(): Boolean = program?.let { p -> ip <= (p.commands.size - 1)} == true

    fun executeNextOp(): ExecutionResult {
        val output = mutableListOf<String>()
        val errors = mutableListOf<String>()
        val op = program!!.commands[ip++] // we make assert for now

        when (op) {
            Op.Add -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.ADD_FAILED)
                } else {
                    val a = stack.pollLast()
                    val b = stack.pollLast()
                    if (a != null && b != null) {
                        stack.add(a + b)
                    } else {
                        errors.add(VMErrors.ADD_FAILED)
                    }
                }
            }

            Op.Halt -> {
                output.add(VMOutput.HALTED)
            }

            Op.Pop -> {
                if (stack.isEmpty()) {
                    errors.add(VMErrors.STACK_UNDERFLOW)
                } else {
                    stack.pollLast()
                }
            }

            Op.Print -> {
                if (stack.isEmpty()) {
                    errors.add(VMErrors.PRINT_FAILED)
                } else {
                    output.add(stack.peek()!!.toString())
                }
            }

            is Op.Push -> {
                if (stack.size < STACK_CAPACITY) {
                    val addResult = runCatching {
                        stack.add(op.operand)
                    }
                    if (addResult.isFailure) {
                        // handle exception here
                        errors.add(addResult.toString())
                    }
                } else {
                    errors.add(VMErrors.STACK_OVERFLOW)
                }
            }
        }

        // we assume that error on this stage breaks execution
        return ExecutionResult(
                errors = errors,
                output = output,
                stackState = stack.toList()
        )
    }

    companion object {

        const val STACK_CAPACITY = 100

    }

}

object VMOutput {
    const val HALTED = "HALTED"
}

object VMErrors {
    const val STACK_UNDERFLOW = "Stack underflow"
    const val STACK_OVERFLOW = "Stack overflowed"
    const val ADD_FAILED = "Not enough values on stack to apply addition"
    const val PRINT_FAILED = "Print failed: stack is empty"
}

class ExecutionResult(
        val errors: List<String>,
        val output: List<String>,
        val stackState: List<Int>
)