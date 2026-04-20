# MIPS Simulator — Kotlin Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate all source files to idiomatic Kotlin, add detekt + ktlint, and verify the project builds cleanly.

**Architecture:** Model layer (InstructionType, Register, Word) is refactored first since all other layers depend on it. Controller and view layers follow. Build tooling is set up first so lint runs incrementally.

**Tech Stack:** Kotlin 2.3, Gradle KTS, detekt 1.23.8, ktlint plugin 12.3.0 / engine 1.5.0, Java 17, Swing

---

## File Map

| File | Action | Change |
|---|---|---|
| `build.gradle.kts` | Modify | Add detekt + ktlint plugins, bump JVM 8→17, update mainClass |
| `detekt.yml` | Create | detekt rule config |
| `src/entity/InstructionType.kt` | Modify | Remove `code: Int` field |
| `src/model/Register.kt` | Modify | `data class` |
| `src/model/Word.kt` | Modify | `data class`, `type: InstructionType`, `NOOP` companion |
| `src/controller/MipsSimulator.kt` | Modify | Kotlin IO, remove dead methods, remove `binaryInstructions` field |
| `src/controller/Processor.kt` | Modify | `when`, `MutableList`, `padStart`, remove dead code |
| `src/view/GetTcWindow.kt` | Modify | Single constructor, specific imports, `_` params |
| `src/view/PipelineWindow.kt` | Modify | `val` processor, specific imports, `_` params |
| `src/view/MainWindow.kt` | Modify | Specific imports, remove `@Throws`, `_` params |
| `src/main/Main.kt` | Modify | Top-level `fun main()` |

---

## Task 1: Configure build tooling

**Files:**
- Modify: `build.gradle.kts`
- Create: `detekt.yml`

- [ ] **Step 1: Replace `build.gradle.kts`**

```kotlin
plugins {
    kotlin("jvm") version "2.3.0"
    application
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("main.MainKt")
}

sourceSets {
    main {
        kotlin.srcDirs("src")
        resources.srcDirs("src")
    }
    test {
        kotlin.srcDirs("test")
        resources.srcDirs("test")
    }
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
}

ktlint {
    version.set("1.5.0")
}
```

- [ ] **Step 2: Create `detekt.yml` at project root**

```yaml
complexity:
  LongMethod:
    threshold: 80
  LongParameterList:
    functionThreshold: 8
  NestedBlockDepth:
    threshold: 5
  TooManyFunctions:
    thresholdInClasses: 20

style:
  MagicNumber:
    active: true
    ignoreNumbers: ['-1', '0', '1', '2', '3', '4', '5', '8', '10', '16', '17', '26', '32', '34']
  MaxLineLength:
    maxLineLength: 120
  WildcardImport:
    active: true

naming:
  FunctionNaming:
    active: true
  VariableNaming:
    active: true
```

- [ ] **Step 3: Verify build resolves plugins**

```bash
./gradlew dependencies --configuration detekt
```

Expected: dependency tree printed with no errors.

- [ ] **Step 4: Commit**

```bash
git add build.gradle.kts detekt.yml
git commit -m "chore: add detekt and ktlint, bump JVM to 17"
```

---

## Task 2: Refactor InstructionType

**Files:**
- Modify: `src/entity/InstructionType.kt`

- [ ] **Step 1: Replace file content**

```kotlin
package entity

enum class InstructionType {
    TypeR,
    TypeI,
    TypeJ,
    TypeD,
}
```

Removes `code: Int` — it was only used for index-based lookup `InstructionType.values()[word.type]`. `Word.type` will store `InstructionType` directly (Task 3).

- [ ] **Step 2: Commit**

```bash
git add src/entity/InstructionType.kt
git commit -m "refactor: remove code field from InstructionType"
```

---

## Task 3: Refactor Register and Word

**Files:**
- Modify: `src/model/Register.kt`
- Modify: `src/model/Word.kt`

- [ ] **Step 1: Replace `Register.kt`**

