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
            layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout
                        .createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(
                            layout
                                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(okButton)
                                .addComponent(valueField, GroupLayout.DEFAULT_SIZE, 50, Int.MAX_VALUE)
                                .addComponent(
                                    valueLabel,
                                    GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.DEFAULT_SIZE,
                                    Int.MAX_VALUE,
                                ),
                        ).addContainerGap(48, Int.MAX_VALUE),
                ),
        )
        layout.setVerticalGroup(
            layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(valueLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(
                            valueField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                        ).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(okButton)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Int.MAX_VALUE),
                ),
        )

        pack()
        setLocationRelativeTo(null)
    }

    private fun okButtonActionPerformed() {
        processor.memory[position] = valueField.text
        isVisible = false
    }
}
