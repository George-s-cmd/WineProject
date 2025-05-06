package boundary;

import javax.swing.*;
import java.awt.*;
import control.StorageLocationLogic;
import entity.StorageLocation;

public class AddStorageLocationFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfStorageNumber, tfName;
    private RoundedButton btnAdd, btnClear, btnBack;

    public AddStorageLocationFrame() {
        setTitle("Add New Storage Location");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Background Panel with Image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ✅ Title Label
        JLabel titleLabel = new JLabel("Adding New Storage Location");
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

        // ✅ Initialize components
        tfStorageNumber = createStyledTextField();
        tfName = createStyledTextField();

        btnAdd = createRoundedButton("Add Storage");
        btnClear = createRoundedButton("Clear");
        btnBack = createRoundedButton("Close");

        // ✅ Add components to layout
        addInputField("Storage Number:", tfStorageNumber, gbc, backgroundPanel, 1);
        addInputField("Storage Name:", tfName, gbc, backgroundPanel, 2);

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
        btnAdd.addActionListener(e -> addStorageLocation());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> {
            dispose();
           
        });

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

    private void addStorageLocation() {
        try {
            String storageNumber = tfStorageNumber.getText().trim();
            String name = tfName.getText().trim();

            

            // ✅ 2. Check if Storage Number Already Exists
            if (StorageLocationLogic.getInstance().storageLocationExistsById(storageNumber)) {
                showError("A storage location with this ID already exists.");
                return;
            }

            // ✅ 3. Validate Storage Name
            if (!name.matches("^[a-zA-Z\\s]+$") || name.length() < 2) {
                showError("Storage Name must contain only letters and be at least 2 characters long.");
                return;
            }

          

            // ✅ Create and Save Storage Location
            StorageLocation newStorage = new StorageLocation(storageNumber, name);
            boolean success = StorageLocationLogic.getInstance().addStorageLocation(newStorage);

            if (success) {
                JOptionPane.showMessageDialog(this, "Storage location added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();

                // ✅ Refresh StorageLocationManagement if open
                if (StorageLocationManagement.getInstance() != null) {
                    StorageLocationManagement.getInstance().refreshStorageLocations();
                }

                dispose();
            } else {
                showError("Failed to add storage location! Please check the details.");
            }
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }


    private void clearFields() {
        tfStorageNumber.setText("");
        tfName.setText("");
    }

    // ✅ Styled Text Field
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    // ✅ Styled Rounded Buttons
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

    // ✅ Background Panel with Image
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
        SwingUtilities.invokeLater(() -> new AddStorageLocationFrame().setVisible(true));
    }
}
