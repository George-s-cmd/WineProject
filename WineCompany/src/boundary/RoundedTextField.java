package boundary;

import javax.swing.*;
import java.awt.*;

public class RoundedTextField extends JTextField {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int arcSize = 15;

    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setHorizontalAlignment(SwingConstants.LEFT);
        
        // Force exact same size as combo box
        Dimension size = new Dimension(140, 25);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcSize, arcSize);
        
        // Draw border
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcSize, arcSize);
        
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Custom border already drawn in paintComponent
    }
}
