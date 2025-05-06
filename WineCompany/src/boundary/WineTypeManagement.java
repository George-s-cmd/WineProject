package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import control.WineTypeLogic;

import control.WineTypeAndWineLogic;
import entity.WineType;
import entity.Wine;

public class WineTypeManagement extends MainFrame {
	private static final long serialVersionUID = 1L;
	private static WineTypeManagement instance;

	public static WineTypeManagement getInstance() {
		return instance;
	}

	private JComboBox<String> cbWineType;
	private JTextField tfName;
	private JTable wineTable;
	private DefaultTableModel wineTableModel;

	public WineTypeManagement() {
		instance = this; // Store instance for external updates
		initializeUI();
	}

	private void initializeUI() {
		try {
			System.out.println("Initializing WineTypeManagement...");

			// ✅ Create Background Panel
			BackgroundPanel backgroundPanel = new BackgroundPanel();
			backgroundPanel.setLayout(new BorderLayout());

			// ✅ Create Sidebar Panel
			JPanel sidebarPanel = new JPanel();
			sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
			sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
			sidebarPanel.setBackground(new Color(255, 255, 255, 100));

			sidebarPanel.add(Box.createVerticalStrut(50));

			String[] sidebarItems = { "Search Wine Type", "Add Wine Type", "Edit Wine Type", "Delete Wine Type",
					"Show All Wine Types", "Clear Fields" ,"Food Pairing And WineType","Occasion And WineType"};

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
			loadWineTypes();

			// ✅ Add Sidebar & Content to Background Panel
			backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
			backgroundPanel.add(contentPanel, BorderLayout.CENTER);

			setContentPane(backgroundPanel);
			setSize(1200, 700);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			System.out.println("WineTypeManagement initialization complete");

		} catch (Exception e) {
			handleError("Critical error during initialization", e);
		}
	}

