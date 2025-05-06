package boundary;

import javax.swing.*;
import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.w3c.dom.Attr;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

import control.CustomerLogic;
import control.OrderLogic;
import entity.Customer;

public class AddCustomerFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfCustomerId, tfName, tfPhoneNumber, tfEmail, tfFirstContactDate, tfDeliveryAddress;
    private RoundedButton btnAdd, btnClear, btnClose;

    public AddCustomerFrame() {
        setTitle("Add New Customer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Background Panel with Image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Adding New Customer");
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

        // Initialize components
        tfCustomerId = new JTextField(15);
        tfName = new JTextField(15);
        tfPhoneNumber = new JTextField(15);
        tfEmail = new JTextField(15);
        tfFirstContactDate = new JTextField(15);
        tfDeliveryAddress = new JTextField(15);

        btnAdd = createRoundedButton("Add Customer");
        btnClear = createRoundedButton("Clear");
        btnClose = createRoundedButton("Close");

        // Add components to layout
        addInputField("Customer ID:", tfCustomerId, gbc, backgroundPanel, 1);
        addInputField("Name:", tfName, gbc, backgroundPanel, 2);
        addInputField("Phone Number:", tfPhoneNumber, gbc, backgroundPanel, 3);
        addInputField("Email:", tfEmail, gbc, backgroundPanel, 4);
        addInputField("First Contact Date (YYYY-MM-DD):", tfFirstContactDate, gbc, backgroundPanel, 5);
        addInputField("Delivery Address:", tfDeliveryAddress, gbc, backgroundPanel, 6);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnClose);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // Button Actions
        btnAdd.addActionListener(e -> addCustomer());
        btnClear.addActionListener(e -> clearFields());
        btnClose.addActionListener(e -> dispose());

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

    private void addCustomer() {
        try {
            String customerId = tfCustomerId.getText().trim();
            String name = tfName.getText().trim();
            String phoneNumber = tfPhoneNumber.getText().trim();
            String email = tfEmail.getText().trim();
            String firstContactText = tfFirstContactDate.getText().trim();
            String deliveryAddress = tfDeliveryAddress.getText().trim();

            // ✅ 1. Validate Customer ID
            if (customerId.isEmpty()) {
                showError("Customer ID cannot be empty.");
                return;
            }
            if (!customerId.matches("^\\d{9}$")) {
                showError("Customer ID must be exactly 9 digits.");
                return;
            }

            if (CustomerLogic.getInstance().customerExists(customerId)) {
                showError("This Customer ID already exists. Please use a unique one.");
                return;
            }

            // ✅ 2. Validate Name
            if (name.isEmpty() || !name.matches("^[a-zA-Z\\s]+$") || name.length() < 2) {
                showError("Name must contain only letters and be at least 2 characters long.");
                return;
            }

            // ✅ 3. Validate Phone Number
            if (!phoneNumber.matches("^\\d{10}$")) {
                showError("Phone Number must be exactly 10 digits.");
                return;
            }

            // ✅ 4. Validate Email
            if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
                showError("Invalid Email format.");
                return;
            }

            // ✅ 5. Validate First Contact Date
            java.sql.Date firstContactDate;
            try {
                firstContactDate = java.sql.Date.valueOf(firstContactText);
                if (firstContactDate.after(Date.valueOf(LocalDate.now()))) {
                    showError("First Contact Date cannot be in the future.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                showError("Invalid date format! Please use YYYY-MM-DD.");
                return;
            }

            // ✅ 6. Validate Delivery Address
            if (deliveryAddress.length() < 5) {
                showError("Delivery Address must be at least 5 characters long.");
                return;
            }

            // ✅ Create Customer Object
            Customer newCustomer = new Customer(customerId, name, phoneNumber, email, firstContactDate, deliveryAddress);

            // ✅ Add Customer to Database
            boolean success = CustomerLogic.getInstance().addCustomer(newCustomer);

            if (success) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();

                // ✅ Refresh Customer ID ComboBox in CustomerManagement
                if (CustomerManagement.instance != null) {
                    CustomerManagement.instance.loadCustomerIds();
                }

                dispose();
            } else {
                showError("Failed to add customer! Please check the details.");
            }
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
    }

    private void clearFields() {
        tfCustomerId.setText("");
        tfName.setText("");
        tfPhoneNumber.setText("");
        tfEmail.setText("");
        tfFirstContactDate.setText("");
        tfDeliveryAddress.setText("");
    }
    private RoundedButton createRoundedButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 92, 92)); // 🔥 Now using the correct color
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    // Background Panel with Image
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
        SwingUtilities.invokeLater(() -> new AddCustomerFrame().setVisible(true));
    }
}