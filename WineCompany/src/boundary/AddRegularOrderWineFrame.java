package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.ArrayList;
import control.GeneralControl;

public class AddRegularOrderWineFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbRegularOrder, cbWine;
    private JTextField tfQuantity;
    private RoundedButton btnAdd, btnCancel;

    public AddRegularOrderWineFrame() {
        setTitle("Add Pair");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 🔹 Background Panel with Image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Adding Pair");
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

        // 🔹 Initialize Components
        cbRegularOrder = createModernComboBox();
        cbWine = createModernComboBox();
        tfQuantity = createStyledTextField();

        btnAdd = createRoundedButton("Add Pair");
        btnCancel = createRoundedButton("Cancel");

        // 🔹 Load dropdown values
        loadRegularOrders();
        loadWines();

        // 🔹 Add components to layout
        addInputField("Regular Order:", cbRegularOrder, gbc, backgroundPanel, 1);
        addInputField("Wine:", cbWine, gbc, backgroundPanel, 2);
        addInputField("Quantity:", tfQuantity, gbc, backgroundPanel, 3);

        // 🔹 Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnAdd);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // 🔹 Button Actions
        btnAdd.addActionListener(e -> addRegularOrderToWine());
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

    private void loadRegularOrders() {
        cbRegularOrder.addItem("Select Order");
        ArrayList<String> orders = GeneralControl.getInstance().getAllRegularOrderIds();
        for (String order : orders) {
            cbRegularOrder.addItem(order);
        }
    }

    private void loadWines() {
        cbWine.addItem("Select Wine");
        ArrayList<String> wines = GeneralControl.getInstance().getAllWineIds();
        for (String wine : wines) {
            cbWine.addItem(wine);
        }
    }

    private void addRegularOrderToWine() {
        try {
            String selectedOrderId = (String) cbRegularOrder.getSelectedItem();
            String selectedWineId = (String) cbWine.getSelectedItem();
            String quantityText = tfQuantity.getText().trim();

            if ("Select Order".equals(selectedOrderId) || "Select Wine".equals(selectedWineId) || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!quantityText.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Quantity must be a valid number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(quantityText);

            boolean success = GeneralControl.getInstance().addRegularOrderToWine(selectedOrderId, selectedWineId, quantity);

            if (success) {
                JOptionPane.showMessageDialog(this, "Regular Order linked to Wine successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to link Regular Order to Wine!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddRegularOrderWineFrame().setVisible(true));
    }
}
