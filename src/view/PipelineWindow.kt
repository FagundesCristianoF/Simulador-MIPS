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
