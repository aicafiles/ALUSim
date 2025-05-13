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

    public static final Color APP_THEME_COLOR = new Color(98, 0, 238); 
    public static final Color APP_LIGHT_THEME_COLOR = new Color(187, 134, 252); 
    public static final Color FOCUS_HIGHLIGHT_COLOR = new Color(3, 218, 198); 
    public static final Color BUTTON_HOVER_COLOR = new Color(55, 0, 179); 
    public static final Color DEFAULT_PANEL_BACKGROUND = new Color(245, 247, 250); 

    public static final Color FRAME_BACKGROUND = new Color(250, 250, 252); 
    public static final Color FIELD_BACKGROUND_DARK = FRAME_BACKGROUND;
    public static final Color TEXT_LIGHT = new Color(40, 40, 60); 
    public static final Color LABEL_TEXT_LIGHT = new Color(120, 120, 140); 
    public static final Color CALCULATE_BUTTON_BG_LIGHT = new Color(98, 0, 238);
    public static final Color CALCULATE_BUTTON_FG_DARK = Color.WHITE;
    public static final Color HISTORY_PANEL_BG_GREEN = FRAME_BACKGROUND;
    public static final Color HISTORY_TEXT_DARK = TEXT_LIGHT;
    public static final Color DARK_THEME_FOCUS_BORDER_COLOR = new Color(3, 218, 198); 
    public static final Color SUBTLE_BORDER_COLOR = new Color(224, 224, 224);     public static final Font TITLE_FONT = new Font("Inter", Font.BOLD, 40); 
    public static final Font SUBTITLE_FONT = new Font("Inter", Font.PLAIN, 22); 
    public static final Font COMPONENT_LABEL_FONT = new Font("Inter", Font.BOLD, 18); 

    public static final int GENERAL_BORDER_RADIUS = 24;
    public static final int TEXT_FIELD_BORDER_RADIUS = 14;

    public static final Font SEGOE_UI_PLAIN_14 = new Font("Inter", Font.PLAIN, 17);
    public static final Font SEGOE_UI_BOLD_14 = new Font("Inter", Font.BOLD, 17);    public static final int TEXT_FIELD_HORIZONTAL_PADDING = 16; 
    public static final int TEXT_FIELD_VERTICAL_PADDING = 10;

    public static final int COMBO_BOX_RENDERER_PADDING_VERTICAL = 10;
    public static final int COMBO_BOX_RENDERER_PADDING_HORIZONTAL = 14;

    public static final int CALCULATE_BUTTON_HORIZONTAL_PADDING = 28;
    public static final int CALCULATE_BUTTON_VERTICAL_PADDING = 14;

    public static JPanel createRoundedPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, GENERAL_BORDER_RADIUS, GENERAL_BORDER_RADIUS);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBackground(DEFAULT_PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    public static JTextField createModernTextField() {
        JTextField field = new JTextField(15);
        Border roundedPart = new RoundedBorder(TEXT_FIELD_BORDER_RADIUS, SUBTLE_BORDER_COLOR, FOCUS_HIGHLIGHT_COLOR);
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
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.repaint();
            }
        });
        return field;
    }

    public static JButton createModernButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, GENERAL_BORDER_RADIUS, GENERAL_BORDER_RADIUS);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, getHeight() - 6, getWidth() - 4, 6, GENERAL_BORDER_RADIUS, GENERAL_BORDER_RADIUS);
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(APP_THEME_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(SEGOE_UI_BOLD_14);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new HoverEffect(button, APP_THEME_COLOR, BUTTON_HOVER_COLOR));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(
            CALCULATE_BUTTON_VERTICAL_PADDING,
            CALCULATE_BUTTON_HORIZONTAL_PADDING,
            CALCULATE_BUTTON_VERTICAL_PADDING,
            CALCULATE_BUTTON_HORIZONTAL_PADDING
        ));
        button.setPreferredSize(null); 
        return button;
    }

    public static <E> JComboBox<E> createModernComboBox(E[] items) {
        JComboBox<E> combo = new JComboBox<>(items);
        combo.setRenderer(new ModernComboBoxRenderer());
        combo.setBackground(DEFAULT_PANEL_BACKGROUND);
        combo.setForeground(APP_THEME_COLOR);
        combo.setFont(SEGOE_UI_PLAIN_14);
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
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        JLabel messageLabel = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        messageLabel.setFont(SEGOE_UI_PLAIN_14.deriveFont(Font.BOLD, 15f));
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
        private final Color focusBorderColor;

        public RoundedBorder(int radius, Color defaultBorderColor, Color focusBorderColor) {
            this.radius = radius;
            this.defaultBorderColor = defaultBorderColor;
            this.focusBorderColor = focusBorderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Stroke currentStroke;
            if (c.isFocusOwner()) {
                g2.setColor(focusBorderColor);
                currentStroke = new BasicStroke(1.5f);
            } else {
                g2.setColor(defaultBorderColor);
                currentStroke = new BasicStroke(1f);
            }
            g2.setStroke(currentStroke);

            float strokeWidth = 1f;
            if (currentStroke instanceof BasicStroke) {
                strokeWidth = ((BasicStroke) currentStroke).getLineWidth();
            }

            int offset = (int) (strokeWidth / 2);
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

            if (cellHasFocus && !isSelected) {
                label.setBackground(FIELD_BACKGROUND_DARK.brighter());
                label.setForeground(TEXT_LIGHT.brighter());
            }
            list.setBackground(FIELD_BACKGROUND_DARK);
            list.setSelectionBackground(APP_THEME_COLOR);
            list.setSelectionForeground(Color.WHITE);

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
            if (button.isEnabled()) {
                button.setBackground(hoverColor);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(originalColor);
        }
    }
}