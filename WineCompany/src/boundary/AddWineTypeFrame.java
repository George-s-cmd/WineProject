package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.sql.SQLException;

import control.WineTypeLogic;
import entity.WineType;

public class AddWineTypeFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField tfSerialNumber, tfName;
    private RoundedButton btnAdd, btnClear, btnBack;

    public AddWineTypeFrame() {
        setTitle("Add New Wine Type");
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

        JLabel titleLabel = new JLabel("Adding New Wine Type");
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
        tfSerialNumber = createStyledTextField();
        tfName = createStyledTextField();

        btnAdd = createRoundedButton("Add Wine Type");
        btnClear = createRoundedButton("Clear");
        btnBack = createRoundedButton("Close");

        // ✅ Add components to layout
        addInputField("Serial Number:", tfSerialNumber, gbc, backgroundPanel, 1);
        addInputField("Wine Type Name:", tfName, gbc, backgroundPanel, 2);

        // ✅ Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // ✅ Button Actions
        btnAdd.addActionListener(e -> addWineType());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> dispose());

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

    private void addWineType() {
        try {
            String serialNumber = tfSerialNumber.getText().trim();
            String name = tfName.getText().trim();

            // ✅ Check if Serial Number already exists
            if (WineTypeLogic.getInstance().wineTypeExists(serialNumber)) {
                showError("This Serial Number already exists. Please choose a unique one.");
                return;
            }

            // ✅ Validate Serial Number
            if (serialNumber.isEmpty()) {
                showError("Serial Number cannot be empty.");
                return;
            }
            if (!serialNumber.matches("^[a-zA-Z0-9]+$")) {
                showError("Serial Number must contain only letters and numbers (no spaces or special characters).");
                return;
            }
            if (serialNumber.length() < 3) {
                showError("Serial Number must be at least 3 characters long.");
                return;
            }

            // ✅ Validate Wine Type Name
            if (name.isEmpty()) {
                showError("Wine Type Name cannot be empty.");
                return;
            }
            if (!name.matches("^[a-zA-Z\\s]+$")) {
                showError("Wine Type Name must contain only letters and spaces.");
                return;
            }
            if (name.length() < 2) {
                showError("Wine Type Name must be at least 2 characters long.");
                return;
            }

            // ✅ Create and Insert Wine Type
            WineType newWineType = new WineType(serialNumber, name);
            boolean success = WineTypeLogic.getInstance().addWineType(newWineType);

            if (success) {
                JOptionPane.showMessageDialog(this, "Wine Type added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();

                // ✅ Refresh the wine type JComboBox in WineTypeManagement
                WineTypeManagement wineTypeManagement = WineTypeManagement.getInstance();
                if (wineTypeManagement != null) {
                    wineTypeManagement.refreshWineTypes();
                }

                dispose(); // Close the window
            } else {
                showError("Failed to add Wine Type! Please check the details.");
            }
        } catch (Exception e) {
            handleError("Error adding Wine Type", e);
        }
    }



    // ✅ Helper Method to Show Errors
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
    }


    private void clearFields() {
        tfSerialNumber.setText("");
        tfName.setText("");
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

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
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
        SwingUtilities.invokeLater(() -> new AddWineTypeFrame().setVisible(true));
    }
}
