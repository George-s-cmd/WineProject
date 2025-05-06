package boundary;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import control.WineLogic;
import entity.Wine;
import entity.WineType;
import java.util.ArrayList;


public class WineManagment extends MainFrame {
	private static final long serialVersionUID = 1L;
	public static WineManagment instance;
	private JComboBox<String> cbCatalogNum, cbSweetnessLevel;
	private JTextField tfProducerId, tfWineName, tfProductionYear, tfPrice, tfDescription, tfProductImage, tfWineTypeId;
	private JTable wineTable;
	private DefaultTableModel wineTableModel;
	private JPanel wineDetailsPanel, wineTablePanel;

	public static WineManagment getInstance() {
		return instance;
	}

	public WineManagment() {
		instance = this;
		initializeUI();
	}

	 private void initializeUI() {
	        try {
	            System.out.println("Initializing WineManagement...");

	            // ✅ Set Background Panel
	            BackgroundPanel backgroundPanel = new BackgroundPanel();
	            backgroundPanel.setLayout(new BorderLayout());

	            // ✅ Create Sidebar Panel
	            JPanel sidebarPanel = new JPanel();
	            sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
	            sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
	            sidebarPanel.setBackground(new Color(255, 255, 255, 100));

	            sidebarPanel.add(Box.createVerticalStrut(50));

	            String[] sidebarItems = {
	                "Search Wine", "Add Wine", "Edit Wine",
	                "Delete Wine", "Show All Wines", "Clear Fields",
	                "StorageLocation And Wine", "RegularOrder And Wine", "UrgentOrder And Wine"
	            };

	            for (String item : sidebarItems) {
	                RoundedButton button = createSidebarButton(item);
	                sidebarPanel.add(button);
	                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	            }
	            sidebarPanel.add(Box.createVerticalGlue());

	            // ✅ Initialize Components
	            JPanel contentPanel = new JPanel(new BorderLayout());
	            contentPanel.setOpaque(false);

	            initializeComponents();
	            setupLayout(contentPanel);
	            loadCatalogNumbers();

	            // ✅ Add Sidebar & Content to Background Panel
	            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
	            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

	            setContentPane(backgroundPanel);
	            setSize(1300, 700);
	            setLocationRelativeTo(null);
	            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	            System.out.println("WineManagement initialization complete");

	        } catch (Exception e) {
	            handleError("Critical error during initialization", e);
	        }
	    }

	 