```kotlin
package model

data class Register(var value: Int = 0, var inUse: Boolean = false)
```

- [ ] **Step 2: Replace `Word.kt`**

```kotlin
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
```

`NOOP` is a `get()` property so each access returns a fresh instance — critical because `Word` is mutable and each pipeline stage needs its own instance.

- [ ] **Step 3: Verify compile**

```bash
./gradlew compileKotlin 2>&1 | head -30
```

Expected: errors only in `Processor.kt` and `MipsSimulator.kt` (they still reference old API — fixed in Task 4-5).

- [ ] **Step 4: Commit**

```bash
git add src/model/Register.kt src/model/Word.kt
git commit -m "refactor: convert Register and Word to data classes"
```

---

## Task 4: Refactor MipsSimulator

**Files:**
- Modify: `src/controller/MipsSimulator.kt`

- [ ] **Step 1: Replace file content**

```kotlin
package controller

import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import view.MainWindow

class MipsSimulator {
    val instructions: MutableList<String> = mutableListOf()
    lateinit var processor: Processor
    val mainWindow: MainWindow = MainWindow(this)

    fun readFile() {
        instructions.clear()

        val fileChooser = JFileChooser()
        fileChooser.currentDirectory = File(".")
        val result = fileChooser.showOpenDialog(null)

        if (result == JFileChooser.APPROVE_OPTION) {
            val path = fileChooser.selectedFile.absolutePath
            try {
                instructions.addAll(File(path).readLines())
            } catch (ex: IOException) {
                JOptionPane.showMessageDialog(null, "Error reading file: ${ex.message}")
                return
            }
            processor = Processor(instructions, this)
            mainWindow.setProcessor(processor)
        } else {
            JOptionPane.showMessageDialog(null, "No valid input file selected.")
        }
    }

    fun writeToDisk() {
        val formatter = DateTimeFormatter.ofPattern("H.m.d.M")
        val timestamp = LocalDateTime.now().format(formatter)

        try {
            val outputDir = File("./src/Output/")
            outputDir.mkdirs()
            val file = File(outputDir, "$timestamp.fera")
            file.writeText(processor.binaryInstructions.joinToString("\n"))
        } catch (_: IOException) {
        }
    }
}
```

Key changes:
- `ArrayList` → `MutableList` via `mutableListOf()`
- `BufferedReader` while-loop → `File.readLines()`
- `Calendar` timestamp → `LocalDateTime` + `DateTimeFormatter`
- `BufferedWriter` loop → `file.writeText(...joinToString(...))`
- Removed `binaryInstructions` field (read directly from `processor`)
- Removed dead `printInstructions` / `printBinaryInstructions` methods
- `@Throws` annotation removed

- [ ] **Step 2: Verify compile**

```bash
./gradlew compileKotlin 2>&1 | head -30
```

Expected: errors only in `Processor.kt` (still references old Word/InstructionType API).

- [ ] **Step 3: Commit**

```bash
git add src/controller/MipsSimulator.kt
git commit -m "refactor: idiomatic Kotlin IO and collections in MipsSimulator"
```

---

## Task 5: Refactor Processor — pipeline stages

**Files:**
- Modify: `src/controller/Processor.kt` (lines 1–310 — init through executeOperation)

- [ ] **Step 1: Replace file with complete content**

```kotlin
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
```

Key changes from original:
- `ArrayList` → `MutableList` + `mutableListOf()` / `MutableList(34) { Register() }`
- Removed unused `alu: Boolean` field
- Removed redundant double-init in `init` block; unused `register`/`register2` locals removed
- `repeat(5)` replaces 5 manual `instructions.add("end")` calls
- All nested if-else dispatch → `when`
- `InstructionType.values()[word.type]` → `word.type` (now `InstructionType` directly on `Word`)
- Manual padding loops → `.toString(2).padStart(N, '0')`
- `Integer.toBinaryString(x)` → `x.toString(2)`
- Removed dead `encodeJType` method
- `buildString` for `registerSnapshot`
- `fetchInstruction` simplified: both branches did the same — collapsed to `if (forwarding || fetch)`
- `lw` null fallback changed from `"null"` (crashes on parse) to `"0"` (safe default)

