# MIPS Simulator — Kotlin Migration Design

**Date:** 2026-04-20  
**Scope:** Deep idiomatic Kotlin refactor of all source files + detekt/ktlint tooling

---

## Overview

All source files already exist as Kotlin (Java deleted). The current Kotlin is a direct Java translation — not idiomatic. This design covers making the code fully idiomatic Kotlin while preserving all logic, and adding detekt + ktlint to enforce standards.

---

## 1. Entity / Model Layer

### `InstructionType` (`src/entity/InstructionType.kt`)
- Remove `code: Int` field — it existed only for index-based lookup
- Replace all `InstructionType.values()[word.type]` call sites with direct enum value on `Word`
- Use `entries` instead of `values()` where iteration is needed

### `Register` (`src/model/Register.kt`)
- Convert to `data class Register(var value: Int = 0, var inUse: Boolean = false)`
- No behavior changes

### `Word` (`src/model/Word.kt`)
- Convert to `data class Word`
- Change `type: Int` → `type: InstructionType = InstructionType.TypeR`
- Remove `setType(InstructionType)` — assign `word.type = InstructionType.TypeX` directly
- Replace `clone()` / `Cloneable` with `copy()` (data class built-in)
- Add `companion object { val NOOP = Word(text = "noop") }` for pipeline resets
- All `var` fields remain mutable — pipeline mutation pattern still works

---

## 2. Controller Layer

### `Processor` (`src/controller/Processor.kt`)

**Collections:**
- All `ArrayList<X>` → `MutableList<X>` via `mutableListOf()`

**Instruction dispatch:**
- All nested if-else chains in `generateBinary`, `decodeInstruction`, `executeOperation`, `executeIType`, `executeJType`, `executeRType` → `when` expressions

**Binary encoding:**
- Manual `while (length < N) { insert(0, "0") }` loops → `padStart(N, '0')`
- `Integer.toBinaryString(x)` → `x.toString(2)`

**Dead code removal:**
- Remove `encodeJType` (unreachable — `encodeJType2` is the live implementation)
- Remove unused `register` / `register2` local variables in `init`
- Remove redundant double-initialization of `registers`, `memory`, pipeline `Word` fields in `init`

**Logging:**
- `logLockedRegisters`: replace manual accumulation loop with `registers.indices.filter { registers[it].inUse }`

**Type usage:**
- `word.type` is now `InstructionType` enum — all `InstructionType.values()[word.type]` comparisons become direct enum comparisons

### `MipsSimulator` (`src/controller/MipsSimulator.kt`)

- File reading: `File(path).readLines()` replaces `BufferedReader` while-loop
- `writeToDisk`: `file.writeText(binaryInstructions.joinToString("\n"))` replaces `BufferedWriter` loop
- Timestamp: `LocalDateTime.now()` + `DateTimeFormatter` replaces `Calendar.getInstance()`
- Remove dead debug methods `printInstructions` / `printBinaryInstructions` (never called from UI)
- `binaryInstructions` field on `MipsSimulator` removed — `writeToDisk` reads directly from `processor.binaryInstructions`

---

## 3. View Layer

### All view files
- Remove `@Suppress("UNUSED_PARAMETER")` — use `_` for unused lambda parameters: `addActionListener { _ -> ... }`
- Import specific Swing/AWT classes at top instead of inline `javax.swing.JButton` references
- Remove `@Throws(IOException::class)` (not needed in Kotlin)

### `GetTcWindow` (`src/view/GetTcWindow.kt`)
- Remove no-arg constructor that throws `UnsupportedOperationException`
- Use single primary constructor: `class GetTcWindow(private val processor: Processor, private val position: Int) : JFrame()`

### `PipelineWindow` (`src/view/PipelineWindow.kt`)
- `private var processor: Processor = processor` → `private val processor: Processor` as constructor param

### `MainWindow` (`src/view/MainWindow.kt`)
- Remove `@Throws` annotation
- Group field declarations at top of class

### Swing `GroupLayout` verbosity
- Kept as-is — generated layout code, restructuring risks layout regressions with zero readability benefit

---

## 4. Entry Point

### `Main` (`src/main/Main.kt`)
- Replace `companion object { @JvmStatic fun main(...) }` pattern with top-level `fun main(args: Array<String>)` function
- `mainClass` in `build.gradle.kts` updated to `MainKt` (Kotlin top-level main convention)

---

## 5. Build & Tooling

### JVM target
- Bump from 8 → 17 (needed for `LocalDateTime`; Swing compatible)

### ktlint
- Plugin: `org.jlleitschuh.gradle.ktlint` latest stable
- Rules: 4-space indent, import ordering, no wildcard imports, trailing commas
- Wired into `check` task

### detekt
- Plugin: `io.gitlab.arturbosch.detekt` latest stable
- Config: `detekt.yml` at project root
- Rules enabled: `complexity` (function length, nesting depth), `style` (no magic numbers, naming conventions), `potential-bugs`
- Wired into `check` task

---

## Constraints

- All pipeline logic preserved exactly — no algorithmic changes to fetch/decode/ALU/memory/writeback stages
- Forwarding and stall logic unchanged
- Binary encoding output format unchanged
- Swing UI layout preserved pixel-for-pixel
