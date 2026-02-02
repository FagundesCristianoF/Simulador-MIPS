package view

import controller.Processor
import java.awt.Color
import javax.swing.*

class PipeLining(private val processor: Processor) : JFrame() {
    private fun initComponents() {
        panel = JPanel()
        forwardingToggle = JToggleButton()
        nextButton = JButton()
        jScrollPane1 = JScrollPane()
        busca = JTextArea()
        jScrollPane2 = JScrollPane()
        decod = JTextArea()
        jScrollPane3 = JScrollPane()
        ula = JTextArea()
        jScrollPane4 = JScrollPane()
        memory = JTextArea()
        jScrollPane5 = JScrollPane()
        writeback = JTextArea()
        jLabel1 = JLabel()
        jLabel2 = JLabel()
        jLabel3 = JLabel()
        jLabel4 = JLabel()
        jLabel5 = JLabel()
        jLabel6 = JLabel()
        clock = JLabel()
        jLabel7 = JLabel()
        pcLabel = JLabel()
        jLabel8 = JLabel()
        forwarding = JLabel()
        jScrollPane6 = JScrollPane()
        registers = JTextArea()
        jLabel9 = JLabel()
        binaryButton = JButton()
        closeButton = JButton()

        defaultCloseOperation = EXIT_ON_CLOSE
        title = "MIPS Simulator"
        isUndecorated = true

        panel?.border = BorderFactory.createLineBorder(Color(0, 0, 0))

        forwardingToggle?.text = "Forwarding"
        forwardingToggle?.addActionListener { evt -> forwardingActionClicked() }

        nextButton?.text = "Next"
        nextButton?.addActionListener { _ -> nextActionClicked() }

        busca?.columns = 20
        busca?.rows = 5
        jScrollPane1?.setViewportView(busca)

        decod?.columns = 20
        decod?.rows = 5
        jScrollPane2?.setViewportView(decod)

        ula?.columns = 20
        ula?.rows = 5
        jScrollPane3?.setViewportView(ula)

        memory?.columns = 20
        memory?.rows = 5
        jScrollPane4?.setViewportView(memory)

        writeback?.columns = 20
        writeback?.rows = 5
        jScrollPane5?.setViewportView(writeback)

        jLabel1?.text = "Operand Fetch"

        jLabel2?.text = "Operand Decode"

        jLabel3?.text = "                     ALU"

        jLabel4?.text = "Memory"

        jLabel5?.text = "Write Back"

        jLabel6?.text = "Clock:"

        jLabel7?.text = "PC:"

        pcLabel?.text = " "

        jLabel8?.text = "Forwarding: "

        forwarding?.text = "  "

        registers?.columns = 20
        registers?.rows = 5
        jScrollPane6?.setViewportView(registers)

        jLabel9?.text = "Registers"

        binaryButton?.text = "Generate Binary"
        binaryButton?.addActionListener { _ -> binaryButtonClicked() }

        closeButton?.text = "X"
        closeButton?.addActionListener { _ -> exitActionClicked() }

        val group = GroupLayout(panel)
        panel?.layout = group
        group.setHorizontalGroup(
            group.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    group.createSequentialGroup()
                        .addGroup(
                            group.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    GroupLayout.Alignment.TRAILING,
                                    group.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(binaryButton)
                                        .addComponent(forwardingToggle)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(nextButton),
                                )
                                .addGroup(
                                    group.createSequentialGroup()
                                        .addGap(19, 19, 19)
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(
                                                    jLabel1,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE.toInt(),
                                                )
                                                .addComponent(jScrollPane1),
                                        )
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    group.createSequentialGroup()
                                                        .addComponent(
                                                            jScrollPane2,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            GroupLayout.DEFAULT_SIZE,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        )
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(
                                                            jScrollPane3,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            GroupLayout.DEFAULT_SIZE,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        ),
                                                )
                                                .addGroup(
                                                    group.createSequentialGroup()
                                                        .addComponent(jLabel2)
                                                        .addGap(31, 31, 31)
                                                        .addComponent(
                                                            jLabel3,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            166,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        ),
                                                ),
                                        )
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(
                                                    jScrollPane4,
                                                    GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.PREFERRED_SIZE,
                                                )
                                                .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE),
                                        )
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    group.createSequentialGroup()
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(
                                                            jScrollPane5,
                                                            GroupLayout.PREFERRED_SIZE,
                                                            GroupLayout.DEFAULT_SIZE,
                                                            GroupLayout.PREFERRED_SIZE,
                                                        ),
                                                )
                                                .addGroup(
                                                    GroupLayout.Alignment.TRAILING,
                                                    group.createSequentialGroup()
                                                        .addGap(86, 86, 86)
                                                        .addComponent(jLabel5)
                                                        .addGap(24, 24, 24),
                                                ),
                                        )
                                        .addGap(0, 40, Short.MAX_VALUE.toInt()),
                                )
                                .addGroup(
                                    group.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel7)
                                                .addComponent(jLabel6),
                                        )
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(
                                                    clock,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE.toInt(),
                                                )
                                                .addComponent(pcLabel, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE.toInt()),
                                        )
                                        .addComponent(jLabel8)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(forwarding, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                                        .addGap(306, 306, 306)
                                        .addComponent(closeButton),
                                )
                                .addGroup(
                                    group.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane6),
                                ),
                        )
                        .addContainerGap(),
                )
                .addGroup(
                    group.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel9)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE.toInt()),
                ),
        )
        group.setVerticalGroup(
            group.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    GroupLayout.Alignment.TRAILING,
                    group.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            group.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(
                                    group.createSequentialGroup()
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel8)
                                                .addComponent(forwarding),
                                        )
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel5, GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel4, GroupLayout.Alignment.TRAILING),
                                        ),
                                )
                                .addGroup(
                                    group.createSequentialGroup()
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(
                                                    group.createSequentialGroup()
                                                        .addGap(21, 21, 21)
                                                        .addGroup(
                                                            group.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jLabel6)
                                                                .addComponent(
                                                                    clock,
                                                                    GroupLayout.PREFERRED_SIZE,
                                                                    14,
                                                                    GroupLayout.PREFERRED_SIZE,
                                                                ),
                                                        )
                                                        .addGap(18, 18, 18)
                                                        .addGroup(
                                                            group.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jLabel7)
                                                                .addComponent(pcLabel),
                                                        ),
                                                )
                                                .addComponent(closeButton),
                                        )
                                        .addGap(107, 107, 107)
                                        .addGroup(
                                            group.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel2)
                                                .addComponent(jLabel3),
                                        ),
                                ),
                        )
                        .addGap(18, 18, 18)
                        .addGroup(
                            group.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(
                                    jScrollPane1,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    jScrollPane2,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    jScrollPane3,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    jScrollPane4,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                )
                                .addComponent(
                                    jScrollPane5,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                ),
                        )
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane6, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(
                            group.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(forwardingToggle)
                                .addComponent(nextButton)
                                .addComponent(binaryButton),
                        )
                        .addContainerGap(),
                ),
        )

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE.toInt())
                        .addContainerGap(),
                ),
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE.toInt())
                        .addContainerGap(),
                ),
        )

        pack()
        setLocationRelativeTo(null)
    } // </editor-fold>//GEN-END:initComponents

    private fun forwardingActionClicked() {
        processor.forwarding = !(processor.forwarding)
    }

    private fun nextActionClicked() { // GEN-FIRST:event_NextActionPerformed
        val aux = processor.nextOperation()

        if (aux.isNotEmpty()) {
            busca?.text = aux[0]
            decod?.text = aux[1]
            ula?.text = aux[2]
            memory?.text = aux[3]
            writeback?.text = aux[4]
            pcLabel?.text = aux[5]
            clock?.text = aux[6]
            forwarding?.text = aux[7]
            registers?.text = aux[8]
        }

        // XEON.leituraDeInstrucao();
    } // GEN-LAST:event_NextActionPerformed

    private fun binaryButtonClicked() { // GEN-FIRST:event_btnBinarioActionPerformed
        processor.geraBinario()
    } // GEN-LAST:event_btnBinarioActionPerformed

    private fun exitActionClicked() {
        System.exit(0)
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private var clock: JLabel? = null
    private var closeButton: JButton? = null
    private var forwardingToggle: JToggleButton? = null
    private var nextButton: JButton? = null
    private var pcLabel: JLabel? = null
    private var panel: JPanel? = null
    private var binaryButton: JButton? = null
    private var busca: JTextArea? = null
    private var decod: JTextArea? = null
    private var forwarding: JLabel? = null
    private var jLabel1: JLabel? = null
    private var jLabel2: JLabel? = null
    private var jLabel3: JLabel? = null
    private var jLabel4: JLabel? = null
    private var jLabel5: JLabel? = null
    private var jLabel6: JLabel? = null
    private var jLabel7: JLabel? = null
    private var jLabel8: JLabel? = null
    private var jLabel9: JLabel? = null
    private var jScrollPane1: JScrollPane? = null
    private var jScrollPane2: JScrollPane? = null
    private var jScrollPane3: JScrollPane? = null
    private var jScrollPane4: JScrollPane? = null
    private var jScrollPane5: JScrollPane? = null
    private var jScrollPane6: JScrollPane? = null
    private var memory: JTextArea? = null
    private var registers: JTextArea? = null
    private var ula: JTextArea? = null
    private var writeback: JTextArea? = null // End of variables declaration//GEN-END:variables

    init {
        initComponents()
        isVisible = false
        registers?.lineWrap = true

        iconImage = ImageIcon("./image/logo.jpg").image
    }
}