- [ ] **Step 2: Compile check**

```bash
./gradlew compileKotlin 2>&1 | head -40
```

Expected: no errors. View files not yet updated so may warn about unused imports — those are fixed in Task 6-8.

- [ ] **Step 3: Commit**

```bash
git add src/controller/Processor.kt
git commit -m "refactor: idiomatic Kotlin in Processor — when, MutableList, padStart"
```

---

## Task 6: Refactor view — GetTcWindow

**Files:**
- Modify: `src/view/GetTcWindow.kt`

- [ ] **Step 1: Replace file content**

```kotlin
package view

import controller.Processor
import java.awt.event.ActionEvent
import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.LayoutStyle
import javax.swing.WindowConstants

class GetTcWindow(
    private val processor: Processor,
    private val position: Int,
) : JFrame() {
    private lateinit var okButton: JButton
    private lateinit var valueField: JTextField
    private lateinit var valueLabel: JLabel

    init {
        initComponents()
    }

    private fun initComponents() {
        valueField = JTextField()
        valueLabel = JLabel()
        okButton = JButton()

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        valueLabel.text = "Value"
        okButton.text = "OK"
        okButton.addActionListener { _: ActionEvent -> okButtonActionPerformed() }

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(okButton)
                                .addComponent(valueField, GroupLayout.DEFAULT_SIZE, 50, Int.MAX_VALUE)
                                .addComponent(
                                    valueLabel,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    Int.MAX_VALUE,
                                )
                        )
                        .addContainerGap(48, Int.MAX_VALUE)
                )
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(valueLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            valueField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(okButton)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                )
        )

        pack()
        setLocationRelativeTo(null)
    }

    private fun okButtonActionPerformed() {
        processor.memory[position] = valueField.text
        isVisible = false
    }
}
```

Key changes:
- Single primary constructor; no-arg throwing constructor removed
- Specific Swing/AWT imports replacing inline `javax.swing.JButton` etc.
- `@Suppress("UNUSED_PARAMETER")` removed; `_: ActionEvent` replaces named unused param
- Event handler extracted to named method (no inline logic in listener)

- [ ] **Step 2: Commit**

```bash
git add src/view/GetTcWindow.kt
git commit -m "refactor: clean up GetTcWindow — single constructor, specific imports"
```

---

## Task 7: Refactor view — PipelineWindow

**Files:**
- Modify: `src/view/PipelineWindow.kt`

- [ ] **Step 1: Replace file content**

