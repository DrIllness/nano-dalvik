package com.nanodalvik.data.kotlin

import com.nanodalvik.data.JumpTarget
import com.nanodalvik.data.Op
import java.util.ArrayDeque
import java.util.Deque

// class to run the Program
class ExecutionEngine {
    private lateinit var stack: Deque<Int>
    private lateinit var callStack: Deque<Int>
    private var ip: Int = 0
    private var program: Program? = null
    private var memory = IntArray(MEMORY_CAPACITY)
    private var state = State.IDLE

    fun unloadProgram() {
        program = null
        state = State.IDLE
    }

    fun loadProgram(program: Program): ExecutionResult? {
        ip = 0
        this.program = program
        stack = ArrayDeque<Int>(STACK_CAPACITY)
        callStack = ArrayDeque<Int>(STACK_CAPACITY)

        if (program.errorReporter.hasErrors()) {
            return ExecutionResult(
                    errors = program.errorReporter.errors,
                    output = emptyList(),
                    stackState = emptyList(),
                    ipCounter = 0,
                    sourcePosition = SourcePosition(0, 0, 0),
                    nextOpSourcePosition = SourcePosition(0, 0, 0)
            )
        }

        return null
    }

    fun hasNextOp(): Boolean =
        (program?.let { p -> ip <= (p.commands.size - 1) } == true) && (state != State.HALTED)

    fun executeNextOp(): ExecutionResult {
        state = State.RUNNING

        val output = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val opWithLine = program!!.commands[ip++] // we make assert for now
        val op = opWithLine.first
        when (op) {
            Op.Add -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.ADD_FAILED)
                } else {
                    val b = stack.pollLast()
                    val a = stack.pollLast()
                    if (a != null && b != null) {
                        stack.add(a + b)
                    } else {
                        errors.add(VMErrors.ADD_FAILED)
                    }
                }
            }

            Op.Halt -> {
                state = State.HALTED
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
                    output.add(stack.peekLast()!!.toString())
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

            is Op.Jump -> {
                val idx = (op.idx as JumpTarget.Address).index
                if (idx < program!!.commands.size && idx >= 0)
                    ip = idx
                else
                    errors.add(VMErrors.INSTRUCTION_POINTER_OUT_OF_BOUNDS)
            }

            is Op.JumpNotZero -> {
                val idx = (op.idx as JumpTarget.Address).index
                if (stack.isEmpty()) {
                    errors.add(VMErrors.JMP_COMPARISON_FAILED)
                } else {
                    if (stack.peekLast() != 0) {
                        ip = idx
                    }
                }
            }

            is Op.JumpZero -> {
                val idx = (op.idx as JumpTarget.Address).index
                if (stack.isEmpty()) {
                    errors.add(VMErrors.JMP_COMPARISON_FAILED)
                } else {
                    if (stack.peekLast() == 0) {
                        ip = idx
                    }
                }
            }

            Op.Divide -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.DIV_FAILED)
                } else {
                    val b = stack.pollLast()
                    val a = stack.pollLast()
                    if (a != null && b != null) {
                        if (b != 0) {
                            stack.add(a / b)
                        } else {
                            errors.add(VMErrors.DIVIDE_BY_ZERO_ATTEMPT)
                        }

                    } else {
                        errors.add(VMErrors.ADD_FAILED)
                    }
                }
            }

            Op.Modulo -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.MOD_FAILED)
                } else {
                    val b = stack.pollLast()
                    val a = stack.pollLast()
                    if (a != null && b != null) {
                        if (b != 0) {
                            stack.add(a % b)
                        } else {
                            errors.add(VMErrors.DIVIDE_BY_ZERO_ATTEMPT)
                        }

                    } else {
                        errors.add(VMErrors.MOD_FAILED)
                    }
                }
            }

            Op.Multiply -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.MUL_FAILED)
                } else {
                    val b = stack.pollLast()
                    val a = stack.pollLast()
                    if (a != null && b != null) {
                        stack.add(a * b)
                    } else {
                        errors.add(VMErrors.MUL_FAILED)
                    }
                }
            }

            Op.Negate -> {
                val a = stack.pollLast()
                if (a != null) {
                    stack.add(-a)
                } else {
                    errors.add(VMErrors.NEG_FAILED)
                }
            }

            Op.Substract -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.SUB_FAILED)
                } else {
                    val b = stack.pollLast()
                    val a = stack.pollLast()
                    if (a != null && b != null) {
                        stack.add(a - b)
                    } else {
                        errors.add(VMErrors.SUB_FAILED)
                    }
                }
            }

            Op.Drop -> {
                if (stack.isNotEmpty()) {
                    stack.pollLast()
                }
            }

            Op.Duplicate -> {
                if (stack.isNotEmpty()) {
                    stack.peekLast()?.let { last ->
                        stack.add(last)
                    }
                } else {
                    errors.add(VMErrors.STACK_UNDERFLOW)
                }
            }

            Op.Over -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.STACK_UNDERFLOW)
                } else {
                    val elem = stack.elementAt(stack.size - 2)
                    stack.add(elem)
                }
            }

            Op.Swap -> {
                if (stack.size < 2) {
                    errors.add(VMErrors.STACK_UNDERFLOW)
                } else {
                    val elemB = stack.pollLast()
                    val elemA = stack.pollLast()

                    stack.add(elemB)
                    stack.add(elemA)
                }
            }

            Op.ClearMemory -> {
                memory = IntArray(Int.MAX_VALUE)
            }

            is Op.Load -> {
                stack.add(memory[op.addr])
            }

            is Op.Store -> {
                if (stack.isEmpty()) {
                    errors.add(VMErrors.STACK_UNDERFLOW)
                } else {
                    stack.peekLast()?.let { elem ->
                        memory[op.addr] = elem
                    }
                }
            }

            is Op.Call -> {
                val idx = (op.addr as JumpTarget.Address).index
                if (idx in 0..(program!!.commands.size - 1)) {
                    callStack.add(ip)
                    ip = idx
                } else {
                    errors.add(VMErrors.INVALID_FUNC_ADDRESS)
                    state = State.HALTED
                }
            }
            Op.Return -> {
                if (callStack.isEmpty()) {
                    errors.add(VMErrors.RET_CALLED_WITH_EMPTY_CALL_STACK)
                    state = State.HALTED
                } else {
                    callStack.pollLast()?.let { poppedIP ->
                        ip = poppedIP
                    }
                }
            }
        }

        var nextOpWithLine =
            if (hasNextOp()) program!!.commands[ip] else null  // need for step-by-step
        // we assume that error on this stage breaks execution
        return ExecutionResult(
                errors = errors,
                output = output,
                sourcePosition = opWithLine.second,
                nextOpSourcePosition = nextOpWithLine?.second
                    ?: opWithLine.second, // pass same position
                ipCounter = ip,
                stackState = stack.toList()
        )
    }

    companion object {

        const val STACK_CAPACITY = 100
        const val MEMORY_CAPACITY = 1000

    }

}

