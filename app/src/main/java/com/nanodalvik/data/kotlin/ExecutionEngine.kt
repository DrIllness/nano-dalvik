package com.nanodalvik.data.kotlin

import com.nanodalvik.data.Op
import java.util.ArrayDeque
import java.util.Deque

// class to run the Program
class ExecutionEngine {
    private lateinit var stack: Deque<Int>

    fun execute(program: Program): ExecutionResult {
        stack = ArrayDeque<Int>(STACK_CAPACITY)


        if (program.errorReporter.hasErrors()) {
            return ExecutionResult(
                    errors = program.errorReporter.errors,
                    output = emptyList()
            )
        }

        val output = mutableListOf<String>()
        val errors = mutableListOf<String>()
        for (op in program.commands) {
            when (op) {
                Op.Add -> {
                    if (stack.size < 2) {
                        errors.add(VMErrors.ADD_FAILED)
                        break
                    }
                    val a = stack.pollLast()
                    val b = stack.pollLast()
                    if (a != null && b != null) {
                        stack.push(a + b)
                    } else {
                        errors.add(VMErrors.ADD_FAILED)
                    }
                }

                Op.Halt -> {
                    output.add(VMOutput.HALTED)
                    break
                }

                Op.Pop -> {
                    if (stack.isEmpty()) {
                        errors.add(VMErrors.STACK_UNDERFLOW)
                        break
                    }

                    stack.pollLast()
                }

                Op.Print -> {
                    if (stack.isEmpty()) {
                        errors.add(VMErrors.PRINT_FAILED)
                        break
                    }
                    output.add(stack.peek()!!.toString())
                }

                is Op.Push -> {
                    if (stack.size < STACK_CAPACITY) {
                        val addResult = runCatching {
                            stack.push(op.operand)
                        }
                        if (addResult.isFailure) {
                            // handle exception here
                            errors.add(addResult.toString())
                        }
                    } else {
                        errors.add(VMErrors.STACK_OVERFLOW)
                        break
                    }
                }
            }
        }

        // we assume that error on this stage breaks execution
        return ExecutionResult(
                errors = errors,
                output = output
        )
    }

    companion object {

        const val STACK_CAPACITY = 100

    }

}

object VMOutput {
    const val HALTED = "Program halted"
}

object VMErrors {
    const val STACK_UNDERFLOW = "Stack underflow"
    const val STACK_OVERFLOW = "Stack overflowed"
    const val ADD_FAILED = "Not enough values on stack to apply addition"
    const val PRINT_FAILED = "Print failed: stack is empty"
}

class ExecutionResult(
        val errors: List<String>,
        val output: List<String>
)