```kotlin
package view

import controller.Processor
import java.awt.Color
import java.awt.event.ActionEvent
import javax.swing.BorderFactory
import javax.swing.GroupLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JToggleButton
import javax.swing.LayoutStyle
import javax.swing.WindowConstants

class PipelineWindow(private val processor: Processor) : JFrame() {
    private lateinit var panel: JPanel
    private lateinit var forwardingToggle: JToggleButton
    private lateinit var nextButton: JButton
    private lateinit var scrollFetch: JScrollPane
    private lateinit var fetchArea: JTextArea
    private lateinit var scrollDecode: JScrollPane
    private lateinit var decodeArea: JTextArea
    private lateinit var scrollAlu: JScrollPane
    private lateinit var aluArea: JTextArea
    private lateinit var scrollMemory: JScrollPane
    private lateinit var memoryArea: JTextArea
    private lateinit var scrollWriteBack: JScrollPane
    private lateinit var writeBackArea: JTextArea
    private lateinit var labelFetch: JLabel
    private lateinit var labelDecode: JLabel
    private lateinit var labelAlu: JLabel
    private lateinit var labelMemory: JLabel
    private lateinit var labelWriteBack: JLabel
    private lateinit var labelClock: JLabel
    private lateinit var labelClockValue: JLabel
    private lateinit var labelPc: JLabel
    private lateinit var labelPcValue: JLabel
    private lateinit var labelForwarding: JLabel
    private lateinit var labelForwardingValue: JLabel
    private lateinit var scrollRegisters: JScrollPane
    private lateinit var registersArea: JTextArea
    private lateinit var labelRegisters: JLabel
    private lateinit var generateBinaryButton: JButton
    private lateinit var closeButton: JButton

    init {
        initComponents()
        isVisible = false
        registersArea.lineWrap = true
        iconImage = ImageIcon("./image/logo.jpg").image
    }

    private fun initComponents() {
        panel = JPanel()
        forwardingToggle = JToggleButton()
        nextButton = JButton()
        scrollFetch = JScrollPane()
        fetchArea = JTextArea()
        scrollDecode = JScrollPane()
        decodeArea = JTextArea()
        scrollAlu = JScrollPane()
        aluArea = JTextArea()
        scrollMemory = JScrollPane()
        memoryArea = JTextArea()
        scrollWriteBack = JScrollPane()
        writeBackArea = JTextArea()
        labelFetch = JLabel()
        labelDecode = JLabel()
        labelAlu = JLabel()
        labelMemory = JLabel()
        labelWriteBack = JLabel()
        labelClock = JLabel()
        labelClockValue = JLabel()
        labelPc = JLabel()
        labelPcValue = JLabel()
        labelForwarding = JLabel()
        labelForwardingValue = JLabel()
        scrollRegisters = JScrollPane()
        registersArea = JTextArea()
        labelRegisters = JLabel()
        generateBinaryButton = JButton()
        closeButton = JButton()

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        title = "MIPS Simulator"
        isUndecorated = true

        panel.border = BorderFactory.createLineBorder(Color(0, 0, 0))

        forwardingToggle.text = "Forwarding"
        forwardingToggle.addActionListener { _: ActionEvent -> forwardingToggleActionPerformed() }

        nextButton.text = "Next"
        nextButton.addActionListener { _: ActionEvent -> nextButtonActionPerformed() }

        fetchArea.columns = 20
        fetchArea.rows = 5
        scrollFetch.setViewportView(fetchArea)

        decodeArea.columns = 20
        decodeArea.rows = 5
        scrollDecode.setViewportView(decodeArea)

        aluArea.columns = 20
        aluArea.rows = 5
        scrollAlu.setViewportView(aluArea)

        memoryArea.columns = 20
        memoryArea.rows = 5
        scrollMemory.setViewportView(memoryArea)

        writeBackArea.columns = 20
        writeBackArea.rows = 5
        scrollWriteBack.setViewportView(writeBackArea)

        labelFetch.text = "Operand Fetch"
        labelDecode.text = "Operand Decode"
        labelAlu.text = "ALU"
        labelMemory.text = "Memory"
        labelWriteBack.text = "Write Back"
        labelClock.text = "Clock:"
        labelPc.text = "PC:"
        labelPcValue.text = " "
        labelForwarding.text = "Forwarding: "
        labelForwardingValue.text = "  "

        registersArea.columns = 20
        registersArea.rows = 5
        scrollRegisters.setViewportView(registersArea)

        labelRegisters.text = "Registers"

        generateBinaryButton.text = "Generate Binary"
        generateBinaryButton.addActionListener { _: ActionEvent -> generateBinaryButtonActionPerformed() }

        closeButton.text = "X"
        closeButton.addActionListener { _: ActionEvent -> closeButtonActionPerformed() }

        val panelLayout = GroupLayout(panel)
        panel.layout = panelLayout
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    panelLayout.createSequentialGroup()
                        .addGroup(
                            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    GroupLayout.Alignment.TRAILING,
                                    panelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(generateBinaryButton)
                                        .addPreferredGap(
                                            LayoutStyle.ComponentPlacement.RELATED,
                                            GroupLayout.DEFAULT_SIZE,
                                            Int.MAX_VALUE,
                                        )
                                        .addComponent(forwardingToggle)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(nextButton)
                                )
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addGap(19, 19, 19)
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING,
                                                false,
                                            )
                                                .addComponent(
                                                    labelFetch,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    Int.MAX_VALUE,
                                                )
                                                .addComponent(scrollFetch)
                                        )
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addComponent(
                                                            scrollDecode,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            GroupLayout.DEFAULT_SIZE,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        )
                                                        .addPreferredGap(
                                                            LayoutStyle.ComponentPlacement.UNRELATED,
                                                        )
                                                        .addComponent(
                                                            scrollAlu,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            GroupLayout.DEFAULT_SIZE,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        )
                                                )
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addComponent(labelDecode)
                                                        .addGap(31, 31, 31)
                                                        .addComponent(
                                                            labelAlu,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            166,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        )
                                                )
                                        )
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                            panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(
                                                    scrollMemory,
                                                    GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.PREFERRED_SIZE,
                                                )
                                                .addComponent(
                                                    labelMemory,
                                                    GroupLayout.PREFERRED_SIZE,
                                                    112,
                                                    GroupLayout.PREFERRED_SIZE,
                                                )
                                        )
                                        .addGroup(
                                            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addPreferredGap(
                                                            LayoutStyle.ComponentPlacement.RELATED,
                                                        )
                                                        .addComponent(
                                                            scrollWriteBack,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            GroupLayout.DEFAULT_SIZE,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        )
                                                )
                                                .addGroup(
                                                    GroupLayout.Alignment.TRAILING,
                                                    panelLayout.createSequentialGroup()
                                                        .addGap(86, 86, 86)
                                                        .addComponent(labelWriteBack)
                                                        .addGap(24, 24, 24)
                                                )
                                        )
                                        .addGap(0, 40, Int.MAX_VALUE)
                                )
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                GroupLayout.Alignment.TRAILING,
                                            )
                                                .addComponent(labelPc)
                                                .addComponent(labelClock)
                                        )
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING,
                                                false,
                                            )
                                                .addComponent(
                                                    labelClockValue,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    Int.MAX_VALUE,
                                                )
                                                .addComponent(
                                                    labelPcValue,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    25,
                                                    Int.MAX_VALUE,
                                                )
                                        )
                                        .addPreferredGap(
                                            LayoutStyle.ComponentPlacement.RELATED,
                                            GroupLayout.DEFAULT_SIZE,
                                            Int.MAX_VALUE,
                                        )
                                        .addComponent(labelForwarding)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                            labelForwardingValue,
                                            GroupLayout.PREFERRED_SIZE,
                                            33,
                                            GroupLayout.PREFERRED_SIZE,
                                        )
                                        .addGap(306, 306, 306)
                                        .addComponent(closeButton)
                                )
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(scrollRegisters)
                                )
                        )
                        .addContainerGap()
                )
                .addGroup(
                    panelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(labelRegisters)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                )
        )
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    GroupLayout.Alignment.TRAILING,
                    panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE,
                                            )
                                                .addComponent(labelForwarding)
                                                .addComponent(labelForwardingValue)
                                        )
                                        .addPreferredGap(
                                            LayoutStyle.ComponentPlacement.RELATED,
                                            GroupLayout.DEFAULT_SIZE,
                                            Int.MAX_VALUE,
                                        )
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING,
                                            )
                                                .addComponent(
                                                    labelWriteBack,
                                                    GroupLayout.Alignment.TRAILING,
                                                )
                                                .addComponent(
                                                    labelMemory,
                                                    GroupLayout.Alignment.TRAILING,
                                                )
                                        )
                                )
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING,
                                            )
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addGap(21, 21, 21)
                                                        .addGroup(
                                                            panelLayout.createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE,
                                                            )
                                                                .addComponent(labelClock)
                                                                .addComponent(
                                                                    labelClockValue,
                                                                    GroupLayout.PREFERRED_SIZE,
                                                                    14,
                                                                    GroupLayout.PREFERRED_SIZE,
                                                                )
                                                        )
                                                        .addGap(18, 18, 18)
                                                        .addGroup(
                                                            panelLayout.createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE,
                                                            )
                                                                .addComponent(labelPc)
                                                                .addComponent(labelPcValue)
                                                        )
                                                )
                                                .addComponent(closeButton)
                                        )
                                        .addGap(107, 107, 107)
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE,
                                            )
                                                .addComponent(labelFetch)
                                                .addComponent(labelDecode)
                                                .addComponent(labelAlu)
                                        )
                                )
                        )
                        .addGap(18, 18, 18)
                        .addGroup(
                            panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(
                                    scrollFetch,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    scrollDecode,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    scrollAlu,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    scrollMemory,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    scrollWriteBack,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                        )
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            80,
                            Int.MAX_VALUE,
                        )
                        .addComponent(labelRegisters)
                        .addGap(18, 18, 18)
                        .addComponent(
                            scrollRegisters,
                            GroupLayout.PREFERRED_SIZE,
                            58,
                            GroupLayout.PREFERRED_SIZE,
                        )
                        .addGap(18, 18, 18)
                        .addGroup(
                            panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(forwardingToggle)
                                .addComponent(nextButton)
                                .addComponent(generateBinaryButton)
                        )
                        .addContainerGap()
                )
        )

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(
                            panel,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            Int.MAX_VALUE,
                        )
                        .addContainerGap()
                )
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(
                            panel,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            Int.MAX_VALUE,
                        )
                        .addContainerGap()
                )
        )

        pack()
        setLocationRelativeTo(null)
    }

    private fun forwardingToggleActionPerformed() {
        processor.forwarding = !processor.forwarding
    }

    private fun nextButtonActionPerformed() {
        val data = processor.nextStep()

        if (data.isNotEmpty()) {
            fetchArea.text = data[0]
            decodeArea.text = data[1]
            aluArea.text = data[2]
            memoryArea.text = data[3]
            writeBackArea.text = data[4]
            labelPcValue.text = data[5]
            labelClockValue.text = data[6]
            labelForwardingValue.text = data[7]
            registersArea.text = data[8]
        }
    }

    private fun generateBinaryButtonActionPerformed() {
        processor.generateBinary()
    }

    private fun closeButtonActionPerformed() {
        System.exit(0)
    }
}
```

