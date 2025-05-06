package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.ArrayList;
import control.GeneralControl;

public class AddRegularOrderCustomerFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbRegularOrder, cbCustomer;
    private RoundedButton btnAdd, btnCancel;

    public AddRegularOrderCustomerFrame() {
        setTitle("Add Pair ");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Add a new match");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // Initialize components
        cbRegularOrder = createModernComboBox();
        cbCustomer = createModernComboBox();
        btnAdd = createRoundedButton("Add");
        btnCancel = createRoundedButton("Cancel");

        // Load Data
        loadRegularOrders();
        loadCustomers();

        // Add components to layout
        addInputField("Regular Order:", cbRegularOrder, gbc, backgroundPanel, 1);
        addInputField("Customer:", cbCustomer, gbc, backgroundPanel, 2);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // Button Actions
        btnAdd.addActionListener(e -> addRegularOrderToCustomer());
        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void addInputField(String label, JComponent field, GridBagConstraints gbc, JPanel panel, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.black);
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

    private void loadCustomers() {
        cbCustomer.addItem("Select Customer");
        ArrayList<String> customers = GeneralControl.getInstance().getAllCustomerIds();
        for (String customer : customers) {
            cbCustomer.addItem(customer);
        }
    }

    private void addRegularOrderToCustomer() {
        try {
            String selectedOrderId = (String) cbRegularOrder.getSelectedItem();
            String selectedCustomerId = (String) cbCustomer.getSelectedItem();

            if ("Select Order".equals(selectedOrderId) || "Select Customer".equals(selectedCustomerId)) {
                JOptionPane.showMessageDialog(this, "Please select a Regular Order and a Customer!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = GeneralControl.getInstance().addRegularOrderToCustomer(selectedCustomerId, selectedOrderId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Regular Order linked to Customer successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to link Regular Order to Customer!", "Error", JOptionPane.ERROR_MESSAGE);
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
        button.setBackground(new Color(255, 92, 92)); // 🔥 Correct Color
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddRegularOrderCustomerFrame().setVisible(true));
    }
}
