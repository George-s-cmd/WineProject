package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import control.ProducerLogic;
import entity.Producer;

public class AddProducerFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfProducerId, tfName, tfContactPhone, tfAddress, tfEmail;
    private RoundedButton btnAdd, btnClear, btnBack;

    public AddProducerFrame() {
        setTitle("Add New Producer");
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

        JLabel titleLabel = new JLabel("Adding New Producer");
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
        tfProducerId = new JTextField(15);
        tfName = new JTextField(15);
        tfContactPhone = new JTextField(15);
        tfAddress = new JTextField(15);
        tfEmail = new JTextField(15);

        // 🔹 Modern Rounded Buttons
        btnAdd = createRoundedButton("Add Producer");
        btnClear = createRoundedButton("Clear");
        btnBack = createRoundedButton("Close");

        // Add components to layout
        addInputField("Producer ID:", tfProducerId, gbc, backgroundPanel, 1);
        addInputField("Name:", tfName, gbc, backgroundPanel, 2);
        addInputField("Phone:", tfContactPhone, gbc, backgroundPanel, 3);
        addInputField("Address:", tfAddress, gbc, backgroundPanel, 4);
        addInputField("Email:", tfEmail, gbc, backgroundPanel, 5);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // Button Actions
        btnAdd.addActionListener(e -> addProducer());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> dispose());

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

    private void addProducer() {
        try {
            String producerId = tfProducerId.getText().trim();
            String name = tfName.getText().trim();
            String phone = tfContactPhone.getText().trim();
            String address = tfAddress.getText().trim();
            String email = tfEmail.getText().trim();

           

            // ✅ 2. Check if Producer ID Already Exists
            if (ProducerLogic.getInstance().producerExists(producerId)) {
                showError("A producer with this ID already exists.");
                return;
            }

            // ✅ 3. Validate Name
            if (!name.matches("^[a-zA-Z\\s]+$") || name.length() < 2) {
                showError("Name must contain only English letters and be at least 2 characters long.");
                return;
            }

            // ✅ 4. Validate Phone Number (Israeli Format)
            if (!phone.matches("^05\\d{8}$")) {
                showError("Phone number must be in Israeli format (e.g., 0501234567).");
                return;
            }

            // ✅ 5. Validate Address
            if (address.length() < 5) {
                showError("Address must be at least 5 characters long.");
                return;
            }

            // ✅ 6. Validate Email
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showError("Invalid email format.");
                return;
            }

            // ✅ Create and Save Producer
            Producer newProducer = new Producer(producerId, name, phone, address, email);
            boolean success = ProducerLogic.getInstance().addProducer(newProducer);

            if (success) {
                JOptionPane.showMessageDialog(this, "Producer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                FrameProducer.getInstance().refreshProducerIds(); // ✅ Update ComboBox
                clearFields();
                dispose();
            } else {
                showError("Failed to add producer! Please check the details.");
            }
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void clearFields() {
        tfProducerId.setText("");
        tfName.setText("");
        tfContactPhone.setText("");
        tfAddress.setText("");
        tfEmail.setText("");
    }

    // 🔹 Modern Rounded Buttons
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
 


    // 🔹 Background Panel with Image
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
        SwingUtilities.invokeLater(() -> new AddProducerFrame().setVisible(true));
    }
}
