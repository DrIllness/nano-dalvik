package com.nanodalvik.data

sealed class Op(val name: String) {

    data class Push(val operand: Int) : Op(OpCodeNames.PUSH.name)
    data object Pop : Op(OpCodeNames.POP.name)
    data object Add : Op(OpCodeNames.ADD.name)
    data object Print : Op(OpCodeNames.PRINT.name)
    data object Halt : Op(OpCodeNames.HALT.name)
    data class Jump(val idx: Int) : Op(OpCodeNames.JMP.name)
    data class JumpNotZero(val idx: Int) : Op(OpCodeNames.JNZ.name)
    data class JumpZero(val idx: Int) : Op(OpCodeNames.JZ.name)

}

enum class OpCodeNames {
    PUSH,
    POP,
    ADD,
    PRINT,
    HALT,
    JMP,
    JNZ,
    JZ
}