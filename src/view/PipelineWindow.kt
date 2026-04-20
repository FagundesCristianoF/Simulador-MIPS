package view

import controller.Processor
import java.util.ArrayList
import javax.swing.ImageIcon

class PipelineWindow(processor: Processor) : javax.swing.JFrame() {
    private var processor: Processor = processor

    init {
        initComponents()
        isVisible = false
        registersArea.lineWrap = true
        iconImage = ImageIcon("./image/logo.jpg").image
    }

    @Suppress("UNUSED_PARAMETER")
    private fun initComponents() {
        panel = javax.swing.JPanel()
        forwardingToggle = javax.swing.JToggleButton()
        nextButton = javax.swing.JButton()
        scrollFetch = javax.swing.JScrollPane()
        fetchArea = javax.swing.JTextArea()
        scrollDecode = javax.swing.JScrollPane()
        decodeArea = javax.swing.JTextArea()
        scrollAlu = javax.swing.JScrollPane()
        aluArea = javax.swing.JTextArea()
        scrollMemory = javax.swing.JScrollPane()
        memoryArea = javax.swing.JTextArea()
        scrollWriteBack = javax.swing.JScrollPane()
        writeBackArea = javax.swing.JTextArea()
        labelFetch = javax.swing.JLabel()
        labelDecode = javax.swing.JLabel()
        labelAlu = javax.swing.JLabel()
        labelMemory = javax.swing.JLabel()
        labelWriteBack = javax.swing.JLabel()
        labelClock = javax.swing.JLabel()
        labelClockValue = javax.swing.JLabel()
        labelPc = javax.swing.JLabel()
        labelPcValue = javax.swing.JLabel()
        labelForwarding = javax.swing.JLabel()
        labelForwardingValue = javax.swing.JLabel()
        scrollRegisters = javax.swing.JScrollPane()
        registersArea = javax.swing.JTextArea()
        labelRegisters = javax.swing.JLabel()
        generateBinaryButton = javax.swing.JButton()
        closeButton = javax.swing.JButton()

        defaultCloseOperation = javax.swing.WindowConstants.EXIT_ON_CLOSE
        title = "MIPS Simulator"
        isUndecorated = true

        panel.border = javax.swing.BorderFactory.createLineBorder(java.awt.Color(0, 0, 0))

        forwardingToggle.text = "Forwarding"
        forwardingToggle.addActionListener { evt: java.awt.event.ActionEvent ->
            forwardingToggleActionPerformed(evt)
        }

        nextButton.text = "Next"
        nextButton.addActionListener { evt: java.awt.event.ActionEvent ->
            nextButtonActionPerformed(evt)
        }

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
        generateBinaryButton.addActionListener { evt: java.awt.event.ActionEvent ->
            generateBinaryButtonActionPerformed(evt)
        }

        closeButton.text = "X"
        closeButton.addActionListener { evt: java.awt.event.ActionEvent ->
            closeButtonActionPerformed(evt)
        }

        val panelLayout = javax.swing.GroupLayout(panel)
        panel.layout = panelLayout
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    panelLayout.createSequentialGroup()
                        .addGroup(
                            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    javax.swing.GroupLayout.Alignment.TRAILING,
                                    panelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(generateBinaryButton)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            Int.MAX_VALUE
                                        )
                                        .addComponent(forwardingToggle)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(nextButton)
                                )
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addGap(19, 19, 19)
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                false
                                            )
                                                .addComponent(
                                                    labelFetch,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Int.MAX_VALUE
                                                )
                                                .addComponent(scrollFetch)
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addComponent(
                                                            scrollDecode,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED
                                                        )
                                                        .addComponent(
                                                            scrollAlu,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                )
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addComponent(labelDecode)
                                                        .addGap(31, 31, 31)
                                                        .addComponent(
                                                            labelAlu,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            166,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                )
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(
                                                    scrollMemory,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                                )
                                                .addComponent(
                                                    labelMemory,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                    112,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                                )
                                        )
                                        .addGroup(
                                            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED
                                                        )
                                                        .addComponent(
                                                            scrollWriteBack,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                )
                                                .addGroup(
                                                    javax.swing.GroupLayout.Alignment.TRAILING,
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
                                                javax.swing.GroupLayout.Alignment.TRAILING
                                            )
                                                .addComponent(labelPc)
                                                .addComponent(labelClock)
                                        )
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                false
                                            )
                                                .addComponent(
                                                    labelClockValue,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Int.MAX_VALUE
                                                )
                                                .addComponent(
                                                    labelPcValue,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    25,
                                                    Int.MAX_VALUE
                                                )
                                        )
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            Int.MAX_VALUE
                                        )
                                        .addComponent(labelForwarding)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                            labelForwardingValue,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            33,
                                            javax.swing.GroupLayout.PREFERRED_SIZE
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
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE)
                )
        )
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE
                                            )
                                                .addComponent(labelForwarding)
                                                .addComponent(labelForwardingValue)
                                        )
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            Int.MAX_VALUE
                                        )
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING
                                            )
                                                .addComponent(labelWriteBack, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(labelMemory, javax.swing.GroupLayout.Alignment.TRAILING)
                                        )
                                )
                                .addGroup(
                                    panelLayout.createSequentialGroup()
                                        .addGroup(
                                            panelLayout.createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING
                                            )
                                                .addGroup(
                                                    panelLayout.createSequentialGroup()
                                                        .addGap(21, 21, 21)
                                                        .addGroup(
                                                            panelLayout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE
                                                            )
                                                                .addComponent(labelClock)
                                                                .addComponent(
                                                                    labelClockValue,
                                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                    14,
                                                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                                                )
                                                        )
                                                        .addGap(18, 18, 18)
                                                        .addGroup(
                                                            panelLayout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE
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
                                                javax.swing.GroupLayout.Alignment.BASELINE
                                            )
                                                .addComponent(labelFetch)
                                                .addComponent(labelDecode)
                                                .addComponent(labelAlu)
                                        )
                                )
                        )
                        .addGap(18, 18, 18)
                        .addGroup(
                            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(
                                    scrollFetch,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(
                                    scrollDecode,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(
                                    scrollAlu,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(
                                    scrollMemory,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                                .addComponent(
                                    scrollWriteBack,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE
                                )
                        )
                        .addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                            80,
                            Int.MAX_VALUE
                        )
                        .addComponent(labelRegisters)
                        .addGap(18, 18, 18)
                        .addComponent(
                            scrollRegisters,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            58,
                            javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18, 18, 18)
                        .addGroup(
                            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(forwardingToggle)
                                .addComponent(nextButton)
                                .addComponent(generateBinaryButton)
                        )
                        .addContainerGap()
                )
        )

        val layout = javax.swing.GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(
                            panel,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Int.MAX_VALUE
                        )
                        .addContainerGap()
                )
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(
                            panel,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Int.MAX_VALUE
                        )
                        .addContainerGap()
                )
        )

        pack()
        setLocationRelativeTo(null)
    }

    private fun forwardingToggleActionPerformed(@Suppress("UNUSED_PARAMETER") evt: java.awt.event.ActionEvent) {
        processor.forwarding = !processor.forwarding
    }

    private fun nextButtonActionPerformed(@Suppress("UNUSED_PARAMETER") evt: java.awt.event.ActionEvent) {
        val data: MutableList<String> = processor.nextStep()

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

    private fun generateBinaryButtonActionPerformed(@Suppress("UNUSED_PARAMETER") evt: java.awt.event.ActionEvent) {
        processor.generateBinary()
    }

    private fun closeButtonActionPerformed(@Suppress("UNUSED_PARAMETER") evt: java.awt.event.ActionEvent) {
        System.exit(0)
    }

    private lateinit var labelClockValue: javax.swing.JLabel
    private lateinit var closeButton: javax.swing.JButton
    private lateinit var forwardingToggle: javax.swing.JToggleButton
    private lateinit var nextButton: javax.swing.JButton
    private lateinit var labelPcValue: javax.swing.JLabel
    private lateinit var panel: javax.swing.JPanel
    private lateinit var generateBinaryButton: javax.swing.JButton
    private lateinit var fetchArea: javax.swing.JTextArea
    private lateinit var decodeArea: javax.swing.JTextArea
    private lateinit var labelForwardingValue: javax.swing.JLabel
    private lateinit var labelFetch: javax.swing.JLabel
    private lateinit var labelDecode: javax.swing.JLabel
    private lateinit var labelAlu: javax.swing.JLabel
    private lateinit var labelMemory: javax.swing.JLabel
    private lateinit var labelWriteBack: javax.swing.JLabel
    private lateinit var labelClock: javax.swing.JLabel
    private lateinit var labelPc: javax.swing.JLabel
    private lateinit var labelForwarding: javax.swing.JLabel
    private lateinit var labelRegisters: javax.swing.JLabel
    private lateinit var scrollFetch: javax.swing.JScrollPane
    private lateinit var scrollDecode: javax.swing.JScrollPane
    private lateinit var scrollAlu: javax.swing.JScrollPane
    private lateinit var scrollMemory: javax.swing.JScrollPane
    private lateinit var scrollWriteBack: javax.swing.JScrollPane
    private lateinit var scrollRegisters: javax.swing.JScrollPane
    private lateinit var memoryArea: javax.swing.JTextArea
    private lateinit var registersArea: javax.swing.JTextArea
    private lateinit var aluArea: javax.swing.JTextArea
    private lateinit var writeBackArea: javax.swing.JTextArea
}
