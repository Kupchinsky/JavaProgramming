package com.company;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 600;

    @Getter
    @Setter
    private boolean isTableMul = true;
    private JTable previousTable = null;

    Main() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLocation(screen.width / 2 - (Main.DEFAULT_WIDTH / 2), screen.height / 2 - (Main.DEFAULT_HEIGHT / 2));
        this.setSize(Main.DEFAULT_WIDTH, Main.DEFAULT_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Lab6");
        this.createMenu();
        this.setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Файл");

        JMenuItem menuItem1 = new JMenuItem("Создать");
        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OptionsDialog(Main.this);
            }
        });

        menuFile.add(menuItem1);

        menuBar.add(menuFile);
        this.setJMenuBar(menuBar);
    }

    void createTable() {

        if (this.previousTable != null)
            this.remove(this.previousTable);

        this.previousTable = new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return 10;
            }

            @Override
            public int getColumnCount() {
                return 10;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (rowIndex == 0 && columnIndex != 0)
                    return columnIndex;
                else if (columnIndex == 0 && rowIndex != 0)
                    return rowIndex;
                else if (columnIndex > 0 && rowIndex > 0)
                    return Main.this.isTableMul ? columnIndex * rowIndex : columnIndex + rowIndex;
                else
                    return Main.this.isTableMul ? "Умножение" : "Сложение";
            }
        });

        this.add(this.previousTable);
        this.pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}
