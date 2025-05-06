package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import control.GeneralControl;
import entity.Consts;
import entity.WineTypeAndFoodPairing;

public class WineTypeAndFoodPairingUI extends MainFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbWineType, cbDishName;
    private RoundedButton btnSearch, btnClear, btnShowAll, btnAddPairing;
    private JTable pairingTable;
    private DefaultTableModel pairingTableModel;

    public WineTypeAndFoodPairingUI() {
        initializeUI();
    }

    private void initializeUI() {
        try {
            System.out.println("Initializing WineTypeAndFoodPairingUI...");

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
            	    "Search Pairing", "Add Pairing", "Show All",  "Delete Pair","Clear Fields", 
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
            loadWineTypes();
            loadDishes();

            // ✅ Add Sidebar and Content Panel
            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

            setSize(1200, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            System.out.println("WineTypeAndFoodPairingUI initialization complete");

        } catch (Exception e) {
            handleError("Critical error during initialization", e);
        }
    }
    private void addActionListeners() {
        cbWineType.addActionListener(e -> loadFoodPairingForWineType());
        cbDishName.addActionListener(e -> loadWineTypesForFoodPairing());

        btnSearch.addActionListener(e -> searchPairing());
        btnClear.addActionListener(e -> clearFields());
        btnShowAll.addActionListener(e -> loadAllPairings());

        // ✅ Open New Form for Adding Wine-Food Pairing
        btnAddPairing.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AddWineTypeAndFoodPairingFrame().setVisible(true));
        });
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
                case "Search Pairing":
                    searchPairing();
                    break;
                case "Add Pairing":
                    SwingUtilities.invokeLater(() -> new AddWineTypeAndFoodPairingFrame().setVisible(true));
                    break;
                case "Show All":
                    loadAllPairings();
                    break;
                case "Clear Fields":
                    clearFields();
                    break;
                case "Delete Pair":
                    deletePairing(); // ✅ Connect button to deletion method
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
            }
        });


        return button;
    }

    private void initializeComponents() {
        cbWineType = createModernComboBox();
        cbDishName = createModernComboBox();

        btnSearch = createRoundedButton("Search Pairing");
        btnClear = createRoundedButton("Clear Fields");
        btnShowAll = createRoundedButton("Show All Pairings");
        btnAddPairing = createRoundedButton("Add Pairing");

        String[] pairingColumns = {"Wine Type Serial Number", "Dish Name"};
        pairingTableModel = new DefaultTableModel(pairingColumns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pairingTable = new JTable(pairingTableModel);
        styleTable(pairingTable);
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

    private void setupLayout(JPanel mainPanel) {
        JPanel pairingPanel = new JPanel(new GridBagLayout());
        pairingPanel.setBorder(BorderFactory.createTitledBorder("Wine-Food Pairing Details"));
        pairingPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        addInputField(pairingPanel, gbc, "Wine Type:", cbWineType, 0);
        addInputField(pairingPanel, gbc, "Dish:", cbDishName, 1);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Pairing List"));
        tablePanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(pairingTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(pairingPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
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
    private void handleError(String message, Exception ex) {
  		System.err.println(message + ": " + ex.getMessage());
  		ex.printStackTrace();
  		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
  				JOptionPane.ERROR_MESSAGE);
  	}
    private void loadWineTypes() {
        try {
            cbWineType.removeAllItems();
            cbWineType.addItem("Select Wine Type");

            ArrayList<String> wineTypes = GeneralControl.getInstance().getAllWineTypeIds();
            for (String wineType : wineTypes) {
                cbWineType.addItem(wineType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getAllWineTypeIds() {
	    ArrayList<String> wineTypeIds = new ArrayList<>();
	    String query = "SELECT DISTINCT serialNumber FROM WineTypeTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            wineTypeIds.add(rs.getString("serialNumber"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return wineTypeIds;
	}

    private void loadDishes() {
        try {
            cbDishName.removeAllItems();
            cbDishName.addItem("Select Dish");

            ArrayList<String> dishes = GeneralControl.getInstance().getAllFoodNames();
            for (String dish : dishes) {
                cbDishName.addItem(dish);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getAllFoodNames() {
	    ArrayList<String> foodNames = new ArrayList<>();
	    String query = "SELECT DISTINCT dishName FROM foodPairingsTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            foodNames.add(rs.getString("dishName"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return foodNames;
	}
    private void searchPairing() {
        try {
            String selectedWineType = (String) cbWineType.getSelectedItem();
            String selectedDish = (String) cbDishName.getSelectedItem();

            boolean isWineTypeSelected = selectedWineType != null && !selectedWineType.equals("Select Wine Type");
            boolean isDishSelected = selectedDish != null && !selectedDish.equals("Select Dish");

            // ❌ If both are unselected, show an error message
            if (!isWineTypeSelected && !isDishSelected) {
                JOptionPane.showMessageDialog(this, "Please select a Wine Type or Dish to search.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String wineTypeFilter = isWineTypeSelected ? selectedWineType : null;
            String dishFilter = isDishSelected ? selectedDish : null;

            // ✅ Debugging: Print filters
            System.out.println("🔍 Searching with filters:");
            System.out.println("Wine Type: " + (wineTypeFilter != null ? wineTypeFilter : "None"));
            System.out.println("Dish: " + (dishFilter != null ? dishFilter : "None"));

            // ✅ Fetch filtered results
            ArrayList<WineTypeAndFoodPairing> pairingList = GeneralControl.getInstance()
                    .getWineFoodPairingDetails(wineTypeFilter, dishFilter);

            // ✅ Debugging: Print results
            System.out.println("Results Found: " + pairingList.size());
            for (WineTypeAndFoodPairing pairing : pairingList) {
                System.out.println("✔ " + pairing.getWineTypeSerialNumber() + " - " + pairing.getDishName());
            }

            // ✅ Clear the table before inserting new results
            pairingTableModel.setRowCount(0);

            // ✅ Ensure we are correctly adding matching results to the table
            if (!pairingList.isEmpty()) {
                for (WineTypeAndFoodPairing pairing : pairingList) {
                    pairingTableModel.addRow(new Object[]{
                            pairing.getWineTypeSerialNumber(),
                            pairing.getDishName()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "No matching pairings found.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            handleError("Error searching pairings", e);
        }
    }

    private void deletePairing() {
        try {
            // ✅ Get selected values from combo boxes
            String selectedWineType = (String) cbWineType.getSelectedItem();
            String selectedDish = (String) cbDishName.getSelectedItem();

            // ✅ Ensure valid selection
            if ("Select Wine Type".equals(selectedWineType) || "Select Dish".equals(selectedDish)) {
                JOptionPane.showMessageDialog(this, "Please select a Wine Type and a Dish to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this pairing?\n\nWine Type: " + selectedWineType + "\nDish: " + selectedDish,
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // ✅ Call the control layer to delete
                boolean success = GeneralControl.getInstance().deleteWineFoodPairing(selectedWineType, selectedDish);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Pairing deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllPairings(); // ✅ Refresh the table
                    clearFields(); // ✅ Clear selected values from combo boxes
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete pairing.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            handleError("Error deleting pairing", e);
        }
    }

    private void loadAllPairings() {
        pairingTableModel.setRowCount(0);

        ArrayList<WineTypeAndFoodPairing> pairingList = GeneralControl.getInstance().getAllWineFoodPairings();

        for (WineTypeAndFoodPairing pairing : pairingList) {
            pairingTableModel.addRow(new Object[]{
                    pairing.getWineTypeSerialNumber(),
                    pairing.getDishName()
            });
        }
    }
    private void loadWineTypesForFoodPairing() {
        try {
            String selectedDish = (String) cbDishName.getSelectedItem();
            if (selectedDish == null || "Select Dish".equals(selectedDish)) {
                return;
            }

            String previousWineType = (String) cbWineType.getSelectedItem(); // Save previous selection

            cbWineType.removeAllItems();
            cbWineType.addItem("Select Wine Type");

            ArrayList<String> wineTypes = GeneralControl.getInstance().getWineTypesForDish(selectedDish);
            for (String wineType : wineTypes) {
                cbWineType.addItem(wineType);
            }

            // ✅ Restore previous selection if it still exists
            if (previousWineType != null && wineTypes.contains(previousWineType)) {
                cbWineType.setSelectedItem(previousWineType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFoodPairingForWineType() {
        try {
            String selectedWineType = (String) cbWineType.getSelectedItem();
            if (selectedWineType == null || "Select Wine Type".equals(selectedWineType)) {
                return;
            }

            String previousDish = (String) cbDishName.getSelectedItem(); // Save previous selection

            cbDishName.removeAllItems();
            cbDishName.addItem("Select Dish");

            ArrayList<String> dishes = GeneralControl.getInstance().getDishesForWineType(selectedWineType);
            for (String dish : dishes) {
                cbDishName.addItem(dish);
            }

            // ✅ Restore previous selection if it still exists
            if (previousDish != null && dishes.contains(previousDish)) {
                cbDishName.setSelectedItem(previousDish);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clearFields() {
        cbWineType.setSelectedIndex(0);
        cbDishName.setSelectedIndex(0);
        pairingTableModel.setRowCount(0);

        loadWineTypes();
        loadDishes();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WineTypeAndFoodPairingUI().setVisible(true));
    }
}
