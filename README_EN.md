# MIPS Simulator

This project simulates a MIPS pipeline (5 stages) and generates the binary
encoding for user-provided assembly instructions. The UI lets you load an
instruction file, step through the pipeline, and toggle forwarding.

## Requirements

- JDK 8 or newer
- Gradle (compatible with the Kotlin JVM plugin 2.3.0)

## Build

```bash
gradle build
```

## Run

```bash
gradle run
```

## Usage

1. Click `File` to select a text file with one instruction per line.
2. Click `Start` to open the pipeline window.
3. Use `Next` to advance the pipeline.
4. Use `Forwarding` to enable/disable forwarding.
5. Use `Generate Binary` to output the binary file to `src/Output`.