enum class State {
    HALTED,
    RUNNING,
    IDLE
}

object VMOutput {
    const val HALTED = "HALTED"
}

object VMErrors {
    const val STACK_UNDERFLOW = "Stack underflow"
    const val STACK_OVERFLOW = "Stack overflowed"
    const val INSTRUCTION_POINTER_OUT_OF_BOUNDS = "Instruction pointer out of bounds"
    const val RET_CALLED_WITH_EMPTY_CALL_STACK = "RET called with empty call stack. Halting"
    const val ADD_FAILED = "Not enough values on stack to apply addition"
    const val SUB_FAILED = "Not enough values on stack to apply substraction"
    const val MUL_FAILED = "Not enough values on stack to apply multiplication"
    const val NEG_FAILED = "Not enough values on stack to apply negate"
    const val MOD_FAILED = "Not enough values on stack to apply modulo"
    const val DIV_FAILED = "Not enough values on stack to apply division"
    const val DIVIDE_BY_ZERO_ATTEMPT = "Attempting to divide by zero"
    const val PRINT_FAILED = "Print failed: stack is empty"
    const val INVALID_FUNC_ADDRESS = "CALL used with invalid address. Halting"
    const val JMP_COMPARISON_FAILED = "Comparison before jump failed: stack is empty"
}

class ExecutionResult(
        val errors: List<String>,
        val output: List<String>,
        val ipCounter: Int,
        val sourcePosition: SourcePosition,
        val nextOpSourcePosition: SourcePosition,
        val stackState: List<Int>
)