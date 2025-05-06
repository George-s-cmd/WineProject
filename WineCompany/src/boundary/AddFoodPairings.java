package boundary;

import control.FoodPairingsLogic;
import entity.FoodPairing;

import javax.swing.*;
import java.awt.*;

public class AddFoodPairings extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField tfDishName;
    private JTextField tfRecipe1, tfRecipe2, tfRecipe3, tfRecipe4, tfRecipe5;
    private RoundedButton btnAdd, btnClear, btnBack;

    public AddFoodPairings() {
        setTitle("Add New Food Pairing");
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

        JLabel titleLabel = new JLabel("Adding New Food Pairing");
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
        tfDishName = new JTextField(15);
        tfRecipe1 = new JTextField(15);
        tfRecipe2 = new JTextField(15);
        tfRecipe3 = new JTextField(15);
        tfRecipe4 = new JTextField(15);
        tfRecipe5 = new JTextField(15);

        btnAdd = new RoundedButton("Add Food Pairing");
        btnClear = new RoundedButton("Clear");
        btnBack = new RoundedButton("Close");

        // Add components to layout
        addInputField("Dish Name:", tfDishName, gbc, backgroundPanel, 1);
        addInputField("Recipe 1:", tfRecipe1, gbc, backgroundPanel, 2);
        addInputField("Recipe 2:", tfRecipe2, gbc, backgroundPanel, 3);
        addInputField("Recipe 3:", tfRecipe3, gbc, backgroundPanel, 4);
        addInputField("Recipe 4:", tfRecipe4, gbc, backgroundPanel, 5);
        addInputField("Recipe 5:", tfRecipe5, gbc, backgroundPanel, 6);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // Button Actions
        btnAdd.addActionListener(e -> addFoodPairingToDatabase());
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

    private void addFoodPairingToDatabase() {
        try {
            String dishName = tfDishName.getText().trim();
            String recipe1 = tfRecipe1.getText().trim();
            String recipe2 = tfRecipe2.getText().trim();
            String recipe3 = tfRecipe3.getText().trim();
            String recipe4 = tfRecipe4.getText().trim();
            String recipe5 = tfRecipe5.getText().trim();

            // ✅ 1. Validate Dish Name
            if (dishName.isEmpty() || !dishName.matches("^[a-zA-Z\\s]+$") || dishName.length() < 2) {
                showError("Dish Name must contain only letters and be at least 2 characters long.");
                return;
            }

            // ✅ 2. Check if Dish Name Already Exists
            if (FoodPairingsLogic.getInstance().foodPairingExists(dishName)) {
                showError("A food pairing with this Dish Name already exists.");
                return;
            }

            // ✅ 3. Validate at least one recipe is provided
            if (recipe1.isEmpty()) {
                showError("At least one recipe is required (Recipe 1).");
                return;
            }

            // ✅ 4. Validate Recipe Lengths (max 100 characters)
            if (!isValidRecipe(recipe1) || !isValidRecipe(recipe2) || !isValidRecipe(recipe3) || 
                !isValidRecipe(recipe4) || !isValidRecipe(recipe5)) {
                showError("Each recipe must be 100 characters or less.");
                return;
            }

            // ✅ Create and Save Food Pairing
            FoodPairing newFoodPairing = new FoodPairing(dishName, recipe1, recipe2, recipe3, recipe4, recipe5);
            boolean success = FoodPairingsLogic.getInstance().addFoodPairing(newFoodPairing);

            if (success) {
                JOptionPane.showMessageDialog(this, "Food Pairing added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                showError("Failed to add food pairing! Please check the details.");
            }
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    private boolean isValidRecipe(String recipe) {
        return recipe.isEmpty() || recipe.length() <= 100;
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
    private void clearFields() {
        tfDishName.setText("");
        tfRecipe1.setText("");
        tfRecipe2.setText("");
        tfRecipe3.setText("");
        tfRecipe4.setText("");
        tfRecipe5.setText("");
    }

    // Background Panel to display an image
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
        SwingUtilities.invokeLater(() -> new AddFoodPairings().setVisible(true));
    }
}
