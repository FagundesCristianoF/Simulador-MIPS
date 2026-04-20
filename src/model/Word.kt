package model

import entity.InstructionType

data class Word(
    var text: String = "",
    var type: InstructionType = InstructionType.TypeR,
    var reg1: Int = 0,
    var reg2: Int = 0,
    var reg3: Int = 0,
    var op: String = "",
    var offset: Int = 0,
    var address: Int = 0,
    var jumpTarget: Int = 0,
    var temp: Int = 0,
) {
    companion object {
        val NOOP get() = Word(text = "noop")
    }
}
