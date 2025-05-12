package Source;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class Alu extends JFrame {
    private Logic logicEngine;

    public Alu() {
        logicEngine = new Logic();
        setTitle("ALU Sim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 80, 40, 80)); // Top, Left, Bottom, Right padding
        panel.setBackground(new Color(245, 247, 250)); // Light modern background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16); // More spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel labelA = new JLabel("Input A:");
        labelA.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(labelA, gbc);
        gbc.gridx = 1;
        JTextField inputA = new JTextField();
        inputA.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        inputA.setColumns(10);
        panel.add(inputA, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel labelB = new JLabel("Input B:");
        labelB.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(labelB, gbc);
        gbc.gridx = 1;
        JTextField inputB = new JTextField();
        inputB.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        inputB.setColumns(10);
        panel.add(inputB, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel opLabel = new JLabel("Operation:");
        opLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(opLabel, gbc);
        gbc.gridx = 1;
        String[] operations = {"Add", "Subtract", "AND", "OR", "NOT A"};
        JComboBox<String> operationBox = new JComboBox<>(operations);
        operationBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(operationBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JButton calcButton = new JButton("Calculate");
        calcButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        calcButton.setBackground(new Color(0, 120, 215));
        calcButton.setForeground(Color.WHITE);
        calcButton.setFocusPainted(false);
        calcButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(calcButton, gbc);

        gbc.gridx = 1;
        JLabel resultLabel = new JLabel("Result: ");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultLabel.setForeground(new Color(0, 120, 80));
        panel.add(resultLabel, gbc);

        operationBox.addActionListener(e -> {
            String op = (String) operationBox.getSelectedItem();
            boolean isNot = "NOT A".equals(op);
            labelB.setVisible(!isNot);
            inputB.setVisible(!isNot);
        });
        labelB.setVisible(true);
        inputB.setVisible(true);

        calcButton.addActionListener(e -> {
            try {
                int a = Integer.parseInt(inputA.getText());
                int b = 0;
                String op = (String) operationBox.getSelectedItem();
                int result = 0;

                if (!"NOT A".equals(op)) {
                    b = Integer.parseInt(inputB.getText());
                }

                switch (op) {
                    case "Add":
                        result = logicEngine.add(a, b);
                        break;
                    case "Subtract":
                        result = logicEngine.subtract(a, b);
                        break;
                    case "AND":
                        result = logicEngine.and(a, b);
                        break;
                    case "OR":
                        result = logicEngine.or(a, b);
                        break;
                    case "NOT A":
                        result = logicEngine.not(a);
                        break;
                }
                resultLabel.setText("Result: " + result);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid input!");
            }
        });

        setContentPane(panel);
    }
}