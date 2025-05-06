package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import control.GeneralControl;
import entity.StockedWine;

public class WineAndStorage extends MainFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbWineId, cbStorageLocationId;
  
    private JTable storageTable;
    private DefaultTableModel storageTableModel;

    public WineAndStorage() {
        initializeUI();
    }

    private void initializeUI() {
        try {
            System.out.println("Initializing WineAndStorage...");

            // ✅ Background Panel
            BackgroundPanel backgroundPanel = new BackgroundPanel();
            backgroundPanel.setLayout(new BorderLayout());
            setContentPane(backgroundPanel);

            // ✅ Sidebar Panel
            JPanel sidebarPanel = new JPanel();
            sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
            sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
            sidebarPanel.setBackground(new Color(255, 255, 255, 100));

            sidebarPanel.add(Box.createVerticalStrut(50));

            String[] sidebarItems = {
            	    "Search Storage", "Add Pair", "Show All Pairs", "Delete Pair", "Edit Pair", "Clear Fields"
            	};


            for (String item : sidebarItems) {
                RoundedButton button = createSidebarButton(item);
                sidebarPanel.add(button);
                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            sidebarPanel.add(Box.createVerticalGlue());

            // ✅ Content Panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);

            initializeComponents();
            setupLayout(contentPanel);
            addActionListeners();
            loadWineIds();
            loadStorageLocations();

            // ✅ Add Sidebar and Content Panel
            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

            setSize(1200, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            System.out.println("WineAndStorage initialization complete");

        } catch (Exception e) {
            handleError("Critical error during initialization", e);
        }
    }

    private void initializeComponents() {
        cbWineId = createModernComboBox();
        cbStorageLocationId = createModernComboBox();
       

        String[] storageColumns = {"Catalog Number", "Producer ID", "Storage Location ID", "Quantity"};
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

    private void setupLayout(JPanel mainPanel) {
        JPanel storagePanel = new JPanel(new GridBagLayout());
        storagePanel.setBorder(BorderFactory.createTitledBorder("Wine Storage Details"));
        storagePanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        addInputField(storagePanel, gbc, "Wine ID:", cbWineId, 0);
        addInputField(storagePanel, gbc, "Storage Location:", cbStorageLocationId, 1);
     

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Wine Storage List"));
        tablePanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(storageTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(storagePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
    }

    private void addActionListeners() {
        cbWineId.addActionListener(e -> loadStorageLocationsForWine());
        cbStorageLocationId.addActionListener(e -> loadWineIdsForStorage());
    }
    private RoundedButton createSidebarButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
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
                case "Search Storage":
                    searchStorage();
                    break;
                case "Add Pair":
                    SwingUtilities.invokeLater(() -> new AddWineToStorageFrame().setVisible(true));
                    break;
                case "Show All Pairs":
                    loadAllWineStorage();
                    break;
                case "Delete Pair":
                    deleteSelectedWineStorage();
                    break;
                case "Edit Pair":
                    editSelectedWineStorage();
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
    private void deleteSelectedWineStorage() {
        String wineId = (String) cbWineId.getSelectedItem();
        String storageLocationId = (String) cbStorageLocationId.getSelectedItem();

        if (wineId == null || storageLocationId == null || "Select Wine".equals(wineId) || "Select Storage Location".equals(storageLocationId)) {
            JOptionPane.showMessageDialog(this, "Please select a valid wine and storage location pair to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this wine-storage pair?\nWine ID: " + wineId + "\nStorage Location: " + storageLocationId, 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = GeneralControl.getInstance().deleteWineFromStorage(wineId, storageLocationId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Pair deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllWineStorage();  // ✅ Refresh table
                loadWineIds();  // ✅ Ensure all wine IDs are still available
                loadStorageLocations();  // ✅ Ensure all storage locations are still available
                clearFields();  // ✅ Reset selection without removing items
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete pair.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedWineStorage() {
        String wineId = (String) cbWineId.getSelectedItem();
        String storageLocationId = (String) cbStorageLocationId.getSelectedItem();

        if (wineId == null || storageLocationId == null || "Select Wine".equals(wineId) || "Select Storage Location".equals(storageLocationId)) {
            JOptionPane.showMessageDialog(this, "Please select a valid wine and storage location pair to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentQuantity = GeneralControl.getInstance().getQuantityForWineStorage(wineId, storageLocationId);

        if (currentQuantity < 0) { // ✅ Prevent negative retrieved values
            JOptionPane.showMessageDialog(this, "Invalid quantity retrieved. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        openEditQuantityDialog(wineId, storageLocationId, currentQuantity);
    }

    private void openEditQuantityDialog(String wineId, String storageLocationId, int currentQuantity) {
        JDialog dialog = new JDialog(this, "Edit Quantity", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("New Quantity:"));
        JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
        panel.add(quantityField);

        JButton saveButton = new RoundedButton("Save");
        saveButton.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                if (newQuantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = GeneralControl.getInstance().updateWineStorageQuantity(wineId, storageLocationId, newQuantity);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Quantity updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllWineStorage(); // Refresh table
                    dialog.dispose(); // Close dialog
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid quantity entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private void searchStorage() {
        try {
            String selectedWineId = (String) cbWineId.getSelectedItem();
            String selectedStorageId = (String) cbStorageLocationId.getSelectedItem();

            if ("Select Wine".equals(selectedWineId) && "Select Storage Location".equals(selectedStorageId)) {
                JOptionPane.showMessageDialog(this, "Please select either a Wine or a Storage Location.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<StockedWine> storageList = GeneralControl.getInstance().getWineStorageDetails(selectedWineId, selectedStorageId);

            storageTableModel.setRowCount(0);
            for (StockedWine storage : storageList) {
                storageTableModel.addRow(new Object[]{
                        storage.getCatalogNumber(),
                        storage.getProducerId(),
                        storage.getStorageLocationId(),
                        storage.getQuantity()
                });
            }
        } catch (Exception e) {
            handleError("Error searching storage location", e);
        }
    }

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setUI(new RoundedComboBoxUI());
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        comboBox.setOpaque(false);
        return comboBox;
    }

    private void addInputField(JPanel panel, GridBagConstraints gbc, String label, JComponent component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(255, 92, 92));
        table.getTableHeader().setForeground(Color.white);
    }

    class BackgroundPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg")).getImage();

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

    

    private void loadStorageLocationsForWine() {
        try {
            String selectedWineId = (String) cbWineId.getSelectedItem();
            if (selectedWineId == null || "Select Wine".equals(selectedWineId)) {
                return;
            }

            String previousSelection = (String) cbStorageLocationId.getSelectedItem();

            cbStorageLocationId.removeAllItems();
            cbStorageLocationId.addItem("Select Storage Location");

            ArrayList<String> storageLocations = GeneralControl.getInstance().getStorageLocationsForWine(selectedWineId);
            for (String location : storageLocations) {
                cbStorageLocationId.addItem(location);
            }

            // Restore previous selection if it still exists in the new list
            if (previousSelection != null && storageLocations.contains(previousSelection)) {
                cbStorageLocationId.setSelectedItem(previousSelection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadWineIdsForStorage() {
        try {
            String selectedStorageId = (String) cbStorageLocationId.getSelectedItem();
            if (selectedStorageId == null || "Select Storage Location".equals(selectedStorageId)) {
                return;
            }

            String previousSelection = (String) cbWineId.getSelectedItem();

            cbWineId.removeAllItems();
            cbWineId.addItem("Select Wine");

            ArrayList<String> wineIds = GeneralControl.getInstance().getWinesForStorage(selectedStorageId);
            for (String wineId : wineIds) {
                cbWineId.addItem(wineId);
            }

            // Restore previous selection if it still exists in the new list
            if (previousSelection != null && wineIds.contains(previousSelection)) {
                cbWineId.setSelectedItem(previousSelection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadWineIds() {
        cbWineId.removeAllItems();
        cbWineId.addItem("Select Wine");

        // ✅ Fetch all wine IDs directly from the Wine Table (not from stored pairs)
        ArrayList<String> wineIds = GeneralControl.getInstance().getAllWineIdsFromWineTable();  
        for (String wineId : wineIds) {
            cbWineId.addItem(wineId);
        }
    }

    private void loadStorageLocations() {
        cbStorageLocationId.removeAllItems();
        cbStorageLocationId.addItem("Select Storage Location");

        // ✅ Fetch all storage locations directly from the Storage Table
        ArrayList<String> storageLocations = GeneralControl.getInstance().getAllStorageLocationsFromStorageTable();  
        for (String location : storageLocations) {
            cbStorageLocationId.addItem(location);
        }
    }


    private void clearFields() {
        cbWineId.setSelectedIndex(0);
        cbStorageLocationId.setSelectedIndex(0);
   
        storageTableModel.setRowCount(0);

        loadWineIds();
        loadStorageLocations();
    }
    private void handleError(String message, Exception ex) {
        System.err.println(message + ": " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                message + "\nError: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    
    private void loadAllWineStorage() {
        // ✅ Clear the table before reloading data
        storageTableModel.setRowCount(0);

        // ✅ Retrieve all stored wines from the database
        ArrayList<StockedWine> storageList = GeneralControl.getInstance().getAllWineStorage();

        // ✅ Populate the table with data
        for (StockedWine storage : storageList) {
            storageTableModel.addRow(new Object[]{
                    storage.getCatalogNumber(),
                    storage.getProducerId(),
                    storage.getStorageLocationId(),
                    storage.getQuantity()
            });
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WineAndStorage().setVisible(true));
    }
}