	private RoundedButton createSidebarButton(String text) {
		RoundedButton button = new RoundedButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setForeground(Color.white);
		button.setBackground(new Color(255, 92, 92)); // Pink theme
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
			case "Search Wine Type":
				searchWineType();
				break;
			case "Add Wine Type":
				new AddWineTypeFrame().setVisible(true);
				break;
			case "Edit Wine Type":
				editWineType();
				break;
			case "Delete Wine Type":
				deleteWineType();
				break;
			case "Show All Wine Types":
				new ShowAllWineTypesFrame().setVisible(true);
				break;
			case "Clear Fields":
				clearFields();
				break;
			 case "Food Pairing And WineType":
                 new WineTypeAndFoodPairingUI().setVisible(true);
                 break;
			 case "Occasion And WineType":
             	new WineTypeOccasionUI().setVisible(true);
                 break;
			default:
				JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
			}
		});

		return button;
	}

	private void initializeComponents() {
		cbWineType = createModernComboBox(new String[] { });
		tfName = createTextField();

		String[] wineColumns = { "Catalog Number", "Producer ID", "Name", "Year", "Price", "Sweetness Level" };
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

	private JComboBox<String> createModernComboBox(String[] items) {
		JComboBox<String> comboBox = new JComboBox<>(items);
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		comboBox.setPreferredSize(new Dimension(200, 30));
		comboBox.setUI(new RoundedComboBoxUI());
		comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		comboBox.setOpaque(false);
		return comboBox;
	}

	private JTextField createTextField() {
		JTextField textField = new JTextField(15);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		textField.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return textField;
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

	private void setupLayout(JPanel mainPanel) {
		JPanel wineTypePanel = new JPanel(new GridBagLayout());
		wineTypePanel.setBorder(BorderFactory.createTitledBorder("Wine Type Details"));
		wineTypePanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(wineTypePanel, gbc, "Wine Type:", cbWineType, 0);
		addInputField(wineTypePanel, gbc, "Name:", tfName, 1);

		JPanel wineDetailsPanel = new JPanel(new BorderLayout());
		wineDetailsPanel.setBorder(BorderFactory.createTitledBorder("Wines Associated with Selected Wine Type"));
		wineDetailsPanel.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(wineTable);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Removes any borders
		wineDetailsPanel.add(scrollPane, BorderLayout.CENTER);


		mainPanel.add(wineTypePanel, BorderLayout.NORTH);
		mainPanel.add(wineDetailsPanel, BorderLayout.CENTER);
	}

	public void loadWineTypes() {
	    try {
	        cbWineType.removeAllItems(); // ✅ Clear existing items
	        cbWineType.addItem("Select Wine Type"); // ✅ Add default selection

	        List<WineType> wineTypes = WineTypeLogic.getInstance().getAllWineTypes1(); // ✅ Fetch all wine type objects

	        for (WineType wineType : wineTypes) {
	            cbWineType.addItem(wineType.getSerialNumber() ); // ✅ Display both ID & Name
	        }

	    } catch (Exception e) {
	        handleError("Failed to load Wine Types", e);
	    }
	}


	private void searchWineType() {
		try {
			String selectedWineType = (String) cbWineType.getSelectedItem();

			if (selectedWineType == null || selectedWineType.equals("Select Wine Type")) {
				JOptionPane.showMessageDialog(this, "Please select a Wine Type", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			WineType wineType = WineTypeLogic.getInstance().getWineTypeByNumber(selectedWineType);

			if (wineType != null) {
				tfName.setText(wineType.getName());

				// ✅ **Load wines associated with this wine type**
				loadWinesForType(selectedWineType);
			} else {
				JOptionPane.showMessageDialog(this, "Wine Type not found", "Search Result",
						JOptionPane.INFORMATION_MESSAGE);
				clearFields();
			}
		} catch (Exception e) {
			handleError("Error searching wine type", e);
		}
	}

	private void loadWinesForType(String wineTypeSerial) {
		wineTableModel.setRowCount(0); // ✅ Clears previous table data

		HashMap<String, ArrayList<Wine>> winesByType = WineTypeAndWineLogic.getInstance().getWineByWineTypeId();

		if (winesByType.containsKey(wineTypeSerial)) {
			ArrayList<Wine> wines = winesByType.get(wineTypeSerial);

			for (Wine wine : wines) {
				wineTableModel.addRow(new Object[] { wine.getCatalogNum(), wine.getProducerId(), wine.getName(),
						wine.getProductionYear(), wine.getPricePerBottle(), wine.getSweetnessLevel() });
			}
		} else {
			JOptionPane.showMessageDialog(this, "No wines found for this Wine Type.", "No Data",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void editWineType() {
	    try {
	        String selectedWineType = (String) cbWineType.getSelectedItem();

	        if (selectedWineType == null || selectedWineType.equals("Select Wine Type")) {
	            JOptionPane.showMessageDialog(this, "Please select a Wine Type to edit", "Validation Error",
	                    JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        String name = tfName.getText().trim();

	        // ✅ Ensure all fields are filled
	        if (name.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Wine Type name cannot be empty!", "Validation Error",
	                    JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        // ✅ Create WineType object with updated values
	        WineType updatedWineType = new WineType(selectedWineType, name);

	        // ✅ Attempt update in database
	        boolean success = WineTypeLogic.getInstance().updateWineType(updatedWineType);

	        if (success) {
	            JOptionPane.showMessageDialog(this, "Wine Type updated successfully!", "Success",
	                    JOptionPane.INFORMATION_MESSAGE);
	            clearFields();
	            loadWineTypes(); // ✅ Refresh the dropdown list
	        } else {
	            JOptionPane.showMessageDialog(this, "Failed to update Wine Type.", "Error", JOptionPane.ERROR_MESSAGE);
	        }

	    } catch (Exception e) {
	        handleError("Error updating Wine Type", e);
	    }
	}


	private void deleteWineType() {
		try {
			String selectedWineType = (String) cbWineType.getSelectedItem();

			if (selectedWineType == null || selectedWineType.equals("Select Wine Type")) {
				JOptionPane.showMessageDialog(this, "Please select a Wine Type to delete", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int confirmation = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to delete this Wine Type? This action cannot be undone.", "Confirm Deletion",
					JOptionPane.YES_NO_OPTION);

			if (confirmation == JOptionPane.YES_OPTION) {
				boolean success = WineTypeLogic.getInstance().deleteWineType(selectedWineType);

				if (success) {
					JOptionPane.showMessageDialog(this, "Wine Type deleted successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					loadWineTypes(); // ✅ Refresh after deletion
					clearFields();
				} else {
					JOptionPane.showMessageDialog(this, "Failed to delete Wine Type.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			handleError("Error deleting wine type", e);
		}
	}

	private void clearFields() {
		cbWineType.setSelectedIndex(0);
		tfName.setText("");
		wineTableModel.setRowCount(0); // Clears the wine table
	}

	private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void refreshWineTypes() {
	    cbWineType.removeAllItems(); // Clear existing items
	    cbWineType.addItem("Select Wine Type"); // Default option

	    ArrayList<WineType> wineTypes = WineTypeLogic.getInstance().getAllWineTypes();

	    for (WineType wineType : wineTypes) {
	        cbWineType.addItem(wineType.getSerialNumber());
	    }

	    // Reload table data after refresh
	    wineTableModel.setRowCount(0);
	}

	private void styleTable(JTable table) {
	    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
	    table.getTableHeader().setBackground(new Color(255, 92, 92));
	    table.getTableHeader().setForeground(Color.white);
	    table.setOpaque(false);
	    ((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new WineTypeManagement().setVisible(true));
	}
}