Key changes:
- `private var processor` → `private val processor` (never reassigned)
- `private var processor: Processor = processor` redundancy removed
- Field declarations moved to top of class
- Specific imports replacing inline `javax.swing.*`
- `@Suppress("UNUSED_PARAMETER")` removed; `_: ActionEvent` in each listener
- Event handlers extracted to named methods with no parameters

- [ ] **Step 2: Commit**

```bash
git add src/view/PipelineWindow.kt
git commit -m "refactor: clean up PipelineWindow — val processor, specific imports"
```

---

## Task 8: Refactor view — MainWindow

**Files:**
- Modify: `src/view/MainWindow.kt`

- [ ] **Step 1: Replace file content**

```kotlin
package view

import controller.MipsSimulator
import controller.Processor
import java.awt.Color
import java.awt.event.ActionEvent
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.BorderFactory
import javax.swing.GroupLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.LayoutStyle
import javax.swing.WindowConstants

class MainWindow(private val simulator: MipsSimulator) : JFrame() {
    private lateinit var processor: Processor
    private lateinit var pipelineWindow: PipelineWindow
    private lateinit var panel: JPanel
    private lateinit var fileButton: JButton
    private lateinit var startButton: JButton

    init {
        isVisible = true
        initComponents()
        iconImage = ImageIcon("./image/logo.jpg").image
        startButton.isEnabled = false
    }

    fun setProcessor(processor: Processor) {
        this.processor = processor
    }

    private fun initComponents() {
        panel = JPanel()
        fileButton = JButton()
        startButton = JButton()

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        title = "MIPS Simulator"

        panel.border = BorderFactory.createLineBorder(Color(0, 0, 0))

        fileButton.text = "File"
        fileButton.addActionListener { _: ActionEvent -> fileButtonActionPerformed() }

        startButton.text = "Start"
        startButton.addActionListener { _: ActionEvent -> startButtonActionPerformed() }

        val panelLayout = GroupLayout(panel)
        panel.layout = panelLayout
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    panelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(fileButton)
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            323,
                            Int.MAX_VALUE,
                        )
                        .addComponent(startButton)
                        .addContainerGap()
                )
        )
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    panelLayout.createSequentialGroup()
                        .addContainerGap(259, Int.MAX_VALUE)
                        .addGroup(
                            panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(fileButton)
                                .addComponent(startButton)
                        )
                        .addGap(22, 22, 22)
                )
        )

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(
                    panel,
                    GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Int.MAX_VALUE,
                )
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(
                    panel,
                    GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Int.MAX_VALUE,
                )
        )

        accessibleContext.accessibleDescription = ""

        pack()
        setLocationRelativeTo(null)
    }

    private fun fileButtonActionPerformed() {
        try {
            simulator.readFile()
        } catch (ex: IOException) {
            Logger.getLogger(MainWindow::class.java.name).log(Level.SEVERE, null, ex)
        }
        startButton.isEnabled = true
    }

    private fun startButtonActionPerformed() {
        pipelineWindow = PipelineWindow(processor)
        pipelineWindow.isVisible = true
    }
}
```

