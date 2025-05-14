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
    public static final Color APP_THEME_COLOR = new Color(52, 120, 246);
    public static final Color APP_LIGHT_THEME_COLOR = new Color(220, 235, 255);
    public static final Color FOCUS_HIGHLIGHT_COLOR = new Color(120, 180, 255);
    public static final Color BUTTON_HOVER_COLOR = new Color(42, 100, 210);
    public static final Color DEFAULT_PANEL_BACKGROUND = new Color(250, 252, 255);
    public static final Color FRAME_BACKGROUND = new Color(242, 246, 252);
    public static final Color FIELD_BACKGROUND_DARK = new Color(255, 255, 255);
    public static final Color TEXT_LIGHT = new Color(30, 40, 60);
    public static final Color LABEL_TEXT_LIGHT = new Color(110, 130, 160);
    public static final Color CALCULATE_BUTTON_BG_LIGHT = APP_THEME_COLOR;
    public static final Color CALCULATE_BUTTON_FG_DARK = Color.WHITE;
    public static final Color HISTORY_PANEL_BG_GREEN = FRAME_BACKGROUND;
    public static final Color HISTORY_TEXT_DARK = TEXT_LIGHT;
    public static final Color DARK_THEME_FOCUS_BORDER_COLOR = FOCUS_HIGHLIGHT_COLOR;
    public static final Color SUBTLE_BORDER_COLOR = new Color(220, 230, 245); 
    public static final Color SHADOW_COLOR = new Color(52, 120, 246, 30); 

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 48);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 26);
    public static final Font COMPONENT_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font SEGOE_UI_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 20);
    public static final Font SEGOE_UI_BOLD_14 = new Font("Segoe UI", Font.BOLD, 20);

    public static final int GENERAL_BORDER_RADIUS = 32;
    public static final int TEXT_FIELD_BORDER_RADIUS = 18;
    public static final int TEXT_FIELD_HORIZONTAL_PADDING = 22;
    public static final int TEXT_FIELD_VERTICAL_PADDING = 14;
    public static final int COMBO_BOX_RENDERER_PADDING_VERTICAL = 14;
    public static final int COMBO_BOX_RENDERER_PADDING_HORIZONTAL = 18;
    public static final int CALCULATE_BUTTON_HORIZONTAL_PADDING = 38;
    public static final int CALCULATE_BUTTON_VERTICAL_PADDING = 18;

    public static JPanel createRoundedPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g2.setColor(SHADOW_COLOR);
                g2.fillRoundRect(6, 8, width - 12, height - 12, GENERAL_BORDER_RADIUS + 12, GENERAL_BORDER_RADIUS + 12);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, width, height, GENERAL_BORDER_RADIUS, GENERAL_BORDER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBackground(DEFAULT_PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return panel;
    }

    public static JTextField createModernTextField() {
        JTextField field = new JTextField(15);
        Border roundedPart = new RoundedBorder(TEXT_FIELD_BORDER_RADIUS, SUBTLE_BORDER_COLOR, SUBTLE_BORDER_COLOR);
        Border paddingPart = BorderFactory.createEmptyBorder(
                TEXT_FIELD_VERTICAL_PADDING, TEXT_FIELD_HORIZONTAL_PADDING,
                TEXT_FIELD_VERTICAL_PADDING, TEXT_FIELD_HORIZONTAL_PADDING
        );
        field.setBorder(BorderFactory.createCompoundBorder(roundedPart, paddingPart));
        field.setFont(SEGOE_UI_PLAIN_14);
        field.setOpaque(true);
        field.setBackground(FIELD_BACKGROUND_DARK);
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(APP_THEME_COLOR);
        field.setFocusable(true);
        return field;
    }

    public static void setInputFieldFocusBehavior(JTextField... fields) {
        for (JTextField field : fields) {
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    field.setBackground(new Color(245, 250, 255));
                }
                @Override
                public void focusLost(FocusEvent e) {
                    field.setBackground(FIELD_BACKGROUND_DARK);
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
                g2.fillRoundRect(4, 6, width - 8, height - 8, GENERAL_BORDER_RADIUS, GENERAL_BORDER_RADIUS);
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_HOVER_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, width, height, GENERAL_BORDER_RADIUS, GENERAL_BORDER_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(APP_THEME_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(SEGOE_UI_BOLD_14);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setFocusable(false); 
        button.setBorder(BorderFactory.createEmptyBorder(
            CALCULATE_BUTTON_VERTICAL_PADDING,
            CALCULATE_BUTTON_HORIZONTAL_PADDING,
            CALCULATE_BUTTON_VERTICAL_PADDING,
            CALCULATE_BUTTON_HORIZONTAL_PADDING
        ));
        button.setPreferredSize(null);
        button.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                button.setBorder(BorderFactory.createEmptyBorder(
                    CALCULATE_BUTTON_VERTICAL_PADDING,
                    CALCULATE_BUTTON_HORIZONTAL_PADDING,
                    CALCULATE_BUTTON_VERTICAL_PADDING,
                    CALCULATE_BUTTON_HORIZONTAL_PADDING
                ));
            }
        });
        button.addMouseListener(new HoverEffect(button, APP_THEME_COLOR, BUTTON_HOVER_COLOR));
        return button;
    }

    public static <E> JComboBox<E> createModernComboBox(E[] items) {
        JComboBox<E> combo = new JComboBox<>(items);
        combo.setRenderer(new ModernComboBoxRenderer());
        combo.setBackground(DEFAULT_PANEL_BACKGROUND);
        combo.setForeground(APP_THEME_COLOR);
        combo.setFont(SEGOE_UI_PLAIN_14);
        combo.setBorder(new RoundedBorder(TEXT_FIELD_BORDER_RADIUS, SUBTLE_BORDER_COLOR, FOCUS_HIGHLIGHT_COLOR));
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
        panel.setBackground(FRAME_BACKGROUND);
        panel.setLayout(new BorderLayout(0, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(GENERAL_BORDER_RADIUS, APP_THEME_COLOR, APP_THEME_COLOR),
            BorderFactory.createEmptyBorder(36, 48, 36, 48)
        ));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setFont(SEGOE_UI_BOLD_14.deriveFont(Font.BOLD, 20f));
        messageLabel.setForeground(APP_THEME_COLOR);

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
        private final Color defaultBorderColor;
        public RoundedBorder(int radius, Color defaultBorderColor, Color focusBorderColor) {
            this.radius = radius;
            this.defaultBorderColor = defaultBorderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(defaultBorderColor);
            int offset = (int) (1.2f / 2);
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
                    COMBO_BOX_RENDERER_PADDING_VERTICAL,
                    COMBO_BOX_RENDERER_PADDING_HORIZONTAL,
                    COMBO_BOX_RENDERER_PADDING_VERTICAL,
                    COMBO_BOX_RENDERER_PADDING_HORIZONTAL
            ));
            if (isSelected) {
                label.setBackground(APP_THEME_COLOR);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(FIELD_BACKGROUND_DARK);
                label.setForeground(TEXT_LIGHT);
            }
            label.setFocusable(false);
            label.setOpaque(true);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
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