package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import control.FoodPairingsLogic;
import control.WineTypeAndFoodPairingsLogic;
import entity.FoodPairing;

public class FoodPairingManaegment extends MainFrame {
	public static FoodPairingManaegment instance;
	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbFoodName;
	private JTextField tfRecipe1, tfRecipe2, tfRecipe3, tfRecipe4, tfRecipe5;
	private JTable foodTable;
	private DefaultTableModel foodTableModel;
	private JPanel foodPairingPanel, foodDetailsPanel;

	public FoodPairingManaegment() {
		instance = this;
		initializeUI();
	}

	private void initializeUI() {
		try {
			System.out.println("Initializing FoodPairingManagement...");

			BackgroundPanel backgroundPanel = new BackgroundPanel();
			backgroundPanel.setLayout(new BorderLayout());

			// ✅ Create Sidebar Panel
			JPanel sidebarPanel = new JPanel();
			sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
			sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
			sidebarPanel.setBackground(new Color(255, 255, 255, 100)); // ✅ Transparent effect

			sidebarPanel.add(Box.createVerticalStrut(50)); // ✅ Push buttons lower

			String[] sidebarItems = { "Search Food Pairing", "Add Food Pairing", "Edit Food Pairing",
					"Delete Food Pairing", "Show All Food Pairings", "Clear Fields", "Food Pairing And WineType" };

			for (String item : sidebarItems) {
				RoundedButton button = createSidebarButton(item);
				sidebarPanel.add(button);
				sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // ✅ Spacing
			}
			sidebarPanel.add(Box.createVerticalGlue()); // ✅ Push buttons upwards to center them

			// ✅ Initialize Components
			JPanel contentPanel = new JPanel(new BorderLayout());
			contentPanel.setOpaque(false);

			initializeComponents();
			setupLayout(contentPanel);
			loadFoodNames();

			// ✅ Add Sidebar & Content to Background Panel
			backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
			backgroundPanel.add(contentPanel, BorderLayout.CENTER);

			setContentPane(backgroundPanel);
			setSize(1200, 700);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			System.out.println("FoodPairingManagement initialization complete");

		} catch (Exception e) {
			handleError("Critical error during initialization", e);
		}
	}

	private RoundedButton createSidebarButton(String text) {
		RoundedButton button = new RoundedButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setForeground(Color.white);
		button.setBackground(new Color(255, 92, 92)); // ✅ Pink theme
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		button.setPreferredSize(new Dimension(200, 40));
		button.setMaximumSize(new Dimension(200, 40));
		button.setMinimumSize(new Dimension(200, 40));
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setHorizontalAlignment(SwingConstants.CENTER);

		// ✅ Assign functionality to buttons
		button.addActionListener(e -> {
			switch (text) {
			case "Search Food Pairing":
				searchFoodPairing();
				break;
			case "Add Food Pairing":
				SwingUtilities.invokeLater(() -> {
					new AddFoodPairings().setVisible(true);
				});
				break;
			case "Edit Food Pairing":
				editFoodPairing();
				break;
			case "Delete Food Pairing":
				deleteFoodPairing();
				break;
			case "Show All Food Pairings":
				loadAllFoodPairings();
				break;
			case "Food Pairing And WineType":
				new WineTypeAndFoodPairingUI().setVisible(true);
				break;
			case "Clear Fields":
				clearFields();
				break;

			default:
				JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
			}
		});

		return button;
	}

