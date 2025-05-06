package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import control.GeneralControl;
import control.WineTypeAndOccasionLogic;
import entity.WineTypeOccasion;

public class WineTypeOccasionUI extends MainFrame{
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbWineType, cbOccasion;
    private RoundedButton btnSearch, btnClear, btnShowAll, btnAddWineTypeOccasion;
    private JTable occasionTable;
    private DefaultTableModel occasionTableModel;

    public WineTypeOccasionUI() {
        initializeUI();
    }

    private void initializeUI() {
        try {
            System.out.println("Initializing WineTypeOccasionUI...");

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
                "Search Occasion", "Add Occasion", "Show All", "Delete Pair","Clear Fields"
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
            loadOccasions();

            // ✅ Add Sidebar and Content Panel
            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

            setSize(1200, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            System.out.println("WineTypeOccasionUI initialization complete");

        } catch (Exception e) {
            handleError("Critical error during initialization", e);
        }
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

        // ✅ Assign functionality
        button.addActionListener(e -> {
            switch (text) {
                case "Search Occasion":
                    searchOccasion();
                    break;
                case "Add Occasion":
                    SwingUtilities.invokeLater(() -> new AddWineTypeOccasionFrame().setVisible(true));
                    break;
                case "Show All":
                    loadAllOccasions();
                    break;
                case "Clear Fields":
                    clearFields();
                    break;
                case "Delete Pair":
                	deleteOccasionWinePair();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
            }
        });

        return button;
    }
    private void addActionListeners() {
        cbWineType.addActionListener(e -> loadOccasionsForWineType());
        cbOccasion.addActionListener(e -> loadWineTypesForOccasion());
        btnSearch.addActionListener(e -> searchOccasion());
        btnClear.addActionListener(e -> clearFields());
        btnShowAll.addActionListener(e -> loadAllOccasions());

        // ✅ Open New Form for Adding Occasion to Wine Type
        btnAddWineTypeOccasion.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AddWineTypeOccasionFrame().setVisible(true));
        });
    }

    private void initializeComponents() {
        cbWineType = createModernComboBox();
        cbOccasion = createModernComboBox();

        btnSearch = createRoundedButton("Search Occasion");
        btnClear = createRoundedButton("Clear Fields");
        btnShowAll = createRoundedButton("Show All Occasions");
        btnAddWineTypeOccasion = createRoundedButton("Add Occasion");

        String[] occasionColumns = {"Wine Type Serial Number", "Occasion Name"};
        occasionTableModel = new DefaultTableModel(occasionColumns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        occasionTable = new JTable(occasionTableModel);
        styleTable(occasionTable);
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
        JPanel occasionPanel = new JPanel(new GridBagLayout());
        occasionPanel.setBorder(BorderFactory.createTitledBorder("Wine Type Occasion Details"));
        occasionPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        addInputField(occasionPanel, gbc, "Wine Type:", cbWineType, 0);
        addInputField(occasionPanel, gbc, "Occasion:", cbOccasion, 1);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Occasion List"));
        tablePanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(occasionTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(occasionPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
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

    private void loadOccasions() {
        try {
            cbOccasion.removeAllItems();
            cbOccasion.addItem("Select Occasion");

            ArrayList<String> occasions = GeneralControl.getInstance().getAllOccasionNames();
            for (String occasion : occasions) {
                cbOccasion.addItem(occasion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadOccasionsForWineType() {
        try {
            String selectedWineType = (String) cbWineType.getSelectedItem();
            if (selectedWineType == null || "Select Wine Type".equals(selectedWineType)) {
                return;
            }

            String previousOccasion = (String) cbOccasion.getSelectedItem(); // Save previous selection

            cbOccasion.removeAllItems();
            cbOccasion.addItem("Select Occasion");

            ArrayList<String> occasions = GeneralControl.getInstance().getOccasionsForWineType(selectedWineType);
            for (String occasion : occasions) {
                cbOccasion.addItem(occasion);
            }

            // ✅ Restore previous selection if it still exists
            if (previousOccasion != null && occasions.contains(previousOccasion)) {
                cbOccasion.setSelectedItem(previousOccasion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadWineTypesForOccasion() {
        try {
            String selectedOccasion = (String) cbOccasion.getSelectedItem();
            if (selectedOccasion == null || "Select Occasion".equals(selectedOccasion)) {
                return;
            }

            String previousWineType = (String) cbWineType.getSelectedItem(); // Save previous selection

            cbWineType.removeAllItems();
            cbWineType.addItem("Select Wine Type");

            ArrayList<String> wineTypes = GeneralControl.getInstance().getWineTypesForOccasion(selectedOccasion);
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
    private void searchOccasion() {
        try {
            String selectedWineType = (String) cbWineType.getSelectedItem();
            String selectedOccasion = (String) cbOccasion.getSelectedItem();

            if ("Select Wine Type".equals(selectedWineType) && "Select Occasion".equals(selectedOccasion)) {
                JOptionPane.showMessageDialog(this, "Please select a Wine Type or Occasion.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<WineTypeOccasion> occasionList = GeneralControl.getInstance().getWineTypeOccasionDetails(selectedWineType, selectedOccasion);

            occasionTableModel.setRowCount(0);
            for (WineTypeOccasion occasion : occasionList) {
                occasionTableModel.addRow(new Object[]{
                        occasion.getWineTypeSerialNumber(),
                        occasion.getOccasionName()
                });
            }
        } catch (Exception e) {
            handleError("Error searching occasions", e);
        }
    }
    private void loadAllOccasions() {
        occasionTableModel.setRowCount(0);

        ArrayList<WineTypeOccasion> occasionList = GeneralControl.getInstance().getAllWineTypeOccasions();

        for (WineTypeOccasion occasion : occasionList) {
            occasionTableModel.addRow(new Object[]{
                    occasion.getWineTypeSerialNumber(),
                    occasion.getOccasionName()
            });
        }
    }

    private void clearFields() {
        cbWineType.setSelectedIndex(0);
        cbOccasion.setSelectedIndex(0);
        occasionTableModel.setRowCount(0);

        loadWineTypes();
        loadOccasions();
    }

    private void handleError(String message, Exception ex) {
        System.err.println(message + ": " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                message + "\nError: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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
    private void deleteOccasionWinePair() {
        try {
            String selectedWineType = (String) cbWineType.getSelectedItem();
            String selectedOccasion = (String) cbOccasion.getSelectedItem();

            if ("Select Wine Type".equals(selectedWineType) || "Select Occasion".equals(selectedOccasion)) {
                JOptionPane.showMessageDialog(this, "Please select a valid Wine Type and Occasion to delete.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this pairing?\n\nWine Type: " + selectedWineType + "\nOccasion: " + selectedOccasion,
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = WineTypeAndOccasionLogic.getInstance().deleteWineTypeOccasionPairing(selectedWineType, selectedOccasion);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Pairing deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete pairing.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            handleError("Error deleting pairing", e);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WineTypeOccasionUI().setVisible(true));
    }
}
