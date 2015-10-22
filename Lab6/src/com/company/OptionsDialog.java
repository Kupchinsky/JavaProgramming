package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsDialog extends JFrame {

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 200;

    private Main mainFrame;

    OptionsDialog(Main mainFrame) {
        this.mainFrame = mainFrame;

        //
        final JRadioButton button1 = new JRadioButton("Отображать таблицу умножения");
        JRadioButton button2 = new JRadioButton("Отображать таблицу сложения");
        JButton button3 = new JButton("Применить");

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OptionsDialog.this.mainFrame.setTableMul(button1.isSelected());
                OptionsDialog.this.mainFrame.createTable();
                OptionsDialog.this.setVisible(false);
                OptionsDialog.this.dispose();
            }
        });

        button1.setSelected(mainFrame.isTableMul());
        button2.setSelected(!mainFrame.isTableMul());

        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);

        this.setLayout(new GridBagLayout());
        Box sizeBox = Box.createVerticalBox();

        sizeBox.add(button1);
        sizeBox.add(button2);
        sizeBox.add(button3);

        this.add(sizeBox);
        //

        this.pack();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screen.width / 2 - (OptionsDialog.DEFAULT_WIDTH / 2), screen.height / 2 - (OptionsDialog.DEFAULT_HEIGHT / 2));
        this.setSize(OptionsDialog.DEFAULT_WIDTH, OptionsDialog.DEFAULT_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Select option and click button");
        this.setResizable(false);
        this.setVisible(true);
    }
}
