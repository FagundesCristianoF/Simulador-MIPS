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

class MainWindow(
    private val simulator: MipsSimulator,
) : JFrame() {
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
            panelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    panelLayout
                        .createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(fileButton)
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            323,
                            Int.MAX_VALUE,
                        ).addComponent(startButton)
                        .addContainerGap(),
                ),
        )
        panelLayout.setVerticalGroup(
            panelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    panelLayout
                        .createSequentialGroup()
                        .addContainerGap(259, Int.MAX_VALUE)
                        .addGroup(
                            panelLayout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(fileButton)
                                .addComponent(startButton),
                        ).addGap(22, 22, 22),
                ),
        )

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(
                    panel,
                    GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Int.MAX_VALUE,
                ),
        )
        layout.setVerticalGroup(
            layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(
                    panel,
                    GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Int.MAX_VALUE,
                ),
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
