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
            processor = Processor(instructions as ArrayList<String>, this)
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
