package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import control.StorageLocationLogic;
import entity.StorageLocation;

public class StorageLocationManagement extends MainFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static StorageLocationManagement instance;

	public static StorageLocationManagement getInstance() {
		return instance;
	}

	private JComboBox<String> cbStorageNumber;
	private JTextField tfName;
	private JTable storageTable;
	private DefaultTableModel storageTableModel;
	private JPanel storagePanel, storageDetailsPanel;

	public StorageLocationManagement() {
		instance = this; // Store instance for external updates
		initializeUI();
	}

	private void initializeUI() {
		try {
			System.out.println("Initializing StorageLocationManagement...");

			// ✅ Create Background Panel
			BackgroundPanel backgroundPanel = new BackgroundPanel();
			backgroundPanel.setLayout(new BorderLayout());

			// ✅ Create Sidebar Panel
			JPanel sidebarPanel = new JPanel();
			sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
			sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
			sidebarPanel.setBackground(new Color(255, 255, 255, 100));

			sidebarPanel.add(Box.createVerticalStrut(50));

			String[] sidebarItems = { "Search Storage", "Add Storage", "Edit Storage", "Delete Storage",
					"Show All Storage", "Clear Fields","StorageLocation And Wine" };

			for (String item : sidebarItems) {
				RoundedButton button = createSidebarButton(item);
				sidebarPanel.add(button);
				sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
			}
			sidebarPanel.add(Box.createVerticalGlue());

			// ✅ Initialize Components
			JPanel contentPanel = new JPanel(new BorderLayout());
			contentPanel.setOpaque(false);

			initializeComponents();
			setupLayout(contentPanel);
			loadStorageNumbers();

			// ✅ Add Sidebar & Content to Background Panel
			backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
			backgroundPanel.add(contentPanel, BorderLayout.CENTER);

			setContentPane(backgroundPanel);
			setSize(1200, 700);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			System.out.println("StorageLocationManagement initialization complete");

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

		// ✅ Assign functionality
		button.addActionListener(e -> {
			switch (text) {
			case "Search Storage":
				searchStorage();
				break;
			case "Add Storage":
				new AddStorageLocationFrame().setVisible(true);
				break;
			case "Edit Storage":
				editStorageLocation();
				break;
			case "Delete Storage":
				deleteStorageLocation();
				break;
			case "Show All Storage":
				loadAllStorageLocations();
				break;
			case "Clear Fields":
				clearFields();
				break;
			case "StorageLocation And Wine" :
                  SwingUtilities.invokeLater(() -> new WineAndStorage().setVisible(true));
                  break;
			default:
				JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
			}
		});

		return button;
	}

	private void initializeComponents() {
		 cbStorageNumber = createModernComboBox(new String[]{}); 
		tfName = createTextField();

		// ✅ Define Table Columns
		String[] storageColumns = { "Storage Number", "Name" };
		storageTableModel = new DefaultTableModel(storageColumns, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		storageTable = new JTable(storageTableModel);
		styleTable(storageTable);
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
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
		storagePanel = new JPanel(new GridBagLayout());
		storagePanel.setBorder(BorderFactory.createTitledBorder("Storage Location Details"));
		storagePanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(storagePanel, gbc, "Storage Number:", cbStorageNumber, 0);
		addInputField(storagePanel, gbc, "Name:", tfName, 1);

		storageDetailsPanel = new JPanel(new BorderLayout());
		storageDetailsPanel.setBorder(BorderFactory.createTitledBorder("Storage Location List"));
		storageDetailsPanel.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(storageTable);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		storageDetailsPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(storagePanel, BorderLayout.NORTH);
		mainPanel.add(storageDetailsPanel, BorderLayout.CENTER);
	}

	public void loadStorageNumbers() {
	    try {
	        cbStorageNumber.removeAllItems(); // ✅ Clear existing items first
	        cbStorageNumber.addItem("Select Storage"); // ✅ Add default selection

	        // ✅ Fetch all storage location objects
	        List<StorageLocation> storageLocations = StorageLocationLogic.getInstance().getAllStorageLocations1();

	        // ✅ Populate dropdown with fetched storage numbers
	        for (StorageLocation storage : storageLocations) {
	            cbStorageNumber.addItem(storage.getStorageNumber()); // ✅ Add only unique storage numbers
	        }

	    } catch (Exception e) {
	        handleError("Failed to load Storage Locations", e);
	    }
	}



	private void editStorageLocation() {
	    try {
	        String selectedStorageNumber = (String) cbStorageNumber.getSelectedItem();

	        if (selectedStorageNumber == null || selectedStorageNumber.equals("Select Storage")) {
	            showError("Please select a Storage Number to edit!");
	            return;
	        }

	        String newName = tfName.getText().trim();

	        // ✅ 1. Validate Storage Name
	        if (!newName.matches("^[a-zA-Z\\s]+$") || newName.length() < 2) {
	            showError("Storage Name must contain only letters and be at least 2 characters long.");
	            return;
	        }

	        // ✅ Update Storage Location in Database
	        StorageLocation updatedStorage = new StorageLocation(selectedStorageNumber, newName);
	        boolean success = StorageLocationLogic.getInstance().updateStorageLocation(updatedStorage);

	        if (success) {
	            JOptionPane.showMessageDialog(this, "Storage location updated successfully!", "Success",
	                    JOptionPane.INFORMATION_MESSAGE);
	            loadStorageNumbers(); // ✅ Refresh storage list
	        } else {
	            showError("Failed to update storage location!");
	        }
	    } catch (Exception e) {
	        showError("Error updating storage location! " + e.getMessage());
	    }
	}

	private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

	private void deleteStorageLocation() {
		try {
			String selectedStorageNumber = (String) cbStorageNumber.getSelectedItem();

			if (selectedStorageNumber == null || selectedStorageNumber.equals("Select Storage")) {
				JOptionPane.showMessageDialog(this, "Please select a Storage Number to delete!", "Validation Error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this storage location?",
					"Confirm Deletion", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				boolean success = StorageLocationLogic.getInstance().deleteStorageLocation(selectedStorageNumber);

				if (success) {
					JOptionPane.showMessageDialog(this, "Storage location deleted successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					loadStorageNumbers(); // Refresh storage list
					clearFields();
				} else {
					JOptionPane.showMessageDialog(this, "Failed to delete storage location!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error deleting storage location!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadAllStorageLocations() {
		storageTableModel.setRowCount(0); // Clear previous data

		ArrayList<StorageLocation> storageLocations = StorageLocationLogic.getInstance().getAllStorageLocations();

		for (StorageLocation storage : storageLocations) {
			storageTableModel.addRow(new Object[] { storage.getStorageNumber(), storage.getName() });
		}
	}

	private void searchStorage() {
		try {
			String selectedStorageNumber = (String) cbStorageNumber.getSelectedItem();

			if (selectedStorageNumber == null || selectedStorageNumber.equals("Select Storage")) {
				JOptionPane.showMessageDialog(this, "Please select a Storage Number", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			StorageLocation storageLocation = StorageLocationLogic.getInstance()
					.getStorageLocationByNumber(selectedStorageNumber);

			if (storageLocation != null) {
				tfName.setText(storageLocation.getName());
			} else {
				JOptionPane.showMessageDialog(this, "Storage Location not found in the database", "Search Result",
						JOptionPane.INFORMATION_MESSAGE);
				clearFields();
			}
		} catch (Exception e) {
			handleError("Error searching storage location", e);
		}
	}

	private void clearFields() {
		cbStorageNumber.setSelectedIndex(0);
		tfName.setText("");
		storageTableModel.setRowCount(0); // Clears the table
	}

	private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public void refreshStorageLocations() {
	    cbStorageNumber.removeAllItems(); // ✅ Clear existing items
	    cbStorageNumber.addItem("Select Storage"); // ✅ Add default option only once

	    ArrayList<StorageLocation> storageLocations = StorageLocationLogic.getInstance().getAllStorageLocations();
	    for (StorageLocation storage : storageLocations) {
	        cbStorageNumber.addItem(storage.getStorageNumber());
	    }

	    loadAllStorageLocations(); // ✅ Reload table data after refresh
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
		SwingUtilities.invokeLater(() -> new StorageLocationManagement().setVisible(true));
	}
}
