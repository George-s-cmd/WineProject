package boundary;

import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class RoundedComboBoxUI extends BasicComboBoxUI {
    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton("▼");
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(new Font("Proxima Nova", Font.BOLD, 12));
        button.setForeground(new Color(255, 92, 92));
        return button;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = c.getWidth();
        int height = c.getHeight();
        int arc = 20;

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(0, 0, width, height, arc, arc);

        g2d.setColor(new Color(255, 92, 92));
        g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);
    }

    public static ComboBoxUI createUI(JComponent c) {
        return new RoundedComboBoxUI();
    }
}
