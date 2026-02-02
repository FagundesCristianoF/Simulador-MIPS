package view

import controller.MipsSimulator
import controller.Processor
import java.awt.Color
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.*

class UiComponent(
    private val mipsSimulator: MipsSimulator,
) : JFrame() {
    private lateinit var processor: Processor
    private var pipelining: PipeLining? = null

    private fun initComponents() {
        jPanel = JPanel()
        fileButton = JButton()
        startButton = JButton()

        defaultCloseOperation = EXIT_ON_CLOSE
        title = "MIPS Simulator"

        jPanel?.border = BorderFactory.createLineBorder(Color(0, 0, 0))

        fileButton?.text = "File"
        fileButton?.addActionListener { _ -> fileButtonClicked() }

        startButton?.text = "Start"
        startButton?.addActionListener { _ -> btnStartActionPerformed() }

        val jPanel1Layout = GroupLayout(jPanel)
        jPanel?.layout = jPanel1Layout
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(fileButton)
                        .addComponent(startButton)
                        .addContainerGap(),
                ),
        )
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    jPanel1Layout.createSequentialGroup()
                        .addContainerGap(259, Short.MAX_VALUE.toInt())
                        .addGroup(
                            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(fileButton)
                                .addComponent(startButton),
                        )
                        .addGap(22, 22, 22),
                ),
        )

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(
                    jPanel,
                    GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE.toInt(),
                ),
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(
                    jPanel,
                    GroupLayout.Alignment.TRAILING,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE.toInt(),
                ),
        )

        getAccessibleContext().accessibleDescription = ""

        pack()
        setLocationRelativeTo(null)
    }

    private fun fileButtonClicked() {
        try {
            mipsSimulator.readFiles()
        } catch (ex: IOException) {
            Logger.getLogger(UiComponent::class.java.name).log(Level.SEVERE, null, ex)
        }
        startButton?.isEnabled = true
    }

    private fun btnStartActionPerformed() {
        pipelining = PipeLining(processor)
        pipelining?.isVisible = true
    }

    fun start(xeon: Processor) {
        processor = xeon
    }

    private var fileButton: JButton? = null
    private var startButton: JButton? = null
    private var jPanel: JPanel? = null

    init {
        isVisible = true
        initComponents()
        iconImage = ImageIcon("./image/logo.jpg").image
        startButton?.isEnabled = false
    }
}
