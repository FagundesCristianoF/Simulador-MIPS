package view

import controller.Processor
import javax.swing.*

class InputUI(
    private val processor: Processor,
    private val position: Int,
) : JFrame() {
    init {
        initComponents()
    }

    private fun initComponents() {
        inputText = JTextField()
        jLabel1 =
            JLabel().apply {
                text = "    Value"
            }
        ok =
            JButton().apply {
                text = "OK"
                addActionListener { _ -> okButtonClicked() }
            }

        defaultCloseOperation = EXIT_ON_CLOSE

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(ok)
                                .addComponent(inputText, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE.toInt())
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE.toInt()),
                        )
                        .addContainerGap(48, Short.MAX_VALUE.toInt()),
                ),
        )
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ok)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE.toInt()),
                ),
        )

        pack()
        setLocationRelativeTo(null)
    }

    private fun okButtonClicked() {
        processor.memory[position] = inputText?.text

        isVisible = false
    }

    private var ok: JButton? = null
    private var inputText: JTextField? = null
    private var jLabel1: JLabel? = null
}
