package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.ArrayList;
import control.GeneralControl;

public class AddWineTypeOccasionFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbWineType, cbOccasion;
    private RoundedButton btnAdd, btnCancel;

    public AddWineTypeOccasionFrame() {
        setTitle("Add Wine Type to Occasion");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Background Panel with Image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Adding Wine Type and Occasion Pair");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // ✅ Initialize Components
        cbWineType = createModernComboBox();
        cbOccasion = createModernComboBox();

        btnAdd = createRoundedButton("Add Pairing");
        btnCancel = createRoundedButton("Cancel");

        // ✅ Load dropdown values
        loadWineTypes();
        loadOccasions();

        // ✅ Add components to layout
        addInputField("Wine Type:", cbWineType, gbc, backgroundPanel, 1);
        addInputField("Occasion:", cbOccasion, gbc, backgroundPanel, 2);

        // ✅ Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnAdd);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // ✅ Button Actions
        btnAdd.addActionListener(e -> addWineTypeToOccasion());
        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void addInputField(String label, JComponent field, GridBagConstraints gbc, JPanel panel, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.BLACK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadWineTypes() {
        cbWineType.addItem("Select Wine Type");
        ArrayList<String> wineTypes = GeneralControl.getInstance().getAllWineTypeIds();
        for (String wineType : wineTypes) {
            cbWineType.addItem(wineType);
        }
    }

    private void loadOccasions() {
        cbOccasion.addItem("Select Occasion");
        ArrayList<String> occasions = GeneralControl.getInstance().getAllOccasionNames();
        for (String occasion : occasions) {
            cbOccasion.addItem(occasion);
        }
    }

    private void addWineTypeToOccasion() {
        try {
            String selectedWineType = (String) cbWineType.getSelectedItem();
            String selectedOccasion = (String) cbOccasion.getSelectedItem();

            if ("Select Wine Type".equals(selectedWineType) || "Select Occasion".equals(selectedOccasion)) {
                JOptionPane.showMessageDialog(this, "Please select a valid Wine Type and Occasion.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = GeneralControl.getInstance().addWineTypeOccasion(selectedWineType, selectedOccasion);

            if (success) {
                JOptionPane.showMessageDialog(this, "Wine Type successfully linked to Occasion!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to link Wine Type to Occasion.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            handleError("Error adding Wine Type to Occasion", e);
        }
    }

    private RoundedButton createRoundedButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 92, 92));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setUI(new ModernComboBoxUI());
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return comboBox;
    }

    private static class ModernComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼");
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setForeground(new Color(255, 92, 92));
            return button;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = c.getWidth();
            int height = c.getHeight();
            int arc = 15;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, width, height, arc, arc);

            g2d.setColor(new Color(255, 92, 92));
            g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

            super.paint(g, c);
        }
    }

    class BackgroundPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg")).getImage();
            setLayout(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void handleError(String message, Exception ex) {
        System.err.println(message + ": " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddWineTypeOccasionFrame().setVisible(true));
    }
}
