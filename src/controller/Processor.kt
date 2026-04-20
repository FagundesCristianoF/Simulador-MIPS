package controller

import entity.InstructionType
import model.Register
import model.Word
import view.GetTcWindow
import javax.swing.JOptionPane

class Processor(
    private val instructions: MutableList<String>,
    private val simulator: MipsSimulator,
) {
    var pc: Int = 0
    var clock: Int = 0
    var forwarding: Boolean = false
    val registers: MutableList<Register> = MutableList(34) { Register() }
    private var fetchDecode: Word = Word.NOOP
    private var decodeAlu: Word = Word.NOOP
    private var aluMemory: Word = Word.NOOP
    private var memoryWriteback: Word = Word.NOOP
    private var writeBack: Word = Word.NOOP
    private var operandFetch: Word = Word.NOOP
    val memory: Array<String?> = arrayOfNulls(1024)
    val binaryInstructions: MutableList<StringBuilder> = mutableListOf()
    private var fetch: Boolean = true
    private var decode: Boolean = true
    private var jump: Boolean = false

    init {
        repeat(5) { instructions.add("end") }
    }

    private fun logLockedRegisters() {
        val locks = registers.indices.filter { registers[it].inUse }
        println(locks)
    }

    fun generateBinary() {
        for (i in instructions.indices) {
            var text = instructions[i]
            text = text.replaceFirst(" ", ",").replace(" ", "")
            val parts = text.split(",").toTypedArray()

            when (parts[0]) {
                "add", "sub", "mult", "div" -> encodeRType(parts)
                "bne", "beq", "lw", "sw", "bltz", "bgtz" -> encodeIType(parts)
                "j", "jr" -> encodeJType2(parts)
                "noop", "get_tc" -> encodeDType(parts)
            }

            parts.forEach { print(" $it") }
            println()
        }
        simulator.writeToDisk()
        binaryInstructions.forEach { println(it) }
    }

    fun nextStep(): MutableList<String> {
        val result = mutableListOf<String>()
        if (pc == instructions.size - 1) {
            JOptionPane.showMessageDialog(null, "End of execution")
        } else {
            writeBackStage()
            memoryStage()
            runAluStage()
            if (!jump) {
                decodeStage()
                fetchInstruction()
            } else {
                pc--
            }

            unlockRegisters(writeBack)
            logLockedRegisters()

            jump = false

            if (fetch) pc++
            clock++

            result.add(fetchDecode.text)
            result.add(decodeAlu.text)
            result.add(aluMemory.text)
            result.add(memoryWriteback.text)
            result.add(writeBack.text)
            result.add(pc.toString())
            result.add(clock.toString())
            result.add(forwarding.toString())
            result.add(registerSnapshot())
        }
        return result
    }

    private fun unlockRegisters(word: Word) {
        registers[word.reg1].inUse = false
        registers[word.reg2].inUse = false
        registers[word.reg3].inUse = false
    }

    private fun registerSnapshot(): String = buildString {
        append("\n")
        registers.forEach { append("  ${it.value}") }
    }

    private fun fetchInstruction() {
        if (forwarding || fetch) {
            fetchDecode.text = instructions[pc].replaceFirst(" ", ",").replace(" ", "")
        }
    }

    private fun decodeStage() {
        if (forwarding) {
            decodeInstruction()
        } else if (decode && fetchDecode.text != "noop") {
            if (fetchDecode.text != "end") {
                decodeInstruction()
            } else {
                decodeAlu.text = "end"
            }
        }
    }

    private fun decodeInstruction() {
        val parts = fetchDecode.text.split(",").toTypedArray()
        when (parts[0]) {
            "add", "sub", "mult", "div" -> {
                fetchDecode.type = InstructionType.TypeR
                fetchDecode.reg1 = parts[1].substring(1).toInt()
                fetchDecode.reg2 = parts[2].substring(1).toInt()
                fetchDecode.reg3 = parts[3].substring(1).toInt()
                fetchDecode.op = parts[0]
            }
            "lw", "sw" -> {
                fetchDecode.type = InstructionType.TypeI
                fetchDecode.op = parts[0]
                fetchDecode.reg1 = parts[1].substring(1).toInt()
                val aux = parts[2].split("(").toTypedArray()
                fetchDecode.reg2 = aux[1].substring(1, aux[1].length - 1).toInt()
                fetchDecode.offset = aux[0].replace(" ", "").toInt()
            }
            "bne", "beq", "bltz", "bgtz" -> {
                fetchDecode.type = InstructionType.TypeI
                fetchDecode.op = parts[0]
                fetchDecode.reg1 = parts[1].substring(1).toInt()
                fetchDecode.reg2 = parts[2].substring(1).toInt()
                fetchDecode.offset = parts[3].toInt()
            }
            "jr", "j" -> {
                fetchDecode.type = InstructionType.TypeJ
                fetchDecode.jumpTarget = parts[1].toInt()
                fetchDecode.op = parts[0]
            }
            "get_tc" -> {
                fetchDecode.type = InstructionType.TypeD
                fetchDecode.temp = parts[1].toInt()
            }
        }
        decodeAlu = fetchDecode
        fetchDecode = Word.NOOP
    }

    private fun lockRegisters(word: Word) {
        registers[word.reg1].inUse = true
        registers[word.reg2].inUse = true
        registers[word.reg3].inUse = true
    }

    private fun runAluStage() {
        if (decodeAlu.text != "end") {
            if (forwarding) {
                executeOperation()
            } else if (
                !registers[decodeAlu.reg1].inUse &&
                !registers[decodeAlu.reg2].inUse &&
                !registers[decodeAlu.reg3].inUse
            ) {
                lockRegisters(decodeAlu)
                executeOperation()
                fetch = true
                decode = true
            } else {
                fetch = false
                decode = false
            }
        } else {
            aluMemory.text = "end"
        }
    }

    private fun applyForwarding(word: Word) {
        when (word.type) {
            InstructionType.TypeI -> {
                if (word.op == "sw") storeWord(word)
                if (word.op == "lw") loadWord(word)
            }
            InstructionType.TypeR -> {
                if (word.text != "noop" && word.text != "end") executeRType(word)
            }
            else -> Unit
        }
    }

    private fun executeOperation() {
        if (decodeAlu.text != "noop") {
            when (decodeAlu.type) {
                InstructionType.TypeI -> {
                    executeIType(decodeAlu)
                    if (forwarding) applyForwarding(decodeAlu)
                }
                InstructionType.TypeJ -> executeJType(decodeAlu)
                InstructionType.TypeR -> if (forwarding) executeRType(decodeAlu)
                InstructionType.TypeD -> handleDType(decodeAlu)
            }
            aluMemory = decodeAlu
            decodeAlu = Word.NOOP
        }
    }

    private fun handleDType(word: Word) {
        GetTcWindow(this, word.temp).isVisible = true
    }

    private fun memoryStage() {
        if (aluMemory.type == InstructionType.TypeI) {
            unlockRegisters(fetchDecode)
            if (aluMemory.op == "sw") storeWord(aluMemory)
        }
        memoryWriteback = aluMemory
        aluMemory = Word.NOOP
    }

    private fun writeBackStage() {
        if (memoryWriteback.type == InstructionType.TypeR &&
            memoryWriteback.text != "noop" &&
            memoryWriteback.text != "end"
        ) {
            executeRType(memoryWriteback)
        }
        if (memoryWriteback.type == InstructionType.TypeI && memoryWriteback.op == "lw") {
            loadWord(memoryWriteback)
        }
        writeBack = memoryWriteback
        memoryWriteback = Word.NOOP
    }

    private fun executeRType(word: Word) {
        val result = when (word.op) {
            "add" -> registers[word.reg2].value + registers[word.reg3].value
            "sub" -> registers[word.reg2].value - registers[word.reg3].value
            "mult" -> registers[word.reg2].value * registers[word.reg3].value
            "div" -> registers[word.reg2].value / registers[word.reg3].value
            else -> return
        }
        registers[word.reg1].value = result
    }

    private fun executeIType(word: Word) {
        when (word.op) {
            "lw" -> word.temp = (memory[word.offset + registers[word.reg2].value] ?: "0").toInt()
            "sw" -> word.temp = registers[word.reg2].value
            "bne" -> {
                unlockRegisters(word)
                if (registers[word.reg1].value != registers[word.reg2].value) {
                    pc = word.offset
                    fetchDecode = Word.NOOP
                    operandFetch = Word.NOOP
                    jump = true
                }
            }
            "beq" -> {
                unlockRegisters(word)
                if (registers[word.reg1].value == registers[word.reg2].value) {
                    pc = word.offset
                    fetchDecode = Word.NOOP
                    operandFetch = Word.NOOP
                    jump = true
                }
            }
            "bltz" -> {
                unlockRegisters(word)
                if (registers[word.reg1].value < 0) {
                    pc = word.offset
                    fetchDecode = Word.NOOP
                    operandFetch = Word.NOOP
                    jump = true
                }
            }
            "bgtz" -> {
                unlockRegisters(word)
                if (registers[word.reg1].value > 0) {
                    pc = word.offset
                    fetchDecode = Word.NOOP
                    operandFetch = Word.NOOP
                    jump = true
                }
            }
        }
    }

    private fun executeJType(word: Word) {
        jump = true
        when (word.op) {
            "j" -> {
                pc = word.offset
                fetchDecode = Word.NOOP
                operandFetch = Word.NOOP
            }
            "jr" -> {
                pc = registers[word.reg1].value
                fetchDecode = Word.NOOP
                operandFetch = Word.NOOP
            }
        }
    }

    private fun loadWord(word: Word) {
        registers[word.reg1].value = word.temp
    }

    private fun storeWord(word: Word) {
        memory[registers[word.reg1].value + word.offset] = word.temp.toString()
    }

    private fun encodeRType(parts: Array<String>) {
        val op = "000000"
        val rs = parts[2].substring(1).toInt().toString(2).padStart(5, '0')
        val rt = parts[3].substring(1).toInt().toString(2).padStart(5, '0')
        val rd = parts[1].substring(1).toInt().toString(2).padStart(5, '0')
        val shamt = "00000"
        val funct = when (parts[0]) {
            "add" -> "100000"
            "sub" -> "100010"
            else -> ""
        }
        binaryInstructions.add(StringBuilder("$op$rs$rt$rd$shamt$funct"))
    }

    private fun encodeJType2(parts: Array<String>) {
        val word = when (parts[0]) {
            "j" -> {
                val target = parts[1].toInt().toString(2).padStart(26, '0')
                StringBuilder("000010$target")
            }
            "jr" -> {
                val reg = parts[1].toInt().toString(2).padStart(26, '0')
                StringBuilder("00000${reg}000000000000000001000")
            }
            else -> return
        }
        binaryInstructions.add(word)
    }

    private fun encodeIType(parts: Array<String>) {
        val r1 = parts[1].substring(1).toInt().toString(2).padStart(5, '0')
        val split = parts[2].split("(").toTypedArray()

        val op: String
        val r2: String
        val offset: String

        when (parts[0]) {
            "sw" -> {
                op = "101011"
                offset = split[0].toInt().toString(2).padStart(16, '0')
                r2 = split[1].substring(1, split[1].length - 1).toInt().toString(2).padStart(5, '0')
            }
            "lw" -> {
                op = "100011"
                offset = split[0].toInt().toString(2).padStart(16, '0')
                r2 = split[1].substring(1, split[1].length - 1).toInt().toString(2).padStart(5, '0')
            }
            "beq" -> {
                op = "000100"
                r2 = parts[2].substring(1).toInt().toString(2).padStart(5, '0')
                offset = parts[3].toInt().toString(2).padStart(16, '0')
            }
            "bne" -> {
                op = "000101"
                r2 = parts[2].substring(1).toInt().toString(2).padStart(5, '0')
                offset = parts[3].toInt().toString(2).padStart(16, '0')
            }
            "bltz" -> {
                op = "000001"
                r2 = parts[2].substring(1).toInt().toString(2).padStart(5, '0')
                offset = parts[3].toInt().toString(2).padStart(16, '0')
            }
            "bgtz" -> {
                op = "000111"
                r2 = parts[2].substring(1).toInt().toString(2).padStart(5, '0')
                offset = parts[3].toInt().toString(2).padStart(16, '0')
            }
            else -> return
        }

        val word = if (parts[0] == "sw" || parts[0] == "lw") {
            StringBuilder("$op$r2$r1$offset")
        } else {
            StringBuilder("$op$r1$r2$offset")
        }
        binaryInstructions.add(word)
    }

    private fun encodeDType(parts: Array<String>) {
        val word = when (parts[0]) {
            "noop" -> StringBuilder("00000000000000000000000000000000")
            "get_tc" -> {
                val addr = parts[1].toInt().toString(2).padStart(10, '0')
                StringBuilder("111111${addr}0000000000000000")
            }
            else -> return
        }
        binaryInstructions.add(word)
    }
}
