package boundary;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import control.EmployeeDAO;
import entity.Consts;
import entity.Employee;

public class AddEmployeeFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfEmployeeId, tfName, tfPhone, tfEmail, tfOfficeAddress, tfStartDate;
    private RoundedButton btnAdd, btnClear, btnClose;

    public AddEmployeeFrame() {
        setTitle("Add New Employee");
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

        JLabel titleLabel = new JLabel("Adding New Employee");
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
        tfEmployeeId = new JTextField(15);
        tfName = new JTextField(15);
        tfPhone = new JTextField(15);
        tfEmail = new JTextField(15);
        tfOfficeAddress = new JTextField(15);
        tfStartDate = new JTextField(15);

        btnAdd = createRoundedButton("Add Employee");
        btnClear = createRoundedButton("Clear");
        btnClose = createRoundedButton("Close");

        // Add input fields
        addInputField("Employee ID:", tfEmployeeId, gbc, backgroundPanel, 1);
        addInputField("Name:", tfName, gbc, backgroundPanel, 2);
        addInputField("Phone:", tfPhone, gbc, backgroundPanel, 3);
        addInputField("Email:", tfEmail, gbc, backgroundPanel, 4);
        addInputField("Office Address:", tfOfficeAddress, gbc, backgroundPanel, 5);
        addInputField("Start Date (YYYY-MM-DD):", tfStartDate, gbc, backgroundPanel, 6);

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
        btnAdd.addActionListener(e -> addEmployeeToDatabase());
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
    private void addEmployeeToDatabase() {
        try {
            String id = tfEmployeeId.getText().trim();
            String name = tfName.getText().trim();
            String phone = tfPhone.getText().trim();
            String email = tfEmail.getText().trim();
            String officeAddress = tfOfficeAddress.getText().trim();
            String startDateText = tfStartDate.getText().trim();

            // ✅ 1. Validate Employee ID (Teudat Zehut)
            if (!id.matches("^\\d{9}$")) {
                showError("Employee ID must be exactly 9 digits.");
                return;
            }
            if (EmployeeDAO.getInstance().employeeExists(id)) {
                showError("An employee with this ID already exists.");
                return;
            }

            // ✅ 2. Validate Name
            if (!name.matches("^[א-תa-zA-Z\\s]+$") || name.length() < 2) {
                showError("Name must contain only letters and be at least 2 characters long.");
                return;
            }

            // ✅ 3. Validate Phone Number (Israeli Format)
            if (!phone.matches("^05\\d{8}$")) {
                showError("Phone number must be in Israeli format (e.g., 0501234567).");
                return;
            }

            // ✅ 4. Validate Email
            if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
                showError("Invalid email format.");
                return;
            }

            // ✅ 5. Validate Office Address
            if (officeAddress.length() < 5) {
                showError("Office address must be at least 5 characters long.");
                return;
            }

            // ✅ 6. Validate Start Date
            java.sql.Date startDate;
            try {
                startDate = java.sql.Date.valueOf(startDateText);
                if (startDate.after(Date.valueOf(LocalDate.now()))) {
                    showError("Start date cannot be in the future.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                showError("Invalid date format! Please use YYYY-MM-DD.");
                return;
            }

            // ✅ Insert Employee into Database
            Employee newEmployee = new Employee(id, name, phone, email, officeAddress, startDate);
            boolean success = EmployeeDAO.getInstance().addEmployee(newEmployee);

            if (success) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                dispose();
            } else {
                showError("Failed to add employee! Please check the details.");
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
        tfEmployeeId.setText("");
        tfName.setText("");
        tfPhone.setText("");
        tfEmail.setText("");
        tfOfficeAddress.setText("");
        tfStartDate.setText("");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddEmployeeFrame().setVisible(true));
    }
}
