package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import control.OccasionLogic;
import entity.Occasion;

public class AddOccasionFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField tfOccasionName, tfDescription;
    private JComboBox<String> cbSeason, cbLocation;
    private RoundedButton btnAdd, btnClear, btnBack;

    public AddOccasionFrame() {
        setTitle("Add New Occasion");
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

        JLabel titleLabel = new JLabel("Adding New Occasion");
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
        tfOccasionName = new JTextField(15);
        tfDescription = new JTextField(15);

        // 🔹 ComboBox for Season
        String[] seasons = {"Winter", "Spring", "Summer", "Fall"};
        cbSeason = createModernComboBox(seasons);

        // 🔹 ComboBox for Location
        String[] locations = {"Indoor", "Outdoor"};
        cbLocation = createModernComboBox(locations);

        btnAdd = createRoundedButton("Add Occasion");
        btnClear = createRoundedButton("Clear");
        btnBack = createRoundedButton("Close");

        // Add components to layout
        addInputField("Occasion Name:", tfOccasionName, gbc, backgroundPanel, 1);
        addInputField("Description:", tfDescription, gbc, backgroundPanel, 2);
        addInputField("Season:", cbSeason, gbc, backgroundPanel, 3);
        addInputField("Location:", cbLocation, gbc, backgroundPanel, 4);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // Button Actions
        btnAdd.addActionListener(e -> addOccasion());
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

    private void addOccasion() {
        try {
            String occasionName = tfOccasionName.getText().trim();
            String description = tfDescription.getText().trim();
            String season = (String) cbSeason.getSelectedItem();
            String location = (String) cbLocation.getSelectedItem();

            // ✅ 1. Validate Occasion Name
            if (occasionName.isEmpty() || !occasionName.matches("^[a-zA-Z\\s]+$") || occasionName.length() < 2) {
                showError("Occasion Name must contain only letters and be at least 2 characters long.");
                return;
            }

            // ✅ 2. Check if Occasion Name Already Exists
            if (OccasionLogic.getInstance().occasionExists(occasionName)) {
                showError("An occasion with this name already exists.");
                return;
            }

            // ✅ 3. Validate Description
            if (description.isEmpty() || description.length() < 5) {
                showError("Description must be at least 5 characters long.");
                return;
            }

            // ✅ 4. Validate Season
            if (season == null || season.isEmpty()) {
                showError("Please select a season.");
                return;
            }

            // ✅ 5. Validate Location
            if (location == null || location.isEmpty()) {
                showError("Please select a location.");
                return;
            }

            // ✅ Create and Save Occasion
            Occasion newOccasion = new Occasion(occasionName, description, season, location);
            boolean success = OccasionLogic.getInstance().addOccasion(newOccasion);

            if (success) {
                JOptionPane.showMessageDialog(this, "Occasion added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                OccasionManagment.getInstance().refreshOccasionNames(); // ✅ Update ComboBox
                clearFields();
                dispose();
            } else {
                showError("Failed to add occasion! Please check the details.");
            }
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
    private void clearFields() {
        tfOccasionName.setText("");
        tfDescription.setText("");
        cbSeason.setSelectedIndex(0);
        cbLocation.setSelectedIndex(0);
    }

    // 🔹 Custom Modern ComboBox Method
    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setUI(new ModernComboBoxUI());
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return comboBox;
    }

    // 🔹 Modern ComboBox UI
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
        SwingUtilities.invokeLater(() -> new AddOccasionFrame().setVisible(true));
    }
}
