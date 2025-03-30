package com.nanodalvik.data

sealed class Op(val name: String) {

    class Push(val operand: Int) : Op(OpCodeNames.PUSH.name)
    data object Pop : Op(OpCodeNames.POP.name)
    data object Add : Op(OpCodeNames.ADD.name)
    data object Print : Op(OpCodeNames.PRINT.name)
    data object Halt : Op(OpCodeNames.HALT.name)

}


enum class OpCodeNames {
    PUSH,
    POP,
    ADD,
    PRINT,
    HALT
}