Key changes:
- Field declarations grouped at top of class
- Specific imports replacing inline `javax.swing.*`
- `@Throws(IOException::class)` removed
- `@Suppress("UNUSED_PARAMETER")` removed; `_: ActionEvent` in each listener
- Event handlers extracted to named no-param methods

- [ ] **Step 2: Compile check**

```bash
./gradlew compileKotlin 2>&1 | head -20
```

Expected: no errors.

- [ ] **Step 3: Commit**

```bash
git add src/view/MainWindow.kt
git commit -m "refactor: clean up MainWindow — specific imports, remove @Throws"
```

---

## Task 9: Refactor entry point

**Files:**
- Modify: `src/main/Main.kt`

- [ ] **Step 1: Replace file content**

```kotlin
package main

import controller.MipsSimulator

fun main() {
    MipsSimulator()
}
```

Replaces `companion object { @JvmStatic fun main(...) }` pattern. Kotlin compiles this to `main.MainKt` which matches `mainClass.set("main.MainKt")` set in Task 1.

- [ ] **Step 2: Full build check**

```bash
./gradlew build 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add src/main/Main.kt
git commit -m "refactor: replace companion object main with top-level fun main"
```

---

## Task 10: Run linters and fix issues

**Files:** Various (fix whatever detekt/ktlint flags)

