package com.nanodalvik.data

sealed class Op(val name: String) {

    data class Push(val operand: Int) : Op(OpCodeNames.PUSH.name)
    data object Pop : Op(OpCodeNames.POP.name)
    data object Add : Op(OpCodeNames.ADD.name)
    data object Substract : Op(OpCodeNames.SUB.name)
    data object Multiply : Op(OpCodeNames.MUL.name)
    data object Divide : Op(OpCodeNames.DIV.name)
    data object Modulo : Op(OpCodeNames.MOD.name)
    data object Negate : Op(OpCodeNames.NEG.name)
    data object Print : Op(OpCodeNames.PRINT.name)
    data object Halt : Op(OpCodeNames.HALT.name)
    data object Duplicate : Op(OpCodeNames.DUP.name)
    data object Over : Op(OpCodeNames.OVER.name)
    data object Swap : Op(OpCodeNames.SWAP.name)
    data object Drop : Op(OpCodeNames.DROP.name)
    data object ClearMemory : Op(OpCodeNames.CLEARMEM.name)
    data class Jump(var idx: JumpTarget) : Op(OpCodeNames.JMP.name)
    data class JumpNotZero(var idx: JumpTarget) : Op(OpCodeNames.JNZ.name)
    data class JumpZero(var idx: JumpTarget) : Op(OpCodeNames.JZ.name)
    data class Load(val addr: Int) : Op(OpCodeNames.LOAD.name)
    data class Store(val addr: Int) : Op(OpCodeNames.STORE.name)
    data class Call(var addr: JumpTarget) : Op(OpCodeNames.CALL.name)
    data object Return : Op(OpCodeNames.RET.name)
}

enum class OpCodeNames {
    PUSH,
    POP,
    ADD,
    PRINT,
    HALT,
    JMP,
    JNZ,
    JZ,
    SUB,
    MUL,
    DIV,
    MOD,
    NEG,
    SWAP,
    DROP,
    OVER,
    DUP,
    LOAD,
    STORE,
    CLEARMEM,
    CALL,
    RET
}

sealed class JumpTarget {
    data class Label(val name: String) : JumpTarget()
    data class Address(val index: Int) : JumpTarget()
}
