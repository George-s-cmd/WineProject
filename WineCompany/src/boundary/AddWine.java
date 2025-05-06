package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.ArrayList;
import control.ProducerLogic;
import control.WineLogic;
import control.WineTypeLogic;
import entity.Producer;
import entity.Wine;

public class AddWine extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField tfCatalogNum, tfWineName, tfProductionYear, tfPrice, tfDescription, tfProductImage;
    private JComboBox<String> cbSweetnessLevel, cbWineTypeId, cbProducerId;
    private RoundedButton btnAdd, btnClear, btnBack;

    public AddWine() {
        setTitle("Add New Wine");
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

        JLabel titleLabel = new JLabel("Adding New Wine");
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
        tfCatalogNum = createStyledTextField();
        tfWineName =createStyledTextField();
        tfProductionYear = createStyledTextField();
        tfPrice = createStyledTextField();
        tfDescription = createStyledTextField();
        tfProductImage = createStyledTextField();

        cbSweetnessLevel = createModernComboBox(new String[]{"DRY", "SEMI_DRY", "SEMI_SWEET", "SWEET"});
        cbWineTypeId = createModernComboBox();
        cbProducerId = createModernComboBox();

        btnAdd = createRoundedButton("Add Wine");
        btnClear = createRoundedButton("Clear");
        btnBack = createRoundedButton("Close");

        // ✅ Load dropdown values
        populateWineTypes();
        populateProducerIds();

        // ✅ Add components to layout
        addInputField("Catalog Number:", tfCatalogNum, gbc, backgroundPanel, 1);
        addInputField("Wine Name:", tfWineName, gbc, backgroundPanel, 2);
        addInputField("Production Year:", tfProductionYear, gbc, backgroundPanel, 3);
        addInputField("Price:", tfPrice, gbc, backgroundPanel, 4);
        addInputField("Sweetness Level:", cbSweetnessLevel, gbc, backgroundPanel, 5);
        addInputField("Description:", tfDescription, gbc, backgroundPanel, 6);
        addInputField("Product Image URL:", tfProductImage, gbc, backgroundPanel, 7);
        addInputField("Wine Type ID:", cbWineTypeId, gbc, backgroundPanel, 8);
        addInputField("Producer ID:", cbProducerId, gbc, backgroundPanel, 9);

        // ✅ Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // ✅ Button Actions
        btnAdd.addActionListener(e -> addWine());
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

    private void populateWineTypes() {
        WineTypeLogic wineTypeLogic = new WineTypeLogic();
        ArrayList<String> wineTypeIds = wineTypeLogic.getAllWineTypeIds();
        for (String id : wineTypeIds) {
            cbWineTypeId.addItem(id);
        }
    }

    private void populateProducerIds() {
        ArrayList<Producer> producers = ProducerLogic.getInstance().getAllProducers();
        for (Producer producer : producers) {
            cbProducerId.addItem(producer.getId());
        }
    }

    private void addWine() {
        try {
            // ✅ 1. Validate Catalog Number
            String catalogNum = tfCatalogNum.getText().trim();
            if (catalogNum.isEmpty()) {
                showError("Catalog Number cannot be empty.");
                return;
            }

            // ✅ Check if Wine Already Exists
            if (WineLogic.getInstance().wineExists(catalogNum)) {
                showError("This Catalog Number already exists. Please choose a unique one.");
                return;
            }

            // ✅ 2. Validate Wine Name
            String wineName = tfWineName.getText().trim();
            if (wineName.isEmpty()) {
                showError("Wine Name cannot be empty.");
                return;
            }

            // ✅ 3. Validate Production Year
            int productionYear;
            try {
                productionYear = Integer.parseInt(tfProductionYear.getText().trim());
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                if (productionYear < 1900 || productionYear > currentYear) {
                    showError("Production Year must be between 1900 and " + currentYear + ".");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Production Year must be a valid number.");
                return;
            }

            // ✅ 4. Validate Price
            double price;
            try {
                price = Double.parseDouble(tfPrice.getText().trim());
                if (price <= 0) {
                    showError("Price must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Price must be a valid number.");
                return;
            }

            // ✅ 5. Validate Description
            String description = tfDescription.getText().trim();
            if (description.isEmpty()) {
                showError("Description cannot be empty.");
                return;
            }

            // ✅ 6. Validate Sweetness Level
            String sweetnessLevel = (String) cbSweetnessLevel.getSelectedItem();
            if (sweetnessLevel == null || sweetnessLevel.isEmpty()) {
                showError("Please select a Sweetness Level.");
                return;
            }

            // ✅ 7. Validate Wine Type ID
            String wineTypeId = (String) cbWineTypeId.getSelectedItem();
            if (wineTypeId == null || wineTypeId.isEmpty()) {
                showError("Please select a Wine Type.");
                return;
            }

            // ✅ 8. Validate Producer ID
            String producerId = (String) cbProducerId.getSelectedItem();
            if (producerId == null || producerId.isEmpty()) {
                showError("Please select a Producer.");
                return;
            }

            // ✅ Create Wine Object (Without Image)
            Wine newWine = new Wine(
                    catalogNum, producerId, wineName, productionYear, price, 
                    sweetnessLevel, description, null, wineTypeId // Image is set to null
            );

            // ✅ Insert into Database
            boolean success = WineLogic.getInstance().addWine(newWine);

            if (success) {
                JOptionPane.showMessageDialog(this, "Wine added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // ✅ Refresh the ComboBox in WineManagement
                if (WineManagment.instance != null) {
                    WineManagment.instance.loadCatalogNumbers();
                }
                
                clearFields();
            } else {
                showError("Failed to add wine. Please check the details.");
            }
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }


    // ✅ Helper Method to Show Errors
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
    }

    private void clearFields() {
        tfCatalogNum.setText("");
        tfWineName.setText("");
        tfProductionYear.setText("");
        tfPrice.setText("");
        tfDescription.setText("");
        tfProductImage.setText("");
        cbSweetnessLevel.setSelectedIndex(0);
        cbWineTypeId.setSelectedIndex(0);
        cbProducerId.setSelectedIndex(0);
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

    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setUI(new RoundedComboBoxUI());
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return comboBox;
    }

    private JComboBox<String> createModernComboBox() {
        return createModernComboBox(new String[]{});
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
    private static class RoundedComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼");
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFont(new Font("Proxima Nova", Font.BOLD, 12));
            button.setForeground(new Color(255, 92, 92));
            return button;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = c.getWidth();
            int height = c.getHeight();
            int arc = 20;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, width, height, arc, arc);

            g2d.setColor(new Color(255, 92, 92));
            g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

            super.paint(g, c);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddWine().setVisible(true));
    }
}