- [ ] **Step 1: Run ktlint check**

```bash
./gradlew ktlintCheck 2>&1 | head -60
```

Note each file and rule reported.

- [ ] **Step 2: Auto-format with ktlint**

```bash
./gradlew ktlintFormat
```

- [ ] **Step 3: Run detekt**

```bash
./gradlew detekt 2>&1 | head -80
```

Review findings. For any `MagicNumber` findings in Swing layout code (gap sizes like `22`, `259`, etc.), suppress only those specific locations:

```kotlin
@Suppress("MagicNumber")
private fun initComponents() { ... }
```

For `LongMethod` on `initComponents` (Swing layout is unavoidably long), suppress per-method:

```kotlin
@Suppress("LongMethod")
private fun initComponents() { ... }
```

Do NOT suppress `complexity`, `style`, or `naming` findings in non-view files — fix them properly.

- [ ] **Step 4: Run full check**

```bash
./gradlew check 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

```bash
git add -u
git commit -m "style: fix detekt and ktlint findings"
```

---

## Task 11: Final verification

- [ ] **Step 1: Clean build**

```bash
./gradlew clean build 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 2: Verify jar runs**

```bash
./gradlew run 2>&1 | head -5
```

Expected: Swing window opens (or at minimum, no crash before GUI init).

- [ ] **Step 3: Final commit if needed**

```bash
git status
```

If any unstaged changes remain from prior steps, stage and commit them.