	private RoundedButton createSidebarButton(String text) {
		RoundedButton button = new RoundedButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setForeground(Color.white);
		button.setBackground(new Color(255, 92, 92));
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		button.setPreferredSize(new Dimension(200, 40));
		button.setMaximumSize(new Dimension(200, 40));
		button.setMinimumSize(new Dimension(200, 40));
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setHorizontalAlignment(SwingConstants.CENTER);

		button.addActionListener(e -> {
			switch (text) {
			case "Search Wine":
				searchWine();
				break;
			case "Add Wine":
				SwingUtilities.invokeLater(() -> new AddWine().setVisible(true));
				break;
			case "Edit Wine":
				editWine();
				break;
			case "Delete Wine":
				deleteWine();
				break;
			case "Show All Wines":
			    List<Wine> winesList = WineLogic.getInstance().getAllWines(); // ✅ Fetch all wine objects directly

			    // ✅ Open WineScreen with correctly formatted data
			    SwingUtilities.invokeLater(() -> new WineScreen(winesList).setVisible(true));
			    break;

			case "StorageLocation And Wine":
				SwingUtilities.invokeLater(() -> new WineAndStorage().setVisible(true));
				break;
			case "RegularOrder And Wine":
				SwingUtilities.invokeLater(() -> new RegularOrderWineUI().setVisible(true));
				break;
			case "UrgentOrder And Wine":
				SwingUtilities.invokeLater(() -> new UrgentOrderWineUI().setVisible(true));
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
		cbCatalogNum = createModernComboBox(new String[] {});
		cbSweetnessLevel = createModernComboBox(new String[] { "DRY", "SEMI_DRY", "SEMI_SWEET", "SWEET" });

		tfProducerId = createTextField();
		tfProducerId.setEditable(false); // ✅ Ensure it's editable

		tfWineName = createTextField();
		tfProductionYear = createTextField();
		tfPrice = createTextField();
		tfDescription = createTextField();
		tfProductImage = createTextField();
		tfWineTypeId = createTextField();
		tfWineTypeId.setEditable(false);

		// Define Table Columns
		String[] wineColumns = { "Serial Number", "Wine Type Name" };
		wineTableModel = new DefaultTableModel(wineColumns, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		wineTable = new JTable(wineTableModel);
		styleTable(wineTable);
	}

	private void editWine() {
		if (!validateInputFields())
			return;

		Wine wine = createWineFromFields();
		boolean success = WineLogic.getInstance().editWine(wine);

		if (success) {
			JOptionPane.showMessageDialog(this, "Wine updated successfully!");
			loadCatalogNumbers(); // ✅ Refresh ComboBox to include the new wine
			clearFields();
		} else {
			JOptionPane.showMessageDialog(this, "Failed to update wine.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private Wine createWineFromFields() {
		String catalogNum = (String) cbCatalogNum.getSelectedItem();

		return new Wine(catalogNum, tfProducerId.getText().trim(), tfWineName.getText().trim(),
				Integer.parseInt(tfProductionYear.getText().trim()), Double.parseDouble(tfPrice.getText().trim()),
				cbSweetnessLevel.getSelectedItem().toString(), tfDescription.getText().trim(),
				tfProductImage.getText().trim(), tfWineTypeId.getText().trim());
	}

	private void deleteWine() {
		String catalogNum = (String) cbCatalogNum.getSelectedItem();

		if (catalogNum == null || catalogNum.equals("Select Catalog Number")) {
			JOptionPane.showMessageDialog(this, "Please select a Catalog Number.", "Validation Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this wine?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			boolean success = WineLogic.getInstance().removeWine(catalogNum);

			if (success) {
				JOptionPane.showMessageDialog(this, "Wine deleted successfully!");
				loadCatalogNumbers(); // ✅ Refresh ComboBox to remove deleted wine
				clearFields();
			} else {
				JOptionPane.showMessageDialog(this, "Failed to delete wine.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	
	public void loadCatalogNumbers() {
	    try {
	        cbCatalogNum.removeAllItems(); // ✅ Clear existing items
	        cbCatalogNum.addItem("Select Catalog Number"); // ✅ Add default selection

	        List<Wine> wines = WineLogic.getInstance().getAllWines(); // ✅ Fetch wine objects directly

	        for (Wine wine : wines) {
	            cbCatalogNum.addItem(wine.getCatalogNum()); // ✅ No need to parse, just use `getCatalogNum()`
	        }

	    } catch (Exception e) {
	        handleError("Failed to load Catalog Number", e);
	    }
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

	private JComboBox<String> createModernComboBox(String[] items) {
		JComboBox<String> comboBox = new JComboBox<>(items);
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		comboBox.setUI(new RoundedComboBoxUI());
		comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		comboBox.setOpaque(false);
		return comboBox;
	}

	private JTextField createTextField() {
		JTextField textField = new JTextField(15);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textField.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return textField;
	}

	private void setupLayout(JPanel mainPanel) {
		wineDetailsPanel = new JPanel(new GridBagLayout());
		wineDetailsPanel.setBorder(BorderFactory.createTitledBorder("Wine Details"));
		wineDetailsPanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(wineDetailsPanel, gbc, "Catalog Number:", cbCatalogNum, 0);
		addInputField(wineDetailsPanel, gbc, "Producer ID:", tfProducerId, 1);
		addInputField(wineDetailsPanel, gbc, "Wine Name:", tfWineName, 2);
		addInputField(wineDetailsPanel, gbc, "Production Year:", tfProductionYear, 3);
		addInputField(wineDetailsPanel, gbc, "Price:", tfPrice, 4);
		addInputField(wineDetailsPanel, gbc, "Sweetness Level:", cbSweetnessLevel, 5);
		addInputField(wineDetailsPanel, gbc, "Description:", tfDescription, 6);
		addInputField(wineDetailsPanel, gbc, "Product Image URL:", tfProductImage, 7);
		addInputField(wineDetailsPanel, gbc, "Wine Type ID:", tfWineTypeId, 8);

		wineTablePanel = new JPanel(new BorderLayout());
		wineTablePanel.setBorder(BorderFactory.createTitledBorder("WineType List"));
		wineTablePanel.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(wineTable);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		wineTablePanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(wineDetailsPanel, BorderLayout.NORTH);
		mainPanel.add(wineTablePanel, BorderLayout.CENTER);
	}

	private void styleTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		table.getTableHeader().setBackground(new Color(255, 92, 92));
		table.getTableHeader().setForeground(Color.white);
		table.setOpaque(false);
		((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);
	}

	private void searchWine() {
		String catalogNum = (String) cbCatalogNum.getSelectedItem();

		if (catalogNum == null || catalogNum.equals("Select Catalog Number")) {
			JOptionPane.showMessageDialog(this, "Please select a Catalog Number.", "Validation Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Wine wine = WineLogic.getInstance().getWineByCatalogNumber(catalogNum);

		if (wine != null) {
			populateFields(wine); // ✅ Fill in the fields
			updateSweetnessLevelSelection(wine.getSweetnessLevel()); // ✅ Ensure proper sweetness level update

			tfWineTypeId.setText(wine.getWineTypeId());

			// ✅ Fetch and display the wine type information in the table
			loadWineTypeInfo(wine.getWineTypeId());
		} else {
			JOptionPane.showMessageDialog(this, "Wine not found in the database.", "Search Result",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void updateSweetnessLevelSelection(String sweetnessLevel) {
		if (sweetnessLevel == null || sweetnessLevel.trim().isEmpty())
			return; // Prevent null or empty value issues

		System.out.println("Retrieved Sweetness Level: " + sweetnessLevel); // Debugging print statement

		boolean found = false;
		for (int i = 0; i < cbSweetnessLevel.getItemCount(); i++) {
			String item = cbSweetnessLevel.getItemAt(i).trim();
			if (item.equalsIgnoreCase(sweetnessLevel.trim())) {
				cbSweetnessLevel.setSelectedIndex(i);
				found = true;
				System.out.println("Sweetness Level Updated to: " + cbSweetnessLevel.getSelectedItem()); // Debugging
				break;
			}
		}

		if (!found) {
			System.out.println("Sweetness Level Not Found in ComboBox: " + sweetnessLevel);
		}

		cbSweetnessLevel.revalidate();
		cbSweetnessLevel.repaint(); // Force update UI
	}

	private boolean validateInputFields() {
		String catalogNum = (String) cbCatalogNum.getSelectedItem();
		String producerId = tfProducerId.getText().trim();
		String name = tfWineName.getText().trim();
		String year = tfProductionYear.getText().trim();
		String price = tfPrice.getText().trim();

		if (catalogNum == null || catalogNum.equals("Select Catalog Number") || producerId.isEmpty() || name.isEmpty()
				|| year.isEmpty() || price.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Missing Fields",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		try {
			Integer.parseInt(year);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Production Year must be a valid number.", "Invalid Input",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			Double.parseDouble(price);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Invalid Input",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private void clearFields() {
		cbCatalogNum.setSelectedIndex(0);
		tfProducerId.setText("");
		tfWineName.setText("");
		tfProductionYear.setText("");
		tfPrice.setText("");
		cbSweetnessLevel.setSelectedIndex(0);
		tfDescription.setText("");
		tfProductImage.setText("");
		tfWineTypeId.setText("");

		// ✅ Clear the table completely when "Clear Fields" is clicked
		wineTableModel.setRowCount(0);
	}

	private void populateFields(Wine wine) {
		cbCatalogNum.setSelectedItem(wine.getCatalogNum());
		tfProducerId.setText(wine.getProducerId());
		tfWineName.setText(wine.getName());
		tfProductionYear.setText(String.valueOf(wine.getProductionYear()));
		tfPrice.setText(String.valueOf(wine.getPricePerBottle()));
		cbSweetnessLevel.setSelectedItem(wine.getSweetnessLevel());
		tfDescription.setText(wine.getDescription());
		tfProductImage.setText(wine.getProductImage());
		tfWineTypeId.setText(wine.getWineTypeId());
	}

	private void loadWineTypeInfo(String wineTypeId) {
		// Clear previous table data
		wineTableModel.setRowCount(0);

		if (wineTypeId == null || wineTypeId.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No Wine Type ID found for this wine.", "No Data",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Fetch wine type information
		List<WineType> wineTypes = WineLogic.getInstance().getWineTypeById(wineTypeId);

		if (wineTypes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No Wine Type information found.", "No Data",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// ✅ Add each wine type found to the table
		for (WineType wineType : wineTypes) {
			wineTableModel.addRow(new Object[] { wineType.getSerialNumber(), wineType.getName() });
		}
	}

	private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
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
		SwingUtilities.invokeLater(() -> new WineManagment().setVisible(true));
	}
}
