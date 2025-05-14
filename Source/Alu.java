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
    private static final Font BINARY_TEXT_FIELD_FONT = new Font("Consolas", Font.PLAIN, 16);
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
        setupKeyboardShortcuts();
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
        historyModel.clear();
        for (String entry : history) {
            if (entry != null) {
                historyModel.addElement(entry);
            }
        }
        if (historyModel.getSize() > 0) {
            historyList.ensureIndexIsVisible(historyModel.getSize() - 1);
        }
    }

    private JTextField createStyledTextField(boolean editable) {
        JTextField field = new JTextField(15);
        field.setOpaque(true); 
        field.setBackground(Ui.FIELD_BACKGROUND_DARK);
        field.setForeground(Ui.TEXT_LIGHT);
        field.setFont(Ui.SEGOE_UI_PLAIN_14);
        field.setCaretColor(Ui.TEXT_LIGHT);
        field.setEditable(editable);
        field.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        field.setHorizontalAlignment(JTextField.LEFT);

        Border roundedPart = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.SUBTLE_BORDER_COLOR, Ui.DARK_THEME_FOCUS_BORDER_COLOR);
        Border paddingPart = BorderFactory.createEmptyBorder(
                Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING,
                Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING);
        field.setBorder(BorderFactory.createCompoundBorder(roundedPart, paddingPart));

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.repaint(); }
            @Override public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private OperationItem[] getStyledOperationItems() {
        return new OperationItem[]{
            new OperationItem("ADD", "ADD"),
            new OperationItem("SUBTRACT", "SUBTRACT"),
            new OperationItem("MULTIPLY", "MULTIPLY"),
            new OperationItem("DIVIDE", "DIVIDE"),
            new OperationItem("AND", "AND"),
            new OperationItem("OR", "OR"),
            new OperationItem("NOT", "NOT")
        };
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setOpaque(true);
        combo.setBackground(Ui.FIELD_BACKGROUND_DARK);
        combo.setForeground(Ui.TEXT_LIGHT);
        combo.setFont(Ui.SEGOE_UI_PLAIN_14);
        combo.setPreferredSize(new Dimension(0, 44));

        Border roundedPart = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.SUBTLE_BORDER_COLOR, Ui.DARK_THEME_FOCUS_BORDER_COLOR);
        combo.setBorder(roundedPart);

        if (!(combo.getRenderer() instanceof Ui.ModernComboBoxRenderer)) {
            combo.setRenderer(new Ui.ModernComboBoxRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    label.setBackground(Ui.FIELD_BACKGROUND_DARK);
                    label.setForeground(isSelected ? Color.WHITE : Ui.TEXT_LIGHT);
                    label.setFont(Ui.SEGOE_UI_PLAIN_14);
                    label.setBorder(BorderFactory.createEmptyBorder(Ui.COMBO_BOX_RENDERER_PADDING_VERTICAL, Ui.COMBO_BOX_RENDERER_PADDING_HORIZONTAL, Ui.COMBO_BOX_RENDERER_PADDING_VERTICAL, Ui.COMBO_BOX_RENDERER_PADDING_HORIZONTAL));
                    if (isSelected) {
                        label.setBackground(Ui.APP_THEME_COLOR.darker());
                    }
                    return label;
                }
            });
        }

        for (Component child : combo.getComponents()) {
            if (child instanceof JButton) {
                JButton arrowButton = (JButton) child;
                arrowButton.setOpaque(false);
                arrowButton.setContentAreaFilled(false);
                arrowButton.setBorderPainted(false);
                arrowButton.setBackground(Ui.FIELD_BACKGROUND_DARK);
                arrowButton.setPreferredSize(new Dimension(44, 44));
                break;
            }
        }
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
        label.setForeground(Ui.LABEL_TEXT_LIGHT);
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
        setLayout(new BorderLayout(15, 0));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 40)); 

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Ui.FRAME_BACKGROUND);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("ALU Calculator");
        titleLabel.setFont(Ui.TITLE_FONT);
        titleLabel.setForeground(Ui.TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Arithmetic and Logic Operation Tool");
        subtitleLabel.setFont(Ui.SUBTITLE_FONT);
        subtitleLabel.setForeground(Ui.LABEL_TEXT_LIGHT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 2))); 
        headerPanel.add(subtitleLabel);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(true);
        centerPanel.setBackground(Ui.FRAME_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setOpaque(true);
        mainContentPanel.setBackground(Ui.FRAME_BACKGROUND);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        input1Field = createStyledTextField(true);
        input1Field.setToolTipText("Enter first number");
        input2Field = createStyledTextField(true);
        input2Field.setToolTipText("Enter second number");
        resultField = createStyledTextField(false);
        resultField.setToolTipText("Decimal result of the operation");

        binaryInput1TextField = createStyledTextField(false);
        binaryInput1TextField.setFont(BINARY_TEXT_FIELD_FONT);
        binaryInput1TextField.setText(DEFAULT_BINARY_STRING);
        binaryInput1TextField.setHorizontalAlignment(JTextField.LEFT);
        binaryInput1TextField.setToolTipText("Secondary representation of Input A");

        binaryInput2TextField = createStyledTextField(false);
        binaryInput2TextField.setFont(BINARY_TEXT_FIELD_FONT);
        binaryInput2TextField.setText(DEFAULT_BINARY_STRING);
        binaryInput2TextField.setHorizontalAlignment(JTextField.LEFT);
        binaryInput2TextField.setToolTipText("Secondary representation of Input B");        
        binaryResultTextField = createStyledTextField(false);
        binaryResultTextField.setFont(Ui.SEGOE_UI_PLAIN_14);
        binaryResultTextField.setHorizontalAlignment(JTextField.LEFT);
        binaryResultTextField.setToolTipText("Binary representation of the result");
        binaryResultTextField.setBackground(Ui.FIELD_BACKGROUND_DARK);

        baseSelector = new JComboBox<>(new String[]{"DECIMAL", "BINARY"});
        styleComboBox(baseSelector);
        baseSelector.setToolTipText("Select input number system");
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
        operationCombo = new JComboBox<>(getStyledOperationItems());
        styleComboBox(operationCombo);
        operationCombo.setToolTipText("Select operation to perform");
        
        int currentY = 0;
        secondaryLabelA = new JLabel();
        secondaryLabelA.setFont(Ui.COMPONENT_LABEL_FONT);
        secondaryLabelA.setForeground(Ui.LABEL_TEXT_LIGHT);
        secondaryLabelB = new JLabel();
        secondaryLabelB.setFont(Ui.COMPONENT_LABEL_FONT);
        secondaryLabelB.setForeground(Ui.LABEL_TEXT_LIGHT);
        updateSecondaryLabels();
        currentY = addLabeledComponentToPanel(mainContentPanel, gbc, "Input A:", input1Field, 0, currentY);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 14, 5, 14); 
        mainContentPanel.add(secondaryLabelA, gbc);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 14, 16, 14); 
        mainContentPanel.add(binaryInput1TextField, gbc);
        addLabeledComponentToPanel(mainContentPanel, gbc, "Select Number System", baseSelector, 2, 0);

        currentY = addLabeledComponentToPanel(mainContentPanel, gbc, "Input B:", input2Field, 0, currentY);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(16, 14, 5, 14); 
        mainContentPanel.add(secondaryLabelB, gbc);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 14, 16, 14); 
        mainContentPanel.add(binaryInput2TextField, gbc);
        addLabeledComponentToPanel(mainContentPanel, gbc, "Select Operation", operationCombo, 2, 2);        

        int fieldHeight = input1Field.getPreferredSize().height;

        int buttonHeight = fieldHeight; 
        int dropdownWidth = baseSelector.getPreferredSize().width; 
        int groupButtonWidth = dropdownWidth;
        int buttonGap = 8;
        int buttonWidth = (groupButtonWidth - buttonGap) / 2;
        calculateButton = new JButton("Calculate") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                GradientPaint gradient = new GradientPaint(
                    0, 0, Ui.APP_THEME_COLOR,
                    0, height, Ui.APP_THEME_COLOR.darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, width, height, 20, 20);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, width, height/2, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        calculateButton.setOpaque(false);
        calculateButton.setContentAreaFilled(false);
        calculateButton.setBorderPainted(false);
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(Ui.SEGOE_UI_BOLD_14.deriveFont(Font.BOLD, 17f)); 
        calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateButton.addActionListener(e -> performOperation());
        calculateButton.setToolTipText("Perform calculation (Alt+Enter or specific operation shortcut)");
        calculateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                calculateButton.setForeground(new Color(255, 255, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                calculateButton.setForeground(Color.WHITE);
            }
        });
        calculateButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        calculateButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        calculateButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        calculateButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 

        JButton clearButton = new JButton("Clear") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(200, 0, 80),
                    0, height, new Color(150, 0, 60)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, width, height, 20, 20);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, width, height/2, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        clearButton.setOpaque(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setBorderPainted(false);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(Ui.SEGOE_UI_BOLD_14.deriveFont(Font.BOLD, 17f)); 
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setToolTipText("Clear all fields");
        clearButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        clearButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        clearButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        clearButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        clearButton.addActionListener(e -> {
            input1Field.setText("");
            input2Field.setText("");
            resultField.setText("");
            binaryInput1TextField.setText(DEFAULT_BINARY_STRING);
            binaryInput2TextField.setText(DEFAULT_BINARY_STRING);
            binaryResultTextField.setText("");
            input1Field.requestFocus();
        });

        JPanel groupButtonPanel = new JPanel(new GridBagLayout());
        groupButtonPanel.setOpaque(false);
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        gbcBtn.weightx = 0.5;
        gbcBtn.fill = GridBagConstraints.BOTH;
        gbcBtn.insets = new Insets(0, 0, 0, buttonGap/2);
        groupButtonPanel.add(calculateButton, gbcBtn);
        gbcBtn.gridx = 1;
        gbcBtn.insets = new Insets(0, buttonGap/2, 0, 0);
        groupButtonPanel.add(clearButton, gbcBtn);
        groupButtonPanel.setPreferredSize(new Dimension(groupButtonWidth, buttonHeight));
        groupButtonPanel.setMinimumSize(new Dimension(groupButtonWidth, buttonHeight));
        groupButtonPanel.setMaximumSize(new Dimension(groupButtonWidth, buttonHeight));

        Font resultFont = Ui.SEGOE_UI_PLAIN_14;
        resultField.setFont(resultFont);
        binaryResultTextField.setFont(resultFont);

        Border resultBorder = BorderFactory.createCompoundBorder(
            new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.SUBTLE_BORDER_COLOR, Ui.DARK_THEME_FOCUS_BORDER_COLOR),
            BorderFactory.createEmptyBorder(Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING, Ui.TEXT_FIELD_VERTICAL_PADDING, Ui.TEXT_FIELD_HORIZONTAL_PADDING)
        );
        resultField.setBorder(resultBorder);
        binaryResultTextField.setBorder(resultBorder);
        resultField.setBackground(Ui.FIELD_BACKGROUND_DARK);
        binaryResultTextField.setBackground(Ui.FIELD_BACKGROUND_DARK); 
        Dimension resultSize = resultField.getPreferredSize();
        binaryResultTextField.setPreferredSize(resultSize);
        binaryResultTextField.setMinimumSize(resultSize);
        binaryResultTextField.setMaximumSize(resultSize);
        resultField.setPreferredSize(resultSize);
        resultField.setMinimumSize(resultSize);
        resultField.setMaximumSize(resultSize);
        groupButtonPanel.setPreferredSize(new Dimension(groupButtonWidth, resultSize.height));
        groupButtonPanel.setMinimumSize(new Dimension(groupButtonWidth, resultSize.height));
        groupButtonPanel.setMaximumSize(new Dimension(groupButtonWidth, resultSize.height));
        calculateButton.setPreferredSize(new Dimension(buttonWidth, resultSize.height));
        calculateButton.setMinimumSize(new Dimension(buttonWidth, resultSize.height));
        calculateButton.setMaximumSize(new Dimension(buttonWidth, resultSize.height));
        clearButton.setPreferredSize(new Dimension(buttonWidth, resultSize.height));
        clearButton.setMinimumSize(new Dimension(buttonWidth, resultSize.height));
        clearButton.setMaximumSize(new Dimension(buttonWidth, resultSize.height));

        int resultRowY = currentY;
        gbc.gridx = 0;
        gbc.gridy = resultRowY;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.BASELINE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 14, 5, 14);
        JLabel decimalLabel = new JLabel("Decimal Result");
        decimalLabel.setFont(Ui.COMPONENT_LABEL_FONT);
        decimalLabel.setForeground(Ui.LABEL_TEXT_LIGHT);
        mainContentPanel.add(decimalLabel, gbc);
        gbc.gridx = 1;
        JLabel binaryLabel = new JLabel("Binary Result");
        binaryLabel.setFont(Ui.COMPONENT_LABEL_FONT);
        binaryLabel.setForeground(Ui.LABEL_TEXT_LIGHT);
        mainContentPanel.add(binaryLabel, gbc);
        gbc.gridx = 2;
        JLabel emptyLabel = new JLabel("");
        mainContentPanel.add(emptyLabel, gbc);

        gbc.gridy = resultRowY + 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 14, 16, 14);
        gbc.anchor = GridBagConstraints.BASELINE; 
        mainContentPanel.add(resultField, gbc);
        gbc.gridx = 1;
        mainContentPanel.add(binaryResultTextField, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 14, 16, 14);
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
        centerGbc.insets = new Insets(0, 0, 0, 10); 
        centerPanel.add(calculationsPanel, centerGbc);
        
        JPanel historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));
        historyContainer.setOpaque(false);
        historyContainer.add(Box.createVerticalGlue());
        historyPanel.setMaximumSize(new Dimension(350, 380));
        historyPanel.setPreferredSize(new Dimension(300, 380));
        historyPanel.setMinimumSize(new Dimension(250, 200));
        historyContainer.add(historyPanel);
        historyContainer.add(Box.createVerticalGlue());

        centerGbc.gridx = 1;
        centerGbc.gridy = 0;
        centerGbc.weightx = 0.35;
        centerGbc.weighty = 1.0;
        centerGbc.fill = GridBagConstraints.BOTH;
        centerGbc.insets = new Insets(0, 10, 0, 0); 
        centerPanel.add(historyContainer, centerGbc);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBackground(Ui.FRAME_BACKGROUND);
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16))); 
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
        setMinimumSize(new Dimension(960, 720));
        setLocationRelativeTo(null);
    }

    private void updateSecondaryLabels() {
        String base = (String) baseSelector.getSelectedItem();
        if (base == null) base = "DECIMAL";
        switch (base.toUpperCase()) {
            case "BINARY":
                secondaryLabelA.setText("Decimal A:");
                secondaryLabelB.setText("Decimal B:");
                binaryInput1TextField.setToolTipText("Decimal representation of Input A");
                binaryInput2TextField.setToolTipText("Decimal representation of Input B");
                break;
            case "DECIMAL":
            default:
                secondaryLabelA.setText("Binary A:");
                secondaryLabelB.setText("Binary B:");
                binaryInput1TextField.setToolTipText("Binary representation of Input A");
                binaryInput2TextField.setToolTipText("Binary representation of Input B");
                break;
        }
    }

    private void setupNewHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout(5,5));
        historyPanel.setOpaque(true);
        historyPanel.setBackground(Ui.FRAME_BACKGROUND);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            new Ui.RoundedBorder(Ui.GENERAL_BORDER_RADIUS, Ui.SUBTLE_BORDER_COLOR, Ui.FOCUS_HIGHLIGHT_COLOR), 
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            
        historyPanel.setPreferredSize(new Dimension(300, 0));
        historyPanel.setMinimumSize(new Dimension(250, 0));        
        JPanel historyTitlePanel = new JPanel(new BorderLayout());
        historyTitlePanel.setBackground(Ui.FRAME_BACKGROUND);
        historyTitlePanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        
        JLabel historyTitleLabel = new JLabel("Calculation History");
        historyTitleLabel.setFont(Ui.SEGOE_UI_BOLD_14.deriveFont(Font.BOLD, 19f));
        historyTitleLabel.setForeground(Ui.APP_THEME_COLOR);
        historyTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        historyTitlePanel.add(historyTitleLabel, BorderLayout.CENTER);
        
        historyPanel.add(historyTitlePanel, BorderLayout.NORTH);        
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setFont(new Font("Inter", Font.PLAIN, 16));
        historyList.setBackground(Ui.FRAME_BACKGROUND);
        historyList.setForeground(Ui.TEXT_LIGHT);
        historyList.setSelectionBackground(new Color(233, 233, 249)); 
        historyList.setFixedCellHeight(28); 
        historyList.setSelectionForeground(Ui.APP_THEME_COLOR);
        historyList.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); 
        historyList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(true);
            panel.setBackground(isSelected ? new Color(220,220,240) : Ui.FRAME_BACKGROUND);
            JLabel label = new JLabel(value);
            label.setFont(new Font("Inter", Font.PLAIN, 14));
            label.setForeground(isSelected ? Ui.APP_THEME_COLOR : Ui.TEXT_LIGHT);
            label.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
            panel.add(label, BorderLayout.CENTER);
            if (index == hoveredHistoryIndex) {
                JLabel xLabel = new JLabel("✕");
                xLabel.setFont(new Font("Dialog", Font.BOLD, 14));
                xLabel.setForeground(Color.RED.darker());
                xLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                panel.add(xLabel, BorderLayout.EAST);
            }
            return panel;
        });
        historyList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int idx = historyList.locationToIndex(e.getPoint());
                if (idx != hoveredHistoryIndex) {
                    hoveredHistoryIndex = idx;
                    historyList.repaint();
                }
            }
        });
        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredHistoryIndex = -1;
                historyList.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = historyList.locationToIndex(e.getPoint());
                if (idx >= 0 && idx < historyModel.size() && idx == hoveredHistoryIndex) {
                    Rectangle cellBounds = historyList.getCellBounds(idx, idx);
                    if (cellBounds != null) {
                        int xButtonWidth = 24; 
                        int xButtonX = cellBounds.x + cellBounds.width - xButtonWidth;
                        if (e.getX() >= xButtonX) {
                            aluLogic.removeHistoryEntry(idx);
                        }
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Ui.FRAME_BACKGROUND);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        JButton deleteAllButton = new JButton();
        deleteAllButton.setToolTipText("Clear all history");
        deleteAllButton.setFocusable(false);
        deleteAllButton.setContentAreaFilled(false);
        deleteAllButton.setBorderPainted(false);
        deleteAllButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        deleteAllButton.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int offsetX = x, offsetY = y;
                g2.setColor(new Color(180, 0, 0));
                g2.fillRoundRect(offsetX + 6, offsetY + 10, 16, 14, 6, 6);
                g2.setColor(new Color(120, 0, 0));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(offsetX + 6, offsetY + 10, 16, 14, 6, 6);
                g2.setColor(new Color(120, 0, 0));
                g2.fillRoundRect(offsetX + 8, offsetY + 6, 12, 6, 3, 3);
                g2.setColor(new Color(80, 0, 0));
                g2.drawRoundRect(offsetX + 8, offsetY + 6, 12, 6, 3, 3);
                g2.setColor(new Color(120, 0, 0));
                g2.fillRect(offsetX + 13, offsetY + 3, 4, 4);
                g2.setColor(new Color(255,255,255,200));
                for (int i = 0; i < 3; i++) {
                    g2.drawLine(offsetX + 10 + i*4, offsetY + 14, offsetX + 10 + i*4, offsetY + 20);
                }
                g2.dispose();
            }
            @Override
            public int getIconWidth() { return 32; }
            @Override
            public int getIconHeight() { return 32; }
        });
        deleteAllButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(historyPanel, "Clear all calculation history?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                aluLogic.clearHistory();
            }
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

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK), "CALCULATE_ACTION");
        actionMap.put("CALCULATE_ACTION", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performOperation();
            }
        });

        setupShortcut(inputMap, actionMap, "ADD", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        setupShortcut(inputMap, actionMap, "SUBTRACT", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        setupShortcut(inputMap, actionMap, "MULTIPLY", KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        setupShortcut(inputMap, actionMap, "DIVIDE", KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
    }

    private void setupShortcut(InputMap inputMap, ActionMap actionMap, String operationKey, KeyStroke keystroke) {
        String actionName = "PERFORM_" + operationKey;
        inputMap.put(keystroke, actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < operationCombo.getItemCount(); i++) {
                    if (operationCombo.getItemAt(i).getKey().equals(operationKey)) {
                        operationCombo.setSelectedIndex(i);
                        performOperation();
                        break;
                    }
                }
            }
        });
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
                    Border defaultBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.SUBTLE_BORDER_COLOR, Ui.DARK_THEME_FOCUS_BORDER_COLOR);
                    field.setBorder(BorderFactory.createCompoundBorder(defaultBorder, paddingPart));
                    field.setToolTipText("Enter a number in " + currentBase + " base");
                } else if (!aluLogic.isValidInput(currentText, currentBase)) {
                    Border errorBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Color.RED.darker(), Color.RED);
                    field.setBorder(BorderFactory.createCompoundBorder(errorBorder, paddingPart));
                    field.setToolTipText("Invalid " + currentBase + " number. Example: " + getExampleForBase(currentBase));
                } else {
                    Border validBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.DARK_THEME_FOCUS_BORDER_COLOR, Ui.DARK_THEME_FOCUS_BORDER_COLOR.brighter());
                    field.setBorder(BorderFactory.createCompoundBorder(validBorder, paddingPart));
                    field.setToolTipText("Valid " + currentBase + " input");
                    
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
        String currentBase = baseSelector != null ? (String) baseSelector.getSelectedItem() : "DECIMAL";
        Border defaultBorder = new Ui.RoundedBorder(Ui.TEXT_FIELD_BORDER_RADIUS, Ui.SUBTLE_BORDER_COLOR, Ui.DARK_THEME_FOCUS_BORDER_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(defaultBorder, paddingPart));
        field.setToolTipText("Enter a number in " + currentBase + " base");
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

    private String getExampleForBase(String base) {
        switch (base.toUpperCase()) {
            case "BINARY": return "0101";
            case "DECIMAL":
            default: return "123";
        }
    }

    private void setupOperations() {
        binaryOperations = new HashMap<>();
        binaryOperations.put("ADD", aluLogic::add);
        binaryOperations.put("SUBTRACT", aluLogic::subtract);
        binaryOperations.put("MULTIPLY", aluLogic::multiply);
        binaryOperations.put("DIVIDE", aluLogic::divide);
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
        final Color animationColor = Ui.APP_THEME_COLOR.brighter(); 
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
}