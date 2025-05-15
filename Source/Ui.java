package Source;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Ui {
    public static final Color APP_GRADIENT_START = new Color(255, 222, 233); 
    public static final Color APP_GRADIENT_END = new Color(222, 235, 255);   
    public static final Color ACCENT_PURPLE = new Color(140, 82, 255);
    public static final Color ACCENT_TEAL = new Color(0, 210, 190);
    public static final Color ACCENT_YELLOW = new Color(255, 221, 87);
    public static final Color ACCENT_CORAL = new Color(255, 107, 107);
    public static final Color ACCENT_GREEN = new Color(0, 220, 130);
    public static final Color BUTTON_GRADIENT_START = new Color(140, 82, 255);
    public static final Color BUTTON_GRADIENT_END = new Color(0, 210, 190);
    public static final Color BUTTON_HOVER_GRADIENT_START = new Color(120, 60, 220);
    public static final Color BUTTON_HOVER_GRADIENT_END = new Color(0, 180, 170);
    public static final Color FRAME_BACKGROUND = new Color(248, 249, 255);
    public static final Color PANEL_BACKGROUND = new Color(255, 255, 255, 230);
    public static final Color FIELD_BACKGROUND = new Color(255, 255, 255); 
    public static final Color FIELD_BACKGROUND_FOCUS = new Color(245, 245, 255); 
    public static final Color TEXT_DARK = new Color(40, 40, 60);
    public static final Color TEXT_LIGHT = new Color(120, 120, 140);
    public static final Color BORDER_COLOR = new Color(230, 230, 245);
    public static final Color SHADOW_COLOR = new Color(140, 82, 255, 30);
    public static final Color X_BUTTON_BG = new Color(255, 107, 107, 180);
    public static final Color X_BUTTON_BG_HOVER = new Color(255, 107, 107, 230);

    public static final Font POPPINS_FONT = new Font("Poppins", Font.PLAIN, 16);
    public static final Font POPPINS_BOLD = new Font("Poppins", Font.BOLD, 16);
    public static final Font TITLE_FONT = new Font("Poppins", Font.BOLD, 32);
    public static final Font SUBTITLE_FONT = new Font("Poppins", Font.PLAIN, 16);
    public static final Font COMPONENT_LABEL_FONT = new Font("Poppins", Font.BOLD, 14);

    public static final int GENERAL_BORDER_RADIUS = 28;
    public static final int TEXT_FIELD_BORDER_RADIUS = 16;
    public static final int TEXT_FIELD_HORIZONTAL_PADDING = 16;
    public static final int TEXT_FIELD_VERTICAL_PADDING = 10;
    public static final int BUTTON_BORDER_RADIUS = 16;
    public static final int BUTTON_VERTICAL_PADDING = 8;
    public static final int BUTTON_HORIZONTAL_PADDING = 18;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Font poppins = new Font("Poppins", Font.PLAIN, 14);
            if (poppins.getFamily().equals("Poppins")) {
                UIManager.put("Label.font", poppins);
                UIManager.put("Button.font", poppins);
                UIManager.put("TextField.font", poppins);
                UIManager.put("ComboBox.font", poppins);
                UIManager.put("List.font", poppins);
            }
        } catch (Exception ignored) {}
    }

    public static JPanel createRoundedPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g2.setColor(SHADOW_COLOR);
                g2.fillRoundRect(8, 10, width - 16, height - 16, GENERAL_BORDER_RADIUS + 8, GENERAL_BORDER_RADIUS + 8);
                GradientPaint gp = new GradientPaint(0, 0, APP_GRADIENT_START, width, height, APP_GRADIENT_END);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, GENERAL_BORDER_RADIUS, GENERAL_BORDER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return panel;
    }

    public static JTextField createModernTextField() {
        JTextField field = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        Border roundedPart = new RoundedBorder(TEXT_FIELD_BORDER_RADIUS, BORDER_COLOR);
        Border paddingPart = BorderFactory.createEmptyBorder(
                TEXT_FIELD_VERTICAL_PADDING, TEXT_FIELD_HORIZONTAL_PADDING,
                TEXT_FIELD_VERTICAL_PADDING, TEXT_FIELD_HORIZONTAL_PADDING
        );
        field.setBorder(BorderFactory.createCompoundBorder(roundedPart, paddingPart));
        field.setFont(POPPINS_FONT);
        field.setOpaque(true);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(ACCENT_PURPLE);
        field.setFocusable(true);
        field.setSelectionColor(new Color(ACCENT_PURPLE.getRed(), ACCENT_PURPLE.getGreen(), ACCENT_PURPLE.getBlue(), 60));
        return field;
    }

    public static void setInputFieldFocusBehavior(JTextField... fields) {
        for (JTextField field : fields) {
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    field.setBackground(FIELD_BACKGROUND_FOCUS);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    field.setBackground(FIELD_BACKGROUND);
                }
            });
        }
    }

    public static JButton createModernButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g2.setColor(SHADOW_COLOR);
                g2.fillRoundRect(4, 8, width - 8, height - 8, BUTTON_BORDER_RADIUS + 4, BUTTON_BORDER_RADIUS + 4);
                ButtonModel model = getModel();
                GradientPaint gp;
                if (model.isPressed()) {
                    gp = new GradientPaint(0, 0, BUTTON_HOVER_GRADIENT_START.darker(), width, height, BUTTON_HOVER_GRADIENT_END.darker());
                } else if (model.isRollover()) {
                    gp = new GradientPaint(0, 0, BUTTON_HOVER_GRADIENT_START, width, height, BUTTON_HOVER_GRADIENT_END);
                } else {
                    gp = new GradientPaint(0, 0, BUTTON_GRADIENT_START, width, height, BUTTON_GRADIENT_END);
                }
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, BUTTON_BORDER_RADIUS, BUTTON_BORDER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(BUTTON_GRADIENT_START);
        button.setForeground(Color.WHITE);
        button.setFont(POPPINS_BOLD);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder(
            BUTTON_VERTICAL_PADDING,
            BUTTON_HORIZONTAL_PADDING,
            BUTTON_VERTICAL_PADDING,
            BUTTON_HORIZONTAL_PADDING
        ));
        return button;
    }

    public static <E> JComboBox<E> createModernComboBox(E[] items) {
        JComboBox<E> combo = new JComboBox<>(items);
        combo.setRenderer(new ModernComboBoxRenderer());
        combo.setBackground(PANEL_BACKGROUND);
        combo.setForeground(ACCENT_PURPLE);
        combo.setFont(POPPINS_FONT);
        combo.setBorder(new RoundedBorder(TEXT_FIELD_BORDER_RADIUS, BORDER_COLOR));
        combo.setOpaque(true);
        combo.setFocusable(false);
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setFocusable(false);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return button;
            }
        });
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return combo;
    }

    public static void showErrorDialog(Component parent, String message, String title) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        JPanel panel = createRoundedPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setLayout(new BorderLayout(0, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(GENERAL_BORDER_RADIUS, ACCENT_CORAL),
            BorderFactory.createEmptyBorder(36, 48, 36, 48)
        ));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setFont(POPPINS_BOLD.deriveFont(Font.BOLD, 20f));
        messageLabel.setForeground(ACCENT_CORAL);

        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setOpaque(false);
        msgPanel.add(iconLabel, BorderLayout.WEST);
        msgPanel.add(messageLabel, BorderLayout.CENTER);
        panel.add(msgPanel, BorderLayout.CENTER);

        JButton okButton = createModernButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okButton);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;
        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(borderColor);
            int offset = (int) (1.5f / 2);
            g2.drawRoundRect(x + offset, y + offset,
                             width - (offset * 2) - 1, height - (offset * 2) - 1,
                             radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int ins = Math.max(radius / 3, (int) Math.ceil(1.5 / 2.0) + 2);
            return new Insets(ins, ins, ins, ins);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            int ins = Math.max(radius / 3, (int) Math.ceil(1.5 / 2.0) + 2);
            insets.left = insets.top = insets.right = insets.bottom = ins;
            return insets;
        }
    }

    public static class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(
                    12, 22, 12, 22
            ));
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(ACCENT_TEAL);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(TEXT_DARK);
            }
            label.setFocusable(false);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.setFont(POPPINS_FONT);
            label.setText(value != null ? value.toString() : "");
            return label;
        }
    }

    public static class HoverEffect extends MouseAdapter {
        private final JButton button;
        private final Color originalColor;
        private final Color hoverColor;

        public HoverEffect(JButton button, Color originalColor, Color hoverColor) {
            this.button = button;
            this.originalColor = originalColor;
            this.hoverColor = hoverColor;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            button.setBackground(hoverColor);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(originalColor);
        }
    }
}