	private void initializeComponents() {
		cbFoodName = createModernComboBox(new String[] { "Select Dish" });
		tfRecipe1 = createTextField();
		tfRecipe2 = createTextField();
		tfRecipe3 = createTextField();
		tfRecipe4 = createTextField();
		tfRecipe5 = createTextField();

		// ✅ Define Table Columns
		String[] foodColumns = { "Dish Name", "Recipe 1", "Recipe 2", "Recipe 3", "Recipe 4", "Recipe 5" };
		foodTableModel = new DefaultTableModel(foodColumns, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		foodTable = new JTable(foodTableModel);
		styleTable(foodTable);
	}

	private JComboBox<String> createModernComboBox(String[] items) {
		JComboBox<String> comboBox = new JComboBox<>(items);
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		comboBox.setPreferredSize(new Dimension(200, 30));
		comboBox.setUI(new RoundedComboBoxUI());
		comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		comboBox.setOpaque(false);
		return comboBox;
	}

	public void loadFoodNames() {
	    try {
	        cbFoodName.removeAllItems(); // ✅ Clear existing items
	        cbFoodName.addItem("Select Dish"); // ✅ Add default selection

	        List<String> foodNames = FoodPairingsLogic.getInstance().getAllFoodNames1(); // ✅ Fetch all food names

	        for (String foodName : foodNames) {
	            cbFoodName.addItem(foodName); // ✅ Add each food name to the dropdown
	        }

	    } catch (Exception e) {
	        handleError("Failed to load Food Names", e);
	    }
	}


	private void setupLayout(JPanel mainPanel) {
		foodPairingPanel = new JPanel(new GridBagLayout());
		foodPairingPanel.setBorder(BorderFactory.createTitledBorder("Food Pairing Details"));
		foodPairingPanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(foodPairingPanel, gbc, "Dish Name:", cbFoodName, 0);
		addInputField(foodPairingPanel, gbc, "Recipe 1:", tfRecipe1, 1);
		addInputField(foodPairingPanel, gbc, "Recipe 2:", tfRecipe2, 2);
		addInputField(foodPairingPanel, gbc, "Recipe 3:", tfRecipe3, 3);
		addInputField(foodPairingPanel, gbc, "Recipe 4:", tfRecipe4, 4);
		addInputField(foodPairingPanel, gbc, "Recipe 5:", tfRecipe5, 5);

		foodDetailsPanel = new JPanel(new BorderLayout());
		foodDetailsPanel.setBorder(BorderFactory.createTitledBorder("Food Pairings List"));
		foodDetailsPanel.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(foodTable);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		foodDetailsPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(foodPairingPanel, BorderLayout.NORTH);
		mainPanel.add(foodDetailsPanel, BorderLayout.CENTER);
	}

	private void addInputField(JPanel panel, GridBagConstraints gbc, String label, JComponent component, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.weightx = 0.3;
		panel.add(new JLabel(label), gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.7;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(component, gbc);
	}

	private void searchFoodPairing() {
		try {
			String selectedDish = (String) cbFoodName.getSelectedItem();

			if (selectedDish == null || selectedDish.equals("Select Dish")) {
				JOptionPane.showMessageDialog(this, "Please select a dish", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			FoodPairing foodPairing = FoodPairingsLogic.getInstance().getFoodPairingByName(selectedDish);

			if (foodPairing != null) {
				cbFoodName.setSelectedItem(selectedDish); // **Ensure the selection remains**
				tfRecipe1.setText(foodPairing.getRecipe1());
				tfRecipe2.setText(foodPairing.getRecipe2());
				tfRecipe3.setText(foodPairing.getRecipe3());
				tfRecipe4.setText(foodPairing.getRecipe4());
				tfRecipe5.setText(foodPairing.getRecipe5());
			} else {
				JOptionPane.showMessageDialog(this, "Food Pairing not found", "Search Result",
						JOptionPane.INFORMATION_MESSAGE);
				clearFields();
			}
		} catch (Exception e) {
			handleError("Error searching food pairing", e);
		}
	}

	public void loadAllFoodPairings() {
		foodTableModel.setRowCount(0); // **Clear previous data**

		ArrayList<FoodPairing> foodPairings = FoodPairingsLogic.getInstance().getAllFoodPairings();
		HashMap<String, ArrayList<String>> wineTypesMap = WineTypeAndFoodPairingsLogic.getInstance()
				.getWineTypeByFoodDishName();

		for (FoodPairing foodPairing : foodPairings) {
			String dishName = foodPairing.getDishName();

			// **Get wine types for this dish**
			ArrayList<String> wineTypes = wineTypesMap.getOrDefault(dishName, new ArrayList<>());
			String wineTypesString = String.join(", ", wineTypes); // **Format as comma-separated list**

			foodTableModel.addRow(new Object[] { dishName, foodPairing.getRecipe1(), foodPairing.getRecipe2(),
					foodPairing.getRecipe3(), foodPairing.getRecipe4(), foodPairing.getRecipe5(), wineTypesString // **New
																													// column
																													// for
																													// wine
																													// types**
			});
		}
	}

	private JTextField createTextField() {
		JTextField textField = new JTextField(15);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textField.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return textField;
	}

	private void editFoodPairing() {
		try {
			String selectedDish = (String) cbFoodName.getSelectedItem();

			// ✅ 1. Validate Dish Selection
			if (selectedDish == null || selectedDish.equals("Select Dish")) {
				showError("Please select a valid dish to edit.");
				return;
			}

			// ✅ 2. Validate Dish Name Format
			if (!selectedDish.matches("^[a-zA-Z\\s]+$") || selectedDish.length() < 2) {
				showError("Dish Name must contain only letters and be at least 2 characters long.");
				return;
			}

			// ✅ 3. Ensure Dish Name Exists in Database
			if (!FoodPairingsLogic.getInstance().foodPairingExists(selectedDish)) {
				showError("This Dish Name does not exist. Please select an existing dish.");
				return;
			}

			// ✅ 4. Get Updated Recipe Values
			String recipe1 = tfRecipe1.getText().trim();
			String recipe2 = tfRecipe2.getText().trim();
			String recipe3 = tfRecipe3.getText().trim();
			String recipe4 = tfRecipe4.getText().trim();
			String recipe5 = tfRecipe5.getText().trim();

			// ✅ 5. Ensure At Least One Recipe is Provided
			if (recipe1.isEmpty()) {
				showError("At least one recipe is required (Recipe 1).");
				return;
			}

			// ✅ 6. Validate Recipe Lengths (max 100 characters each)
			if (!isValidRecipe(recipe1) || !isValidRecipe(recipe2) || !isValidRecipe(recipe3) || !isValidRecipe(recipe4)
					|| !isValidRecipe(recipe5)) {
				showError("Each recipe must be 100 characters or less.");
				return;
			}

			// ✅ 7. Call Logic to Update the Food Pairing
			boolean success = FoodPairingsLogic.getInstance().updateFoodPairing(selectedDish, recipe1, recipe2, recipe3,
					recipe4, recipe5);

			if (success) {
				JOptionPane.showMessageDialog(this, "Food Pairing updated successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				clearFields();
				refreshFoodPairingManagement(); // ✅ Refresh UI
			} else {
				showError("Failed to update Food Pairing!");
			}
		} catch (Exception e) {
			showError("Error updating Food Pairing: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// ✅ Checks if the recipe length is valid (100 characters max)
	private boolean isValidRecipe(String recipe) {
		return recipe.isEmpty() || recipe.length() <= 100;
	}

	// ✅ Helper Method to Show Errors
	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
	}

	private void deleteFoodPairing() {
		try {
			String selectedDish = (String) cbFoodName.getSelectedItem();

			if (selectedDish == null || selectedDish.equals("Select Dish")) {
				JOptionPane.showMessageDialog(this, "Please select a valid dish to delete.", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this Food Pairing?",
					"Confirm Delete", JOptionPane.YES_NO_OPTION);
			if (confirm != JOptionPane.YES_OPTION) {
				return;
			}

			// **Call logic to delete**
			boolean success = FoodPairingsLogic.getInstance().deleteFoodPairing(selectedDish);

			if (success) {
				JOptionPane.showMessageDialog(this, "Food Pairing deleted successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				clearFields();
				refreshFoodPairingManagement(); // **Refresh table and wine types**
			} else {
				JOptionPane.showMessageDialog(this, "Failed to delete Food Pairing!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error deleting Food Pairing: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void refreshFoodPairingManagement() {
		SwingUtilities.invokeLater(() -> {
			loadFoodNames(); // **Refresh JComboBox**
			loadAllFoodPairings(); // **Refresh Table including Wine Types**
		});
	}

	private void clearFields() {
		// ✅ Reset JComboBox selection to default
		cbFoodName.setSelectedIndex(0); // Resets to "Select Food" or first item

		// ✅ Clear text fields
		tfRecipe1.setText("");
		tfRecipe2.setText("");
		tfRecipe3.setText("");
		tfRecipe4.setText("");
		tfRecipe5.setText("");

		// ✅ Clear the table
		foodTableModel.setRowCount(0);
	}

	private void styleTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		table.getTableHeader().setBackground(new Color(255, 92, 92));
		table.getTableHeader().setForeground(Color.white);
		table.setOpaque(false);
		((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);
	}

	class BackgroundPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg"))
				.getImage();

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
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
		SwingUtilities.invokeLater(() -> new FoodPairingManaegment().setVisible(true));
	}
}
