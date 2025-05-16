package Source;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Alu extends JFrame {
    private Logic aluLogic;
    private JTextField input1Field, input2Field, resultField;
    private JTextField binaryInput1TextField, binaryInput2TextField, binaryResultTextField;
    private JComboBox<OperationItem> operationCombo;
    private JButton calculateButton;
    private JPanel historyPanel;
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;
    private JComboBox<String> baseSelector;
    private Map<String, BiFunction<Integer, Integer, Integer>> binaryOperations;
    private Map<String, Function<Integer, Integer>> unaryOperations;
    private Timer animationTimer;
    private JLabel secondaryLabelA, secondaryLabelB;
    private static final String DEFAULT_BINARY_STRING = "00000000";
    private static final int RESULT_ANIMATION_DURATION = 600; 
    private int hoveredHistoryIndex = -1; 

    private static class OperationItem {
        private final String displayString;
        private final String operationKey;

        public OperationItem(String displayString, String operationKey) {
            this.displayString = displayString;
            this.operationKey = operationKey;
        }

        @Override
        public String toString() {
            return displayString;
        }

        public String getKey() {
            return operationKey;
        }
    }

    public Alu() {
        aluLogic = new Logic();
        setupOperations();
        setupGUI();
        
        addInputValidationFeedback(input1Field, true);
        addInputValidationFeedback(input2Field, true);
        
        setupModelListeners();
    }

    private void setupModelListeners() {
        aluLogic.addPropertyChangeListener((evt) -> {
            if (evt.getPropertyName().equals("binaryResult")) {
                SwingUtilities.invokeLater(() -> {
                    if (binaryResultTextField != null) {
                        binaryResultTextField.setText((String)evt.getNewValue());
                        binaryResultTextField.setForeground(Ui.TEXT_LIGHT);
                    }
                    animateResultField();
                });
            } else if (evt.getPropertyName().equals("historyUpdate")) {
                SwingUtilities.invokeLater(() -> {
                    updateHistoryDisplay((String[])evt.getNewValue());
                });
            }
        });
    }

    private void updateHistoryDisplay(String[] history) {
        int historyCount = 0;
        for (String entry : history) {
            if (entry != null) historyCount++;
        }
        while (historyModel.getSize() > historyCount) {
            historyModel.remove(historyModel.getSize() - 1);
        }
        int i = 0;
        for (String entry : history) {
            if (entry != null) {
                if (i < historyModel.getSize()) {
                    if (!historyModel.get(i).equals(entry)) {
                        historyModel.set(i, entry);
                    }
                } else {
                    historyModel.addElement(entry);
                }
                i++;
            }
        }
        if (historyModel.getSize() > 0) {
            historyList.ensureIndexIsVisible(historyModel.getSize() - 1);
        }
    }

    private OperationItem[] getStyledOperationItems() {
        return new OperationItem[]{
            new OperationItem("ADD", "ADD"),
            new OperationItem("SUBTRACT", "SUBTRACT"),
            new OperationItem("MULTIPLY", "MULTIPLY"),
            new OperationItem("AND", "AND"),
            new OperationItem("OR", "OR"),
            new OperationItem("NOT", "NOT")
        };
    }

    private int addLabeledComponentToPanel(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int gridx, int startY) {
        gbc.gridx = gridx;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.gridy = startY;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 14, 2, 14); 
        JLabel label = new JLabel(labelText);
        label.setFont(Ui.COMPONENT_LABEL_FONT);
        label.setForeground(Ui.TEXT_LIGHT);
        panel.add(label, gbc);

        gbc.gridy = startY + 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 14, 8, 14);
        panel.add(component, gbc);
        return startY + 2;
    }

    private void setupGUI() {
        setTitle("ALUSim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Ui.FRAME_BACKGROUND);
        setLayout(new BorderLayout(20, 0));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(18, 60, 18, 60));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Ui.FRAME_BACKGROUND);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("ALU Calculator");
        titleLabel.setFont(Ui.TITLE_FONT.deriveFont(Font.BOLD, 40f));
        titleLabel.setForeground(Ui.ACCENT_PURPLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Simple Arithmetic Logic Unit");
        subtitleLabel.setFont(Ui.SUBTITLE_FONT.deriveFont(Font.PLAIN, 22f)); 
        subtitleLabel.setForeground(Ui.ACCENT_TEAL.darker());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(true);
        centerPanel.setBackground(Ui.FRAME_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel mainContentPanel = Ui.createRoundedPanel();
        mainContentPanel.setLayout(new GridBagLayout());
        mainContentPanel.setBackground(Ui.PANEL_BACKGROUND);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        GridBagConstraints gbc = new GridBagConstraints();

        input1Field = Ui.createModernTextField();
        input2Field = Ui.createModernTextField();
        Ui.setInputFieldFocusBehavior(input1Field, input2Field);
        getRootPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Component comp = getRootPane().getContentPane().getComponentAt(e.getPoint());
                if (!(comp instanceof JTextField)) {
                    if (input1Field.hasFocus() || input2Field.hasFocus()) {
                        getRootPane().requestFocusInWindow();
                    }
                }
            }
        });
        resultField = Ui.createModernTextField();
        resultField.setEditable(false);
        resultField.setBackground(Ui.FIELD_BACKGROUND);
        resultField.setFont(Ui.POPPINS_BOLD);

        binaryInput1TextField = Ui.createModernTextField();
        binaryInput1TextField.setFont(new Font("Poppins", Font.PLAIN, 18));
        binaryInput1TextField.setText(DEFAULT_BINARY_STRING);
        binaryInput1TextField.setHorizontalAlignment(JTextField.LEFT);
        binaryInput1TextField.setEditable(false);
        binaryInput1TextField.setBackground(Ui.FIELD_BACKGROUND);

        binaryInput2TextField = Ui.createModernTextField();
        binaryInput2TextField.setFont(new Font("Poppins", Font.PLAIN, 18));
        binaryInput2TextField.setText(DEFAULT_BINARY_STRING);
        binaryInput2TextField.setHorizontalAlignment(JTextField.LEFT);
        binaryInput2TextField.setEditable(false);
        binaryInput2TextField.setBackground(Ui.FIELD_BACKGROUND);

        binaryResultTextField = Ui.createModernTextField();
        binaryResultTextField.setFont(Ui.POPPINS_FONT);
        binaryResultTextField.setHorizontalAlignment(JTextField.LEFT);
        binaryResultTextField.setBackground(Ui.FIELD_BACKGROUND);
        binaryResultTextField.setEditable(false);

        baseSelector = Ui.createModernComboBox(new String[]{"DECIMAL", "BINARY"});
        baseSelector.addActionListener(e -> {
            updateSecondaryLabels();
            addInputValidationFeedback(input1Field);
            addInputValidationFeedback(input2Field);
            updateSecondaryRepresentation(input1Field.getText(), binaryInput1TextField);
            updateSecondaryRepresentation(input2Field.getText(), binaryInput2TextField);
            String base = (String) baseSelector.getSelectedItem();
            if ("BINARY".equalsIgnoreCase(base)) {
                binaryInput1TextField.setText("0");
                binaryInput2TextField.setText("0");
            } else {
                binaryInput1TextField.setText(DEFAULT_BINARY_STRING);
                binaryInput2TextField.setText(DEFAULT_BINARY_STRING);
            }
        });

        operationCombo = Ui.createModernComboBox(getStyledOperationItems());

        int currentY = 0;
        secondaryLabelA = new JLabel();
        secondaryLabelA.setFont(Ui.COMPONENT_LABEL_FONT);
        secondaryLabelA.setForeground(Ui.ACCENT_TEAL.darker());
        secondaryLabelB = new JLabel();
        secondaryLabelB.setFont(Ui.COMPONENT_LABEL_FONT);
        secondaryLabelB.setForeground(Ui.ACCENT_TEAL.darker());
        updateSecondaryLabels();
        currentY = addLabeledComponentToPanel(mainContentPanel, gbc, "Input A:", input1Field, 0, currentY);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(18, 18, 6, 18);
        mainContentPanel.add(secondaryLabelA, gbc);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 18, 18, 18);
        mainContentPanel.add(binaryInput1TextField, gbc);
        addLabeledComponentToPanel(mainContentPanel, gbc, "Select Number System", baseSelector, 2, 0);

        currentY = addLabeledComponentToPanel(mainContentPanel, gbc, "Input B:", input2Field, 0, currentY);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(18, 18, 6, 18);
        mainContentPanel.add(secondaryLabelB, gbc);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 18, 18, 18);
        mainContentPanel.add(binaryInput2TextField, gbc);
        addLabeledComponentToPanel(mainContentPanel, gbc, "Select Operation", operationCombo, 2, 2);

        calculateButton = Ui.createModernButton("Calculate");
        calculateButton.addActionListener(e -> {
            performOperation();
            getRootPane().requestFocusInWindow();
        });
        calculateButton.setFont(Ui.POPPINS_BOLD);
        calculateButton.setPreferredSize(new Dimension(110, 48));
        JButton clearButton = Ui.createModernButton("Clear");
        clearButton.setBackground(Ui.ACCENT_CORAL);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(Ui.POPPINS_BOLD);
        clearButton.setPreferredSize(new Dimension(110, 48));
        clearButton.addActionListener(e -> {
            input1Field.setText("");
            input2Field.setText("");
            resultField.setText("");
            binaryInput1TextField.setText(DEFAULT_BINARY_STRING);
            binaryInput2TextField.setText(DEFAULT_BINARY_STRING);
            binaryResultTextField.setText("");
            input1Field.setCaretPosition(0);
            input2Field.setCaretPosition(0);
            binaryInput1TextField.setCaretPosition(0);
            binaryInput2TextField.setCaretPosition(0);
            binaryResultTextField.setCaretPosition(0);
            input1Field.setSelectionStart(0); input1Field.setSelectionEnd(0);
            input2Field.setSelectionStart(0); input2Field.setSelectionEnd(0);
            binaryInput1TextField.setSelectionStart(0); binaryInput1TextField.setSelectionEnd(0);
            binaryInput2TextField.setSelectionStart(0); binaryInput2TextField.setSelectionEnd(0);
            binaryResultTextField.setSelectionStart(0); binaryResultTextField.setSelectionEnd(0);
            input1Field.setBackground(Ui.FIELD_BACKGROUND);
            input2Field.setBackground(Ui.FIELD_BACKGROUND);
            binaryInput1TextField.setBackground(Ui.FIELD_BACKGROUND);
            binaryInput2TextField.setBackground(Ui.FIELD_BACKGROUND);
            binaryResultTextField.setBackground(Ui.FIELD_BACKGROUND);
            addInputValidationFeedback(input1Field);
            addInputValidationFeedback(input2Field);
            updateSecondaryRepresentation("", binaryInput1TextField);
            updateSecondaryRepresentation("", binaryInput2TextField);
            input1Field.revalidate(); input1Field.repaint();
            input2Field.revalidate(); input2Field.repaint();
            resultField.revalidate(); resultField.repaint();
            binaryInput1TextField.revalidate(); binaryInput1TextField.repaint();
            binaryInput2TextField.revalidate(); binaryInput2TextField.repaint();
            binaryResultTextField.revalidate(); binaryResultTextField.repaint();
            if (input1Field.getParent() != null) { input1Field.getParent().revalidate(); input1Field.getParent().repaint(); }
            if (input2Field.getParent() != null) { input2Field.getParent().revalidate(); input2Field.getParent().repaint(); }
            if (binaryInput1TextField.getParent() != null) { binaryInput1TextField.getParent().revalidate(); binaryInput1TextField.getParent().repaint(); }
            if (binaryInput2TextField.getParent() != null) { binaryInput2TextField.getParent().revalidate(); binaryInput2TextField.getParent().repaint(); }
            if (resultField.getParent() != null) { resultField.getParent().revalidate(); resultField.getParent().repaint(); }
            if (binaryResultTextField.getParent() != null) { binaryResultTextField.getParent().revalidate(); binaryResultTextField.getParent().repaint(); }
            getRootPane().requestFocusInWindow();
        });

        JPanel groupButtonPanel = new JPanel(new GridBagLayout());
        groupButtonPanel.setOpaque(false);
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        gbcBtn.weightx = 0.5;
        gbcBtn.fill = GridBagConstraints.BOTH;
        gbcBtn.insets = new Insets(0, 0, 0, 6);
        groupButtonPanel.add(calculateButton, gbcBtn);
        gbcBtn.gridx = 1;
        gbcBtn.insets = new Insets(0, 6, 0, 0);
        groupButtonPanel.add(clearButton, gbcBtn);
        groupButtonPanel.setPreferredSize(new Dimension(240, 48));

        Font resultFont = Ui.POPPINS_FONT;
        resultField.setFont(resultFont);
        binaryResultTextField.setFont(resultFont);

        Border resultBorder = BorderFactory.createCompoundBorder(
            new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.BORDER_COLOR),
            BorderFactory.createEmptyBorder(Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING, Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING)
        );
        resultField.setBorder(resultBorder);
        binaryResultTextField.setBorder(resultBorder);
        resultField.setBackground(Ui.FIELD_BACKGROUND);
        binaryResultTextField.setBackground(Ui.FIELD_BACKGROUND);
        Dimension resultSize = resultField.getPreferredSize();
        binaryResultTextField.setPreferredSize(resultSize);
        binaryResultTextField.setMinimumSize(resultSize);
        binaryResultTextField.setMaximumSize(resultSize);
        resultField.setPreferredSize(resultSize);
        resultField.setMinimumSize(resultSize);
        resultField.setMaximumSize(resultSize);

        int resultRowY = currentY;
        gbc.gridx = 0;
        gbc.gridy = resultRowY;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.BASELINE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(18, 18, 6, 18);
        JLabel decimalLabel = new JLabel("Decimal Result");
        decimalLabel.setFont(Ui.COMPONENT_LABEL_FONT);
        decimalLabel.setForeground(Ui.ACCENT_PURPLE.darker());
        mainContentPanel.add(decimalLabel, gbc);
        gbc.gridx = 1;
        JLabel binaryLabel = new JLabel("Binary Result");
        binaryLabel.setFont(Ui.COMPONENT_LABEL_FONT);
        binaryLabel.setForeground(Ui.ACCENT_PURPLE.darker());
        mainContentPanel.add(binaryLabel, gbc);
        gbc.gridx = 2;
        JLabel emptyLabel = new JLabel("");
        mainContentPanel.add(emptyLabel, gbc);

        gbc.gridy = resultRowY + 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 18, 18, 18);
        gbc.anchor = GridBagConstraints.BASELINE; 
        mainContentPanel.add(resultField, gbc);
        gbc.gridx = 1;
        mainContentPanel.add(binaryResultTextField, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 18, 18, 18);
        mainContentPanel.add(groupButtonPanel, gbc);
        currentY += 2;

        JPanel calculationsPanel = new JPanel(new BorderLayout());
        calculationsPanel.setOpaque(true);
        calculationsPanel.setBackground(Ui.FRAME_BACKGROUND);
        calculationsPanel.setBorder(null);
        calculationsPanel.add(mainContentPanel, BorderLayout.CENTER);
        setupNewHistoryPanel();

        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerGbc.weightx = 0.65; 
        centerGbc.weighty = 1.0;
        centerGbc.fill = GridBagConstraints.BOTH;
        centerGbc.insets = new Insets(0, 0, 0, 16); 
        centerPanel.add(calculationsPanel, centerGbc);

        JPanel historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));
        historyContainer.setOpaque(false);
        historyContainer.add(Box.createVerticalGlue());
        historyPanel.setMaximumSize(new Dimension(370, 420));
        historyPanel.setPreferredSize(new Dimension(320, 420));
        historyPanel.setMinimumSize(new Dimension(260, 220));
        historyContainer.add(historyPanel);
        historyContainer.add(Box.createVerticalGlue());

        centerGbc.gridx = 1;
        centerGbc.gridy = 0;
        centerGbc.weightx = 0.35;
        centerGbc.weighty = 1.0;
        centerGbc.fill = GridBagConstraints.BOTH;
        centerGbc.insets = new Insets(0, 16, 0, 0); 
        centerPanel.add(historyContainer, centerGbc);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBackground(Ui.FRAME_BACKGROUND);
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 24))); 
        contentPanel.add(centerPanel);

        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setOpaque(true);
        outerPanel.setBackground(Ui.FRAME_BACKGROUND);
        GridBagConstraints gbcOuter = new GridBagConstraints();
        gbcOuter.gridx = 0;
        gbcOuter.gridy = 0;
        gbcOuter.weightx = 1.0;
        gbcOuter.weighty = 1.0;
        gbcOuter.anchor = GridBagConstraints.CENTER;
        outerPanel.add(contentPanel, gbcOuter);
        setContentPane(outerPanel);

        setupAccessibility();
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 800));
        setLocationRelativeTo(null);
        getRootPane().requestFocusInWindow();
    }

    private void updateSecondaryLabels() {
        String base = (String) baseSelector.getSelectedItem();
        if (base == null) base = "DECIMAL";
        switch (base.toUpperCase()) {
            case "BINARY":
                secondaryLabelA.setText("Decimal A:");
                secondaryLabelB.setText("Decimal B:");
                break;
            case "DECIMAL":
            default:
                secondaryLabelA.setText("Binary A:");
                secondaryLabelB.setText("Binary B:");
                break;
        }
    }

    private void setupNewHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout(5,5));
        historyPanel.setOpaque(true);
        historyPanel.setBackground(Ui.FRAME_BACKGROUND);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            new Ui.RoundedBorder(Ui.GENERAL_BORDER_RADIUS, Ui.BORDER_COLOR), 
            BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        historyPanel.setPreferredSize(new Dimension(320, 0));
        historyPanel.setMinimumSize(new Dimension(260, 0));        
        JPanel historyTitlePanel = new JPanel(new BorderLayout());
        historyTitlePanel.setBackground(Ui.FRAME_BACKGROUND);
        historyTitlePanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        JLabel historyTitleLabel = new JLabel("Calculation History");
        historyTitleLabel.setFont(Ui.POPPINS_BOLD.deriveFont(Font.BOLD, 16f));
        historyTitleLabel.setForeground(Ui.ACCENT_PURPLE);
        historyTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        historyTitlePanel.add(historyTitleLabel, BorderLayout.CENTER);
        historyPanel.add(historyTitlePanel, BorderLayout.NORTH);        
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setFont(Ui.POPPINS_FONT.deriveFont(Font.PLAIN, 16f));
        historyList.setBackground(Ui.FRAME_BACKGROUND);
        historyList.setForeground(Ui.TEXT_DARK);
        historyList.setSelectionBackground(Ui.FRAME_BACKGROUND);
        historyList.setSelectionForeground(Ui.TEXT_DARK);
        historyList.setFixedCellHeight(38); 
        historyList.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); 
        historyList.setFocusable(false);
        historyList.setCursor(new Cursor(Cursor.HAND_CURSOR));
        historyList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = historyList.locationToIndex(e.getPoint());
                if (hoveredHistoryIndex != index) {
                    hoveredHistoryIndex = index;
                    historyList.repaint();
                }
            }
        });
        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredHistoryIndex != -1) {
                    hoveredHistoryIndex = -1;
                    historyList.repaint();
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = historyList.locationToIndex(e.getPoint());
                if (index < 0) return;
                Rectangle cellBounds = historyList.getCellBounds(index, index);
                if (cellBounds == null) return;
                int xButtonWidth = 38;
                int xButtonStart = cellBounds.x + cellBounds.width - xButtonWidth;
                Point mouse = e.getPoint();
                if (cellBounds.contains(mouse) && e.getX() >= xButtonStart && e.getX() <= cellBounds.x + cellBounds.width) {
                    aluLogic.removeHistoryEntry(index);
                }
            }
        });
        historyList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            Color bg = Ui.PANEL_BACKGROUND;
            boolean isEntryHovered = (index == hoveredHistoryIndex);
            if (isEntryHovered) {
                bg = Ui.APP_GRADIENT_END;
            }
            JPanel card = new JPanel(new BorderLayout());
            card.setOpaque(true);
            card.setBackground(bg);
            card.setBorder(BorderFactory.createCompoundBorder(
                new Ui.RoundedBorder(18, Ui.BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 18, 6, 8)));
            JLabel label = new JLabel(value);
            label.setFont(Ui.POPPINS_FONT.deriveFont(Font.PLAIN, 13f));
            label.setForeground(Ui.TEXT_DARK);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setPreferredSize(new Dimension(180, 24));
            label.setToolTipText(value); 
            label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            label.setMinimumSize(new Dimension(60, 24));
            label.setOpaque(false);
            card.add(label, BorderLayout.CENTER);
            card.setPreferredSize(new Dimension(220, 28));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            if (isEntryHovered) {
                JPanel xPanel = new JPanel(new BorderLayout());
                xPanel.setOpaque(false);
                xPanel.setPreferredSize(new Dimension(38, 38));
                JLabel xLabel = new JLabel("×"); 
                xLabel.setFont(Ui.POPPINS_BOLD.deriveFont(Font.BOLD, 22f)); 
                xLabel.setForeground(new Color(180, 60, 90));
                xLabel.setHorizontalAlignment(SwingConstants.CENTER);
                xLabel.setVerticalAlignment(SwingConstants.CENTER);
                xLabel.setOpaque(false); 
                xLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                xPanel.add(xLabel, BorderLayout.CENTER);
                card.add(xPanel, BorderLayout.EAST);
            }
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.add(card, BorderLayout.CENTER);
            return panel;
        });
        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Ui.FRAME_BACKGROUND);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        JButton deleteAllButton = Ui.createModernButton("");
        deleteAllButton.setBackground(Ui.ACCENT_CORAL);
        deleteAllButton.setPreferredSize(new Dimension(38, 38));
        deleteAllButton.setFocusable(false);
        deleteAllButton.setContentAreaFilled(false);
        deleteAllButton.setBorderPainted(false);
        deleteAllButton.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        deleteAllButton.setFont(Ui.POPPINS_BOLD.deriveFont(Font.BOLD, 20f));
        deleteAllButton.setText("🗑");
        deleteAllButton.addActionListener(e -> {
            showConfirmDialog(historyPanel, "Clear all calculation history?", "Confirm", () -> {
                aluLogic.clearHistory();
            });
        });
        bottomPanel.add(deleteAllButton);
        historyPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupAccessibility() {
        input1Field.getAccessibleContext().setAccessibleName("Input A");
        input2Field.getAccessibleContext().setAccessibleName("Input B");
        resultField.getAccessibleContext().setAccessibleName("Decimal Result");
        binaryInput1TextField.getAccessibleContext().setAccessibleName("Binary representation of Input A");
        binaryInput2TextField.getAccessibleContext().setAccessibleName("Binary representation of Input B");
        binaryResultTextField.getAccessibleContext().setAccessibleName("Binary representation of Result");
        baseSelector.getAccessibleContext().setAccessibleName("Select Number System");
        operationCombo.getAccessibleContext().setAccessibleName("Select Operation");
        calculateButton.getAccessibleContext().setAccessibleName("Calculate Button");
        historyList.getAccessibleContext().setAccessibleName("Calculation History List");
    }

    private void addInputValidationFeedback(JTextField field) {
        addInputValidationFeedback(field, false);
    }

    private void addInputValidationFeedback(JTextField field, boolean initialSetup) {
        if (baseSelector == null) {
            return;
        }
        for (javax.swing.event.DocumentListener dl : ((javax.swing.text.AbstractDocument)field.getDocument()).getDocumentListeners()) {
            field.getDocument().removeDocumentListener(dl);
        }
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void validate() {
                String currentBase = baseSelector != null ? (String) baseSelector.getSelectedItem() : "DECIMAL";
                Border currentBorder = field.getBorder();
                Border paddingPart = null;
                if (currentBorder instanceof javax.swing.border.CompoundBorder) {
                    paddingPart = ((javax.swing.border.CompoundBorder)currentBorder).getInsideBorder();
                } else {
                    paddingPart = BorderFactory.createEmptyBorder(
                        Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING,
                        Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING);
                }
                final String currentText = field.getText();
                if (currentText.isEmpty()) {
                    Border defaultBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.BORDER_COLOR);
                    field.setBorder(BorderFactory.createCompoundBorder(defaultBorder, paddingPart));
                } else if (!aluLogic.isValidInput(currentText, currentBase)) {
                    Border errorBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Color.RED.darker());
                    field.setBorder(BorderFactory.createCompoundBorder(errorBorder, paddingPart));
                } else {
                    Border validBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.ACCENT_TEAL);
                    field.setBorder(BorderFactory.createCompoundBorder(validBorder, paddingPart));
                    JTextField targetTextField = null;
                    if (field == input1Field) {
                        targetTextField = binaryInput1TextField;
                    } else if (field == input2Field) {
                        targetTextField = binaryInput2TextField;
                    }
                    if (targetTextField != null) {
                        updateSecondaryRepresentation(currentText, targetTextField);
                    }
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    validate();
                    field.setCaretPosition(field.getText().length());
                });
            }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    validate();
                    field.setCaretPosition(field.getText().length());
                });
            }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        Border currentBorder = field.getBorder();
        Border paddingPart = null;
        if (currentBorder instanceof javax.swing.border.CompoundBorder) {
            paddingPart = ((javax.swing.border.CompoundBorder)currentBorder).getInsideBorder();
        } else {
            paddingPart = BorderFactory.createEmptyBorder(
                Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING,
                Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING);
        }
        Border defaultBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.BORDER_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(defaultBorder, paddingPart));
        if (!initialSetup && !field.getText().isEmpty()) {
            JTextField targetTextField = null;
            if (field == input1Field) {
                targetTextField = binaryInput1TextField;
            } else if (field == input2Field) {
                targetTextField = binaryInput2TextField;
            }
            if (targetTextField != null) {
                updateSecondaryRepresentation(field.getText(), targetTextField);
            }
        }
    }

    private void updateSecondaryRepresentation(String inputText, JTextField targetTextField) {
        if (baseSelector == null) {
            return;
        }
        String currentBase = (String) baseSelector.getSelectedItem();
        if (inputText.isEmpty() || !aluLogic.isValidInput(inputText, currentBase)) {
            targetTextField.setText(DEFAULT_BINARY_STRING);
            targetTextField.setForeground(Ui.TEXT_LIGHT);
            return;
        }
        try {
            int value = parseInputNumber(inputText);
            String display;
            if ("BINARY".equalsIgnoreCase(currentBase)) {
                display = Integer.toString(value);
            } else {
                String binary = Integer.toBinaryString(value);
                if (binary.length() > 8) {
                    binary = binary.substring(binary.length() - 8);
                } else {
                    binary = String.format("%8s", binary).replace(' ', '0');
                }
                display = binary;
            }
            targetTextField.setText(display);
            targetTextField.setForeground(Ui.TEXT_LIGHT);
        } catch (NumberFormatException e) {
            targetTextField.setText(DEFAULT_BINARY_STRING);
            targetTextField.setForeground(Ui.TEXT_LIGHT);
        }
    }

    private void setupOperations() {
        binaryOperations = new HashMap<>();
        binaryOperations.put("ADD", aluLogic::add);
        binaryOperations.put("SUBTRACT", aluLogic::subtract);
        binaryOperations.put("MULTIPLY", aluLogic::multiply);
        binaryOperations.put("AND", aluLogic::and);
        binaryOperations.put("OR", aluLogic::or);

        unaryOperations = new HashMap<>();
        unaryOperations.put("NOT", aluLogic::not);
    }

    private void performOperation() {
        try {
            String input1Str = input1Field.getText();
            String input2Str = input2Field.getText();
            OperationItem selectedOperationItem = (OperationItem) operationCombo.getSelectedItem();
            String operationKey = selectedOperationItem.getKey();

            int num1 = parseInputNumber(input1Str);

            if (unaryOperations.containsKey(operationKey)) {
                Function<Integer, Integer> operation = unaryOperations.get(operationKey);
                int calcResult = operation.apply(num1);
                displayResult(calcResult);
                aluLogic.addToHistory(selectedOperationItem.toString(), num1, 0, calcResult, (String) baseSelector.getSelectedItem());
            } else if (binaryOperations.containsKey(operationKey)) {
                if (input2Str.isEmpty()) {
                    showError("Input B is required for " + selectedOperationItem.toString() + " operation.");
                    input2Field.requestFocus();
                    return;
                }
                int num2 = parseInputNumber(input2Str);
                BiFunction<Integer, Integer, Integer> operation = binaryOperations.get(operationKey);
                int calcResult = operation.apply(num1, num2);
                displayResult(calcResult);
                aluLogic.addToHistory(selectedOperationItem.toString(), num1, num2, calcResult, (String) baseSelector.getSelectedItem());
            }
        } catch (NumberFormatException e) {
            showError("Invalid number format. Please check your inputs for the selected base: " + baseSelector.getSelectedItem());
        } catch (ArithmeticException e) {
            showError("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Error: " + e.getMessage());
        }
    }

    private int parseInputNumber(String input) {
        if (input.isEmpty()) {
            throw new NumberFormatException("Input cannot be empty.");
        }
        String currentBase = (String) baseSelector.getSelectedItem();
        try {
            switch (currentBase.toUpperCase()) {
                case "BINARY":
                    return Integer.parseInt(input, 2);
                case "DECIMAL":
                default:
                    return Integer.parseInt(input, 10);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid input '" + input + "' for base " + currentBase);
        }
    }

    private void displayResult(int calcResult) {
        resultField.setText(Integer.toString(calcResult));
        resultField.setForeground(Ui.TEXT_LIGHT); 
    }

    private void showError(String message) {
        resultField.setText(""); 
        if (binaryResultTextField != null) binaryResultTextField.setText("");
        Ui.showErrorDialog(this, message, "Input Error");
    }

    private void animateResultField() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        final Color originalColor = resultField.getForeground();
        final Color animationColor = Ui.ACCENT_PURPLE.brighter();
        resultField.setForeground(animationColor);
        if (binaryResultTextField != null) binaryResultTextField.setForeground(animationColor);
        animationTimer = new Timer(RESULT_ANIMATION_DURATION / 10, new ActionListener() {
            private int step = 0;
            private final int totalSteps = 10;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (step >= totalSteps) {
                    resultField.setForeground(originalColor);
                    if (binaryResultTextField != null) binaryResultTextField.setForeground(originalColor);
                    ((Timer)e.getSource()).stop();
                    return;
                }
                float ratio = (float)step / totalSteps;
                int r = (int)(animationColor.getRed() * (1-ratio) + originalColor.getRed() * ratio);
                int g = (int)(animationColor.getGreen() * (1-ratio) + originalColor.getGreen() * ratio);
                int b = (int)(animationColor.getBlue() * (1-ratio) + originalColor.getBlue() * ratio);
                Color intermediate = new Color(r,g,b);
                resultField.setForeground(intermediate);
                if (binaryResultTextField != null) binaryResultTextField.setForeground(intermediate);
                step++;
            }
        });
        animationTimer.setRepeats(true);
        animationTimer.start();
    }

    private void showConfirmDialog(Component parent, String message, String title, Runnable onConfirm) {
        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        JPanel panel = Ui.createRoundedPanel();
        panel.setBackground(Ui.PANEL_BACKGROUND);
        panel.setLayout(new BorderLayout(0, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new Ui.RoundedBorder(Ui.GENERAL_BORDER_RADIUS, Ui.ACCENT_CORAL),
            BorderFactory.createEmptyBorder(36, 48, 36, 48)
        ));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setFont(Ui.POPPINS_FONT.deriveFont(Font.BOLD, 16f));
        messageLabel.setForeground(Ui.ACCENT_CORAL);

        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setOpaque(false);
        msgPanel.add(iconLabel, BorderLayout.WEST);
        msgPanel.add(messageLabel, BorderLayout.CENTER);
        panel.add(msgPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        btnPanel.setOpaque(false);
        JButton yesButton = Ui.createModernButton("Yes");
        yesButton.addActionListener(ev -> {
            dialog.dispose();
            if (onConfirm != null) onConfirm.run();
        });
        JButton noButton = Ui.createModernButton("No");
        noButton.setBackground(Ui.ACCENT_CORAL);
        noButton.addActionListener(ev -> dialog.dispose());
        btnPanel.add(yesButton);
        btnPanel.add(noButton);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}