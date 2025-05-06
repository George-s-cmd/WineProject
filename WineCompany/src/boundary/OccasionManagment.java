package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import control.OccasionLogic;
import entity.Occasion;


public class OccasionManagment extends MainFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbOccasionName;
    private JTextField tfDescription;
    private JTable occasionTable;
    private DefaultTableModel occasionTableModel;
    private JPanel occasionPanel, occasionDetailsPanel;
    private static OccasionManagment instance; // ✅ Singleton instance
    private JComboBox<String> cbSeason, cbLocation;

    public static OccasionManagment getInstance() {
        return instance;
    }
    public OccasionManagment() {
        instance = this; // ✅ Store instance reference
        initializeUI();
    }


    private void initializeUI() {
        try {
            System.out.println("Initializing OccasionManagement...");

            // ✅ Create Background Panel
            BackgroundPanel backgroundPanel = new BackgroundPanel();
            backgroundPanel.setLayout(new BorderLayout());

            // ✅ Create Sidebar Panel
            JPanel sidebarPanel = new JPanel();
            sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
            sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
            sidebarPanel.setBackground(new Color(255, 255, 255, 100));

            sidebarPanel.add(Box.createVerticalStrut(50));

            String[] sidebarItems = {
                "Search Occasion", "Add Occasion", "Edit Occasion",
                "Delete Occasion", "Show All Occasions", "Clear Fields","Occasion And WineType"
            };

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
            loadOccasionNames();

            // ✅ Add Sidebar & Content to Background Panel
            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

            setContentPane(backgroundPanel);
            setSize(1200, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            System.out.println("OccasionManagement initialization complete");

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
                case "Search Occasion":
                    searchOccasion();
                    break;
                case "Add Occasion":
                    new AddOccasionFrame().setVisible(true);
                    break;
                case "Edit Occasion":
                    editOccasion();
                    break;
                case "Delete Occasion":
                    deleteOccasion();
                    break;
                case "Show All Occasions":
                    loadAllOccasions();
                    break;
                case "Clear Fields":
                    clearFields();
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
    	 cbOccasionName = createModernComboBox(new String[]{});
    	    
    	    // ✅ Season ComboBox (Modern Styled)
    	    String[] seasons = {"Winter", "Spring", "Summer", "Fall"};
    	    cbSeason = createModernComboBox(seasons);

    	    // ✅ Location ComboBox (Modern Styled)
    	    String[] locations = {"Indoor", "Outdoor"};
    	    cbLocation = createModernComboBox(locations);
    	 cbOccasionName = createModernComboBox(new String[]{});
        tfDescription = createTextField();
       

        // ✅ Define Table Columns
        String[] occasionColumns = {"Occasion Name", "Description", "Season", "Location"};
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
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
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
        occasionPanel = new JPanel(new GridBagLayout());
        occasionPanel.setBorder(BorderFactory.createTitledBorder("Occasion Details"));
        occasionPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        addInputField(occasionPanel, gbc, "Occasion Name:", cbOccasionName, 0);
        addInputField(occasionPanel, gbc, "Description:", tfDescription, 1);
        addInputField(occasionPanel, gbc, "Season:", cbSeason, 2); // ✅ Changed to JComboBox
        addInputField(occasionPanel, gbc, "Location:", cbLocation, 3); // ✅ Changed to JComboBox

        occasionDetailsPanel = new JPanel(new BorderLayout());
        occasionDetailsPanel.setBorder(BorderFactory.createTitledBorder("Occasion List"));
        occasionDetailsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(occasionTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        occasionDetailsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(occasionPanel, BorderLayout.NORTH);
        mainPanel.add(occasionDetailsPanel, BorderLayout.CENTER);
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
		private final Image backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg")).getImage();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    
    private void editOccasion() {
        try {
            String selectedOccasionName = (String) cbOccasionName.getSelectedItem();
            String newDescription = tfDescription.getText().trim();
            String newSeason = (String) cbSeason.getSelectedItem();
            String newLocation = (String) cbLocation.getSelectedItem();

            // ✅ 1. Validate Occasion Selection
            if (selectedOccasionName == null || selectedOccasionName.equals("Select Occasion")) {
                showError("Please select an Occasion to edit.");
                return;
            }

            // ✅ 2. Ensure Occasion Exists in Database
            if (!OccasionLogic.getInstance().occasionExists(selectedOccasionName)) {
                showError("This Occasion does not exist. Please select an existing occasion.");
                return;
            }

            // ✅ 3. Validate Occasion Name Format
            if (!selectedOccasionName.matches("^[a-zA-Z\\s]+$") || selectedOccasionName.length() < 2) {
                showError("Occasion Name must contain only letters and be at least 2 characters long.");
                return;
            }

            // ✅ 4. Validate Description
            if (newDescription.isEmpty() || newDescription.length() < 5) {
                showError("Description must be at least 5 characters long.");
                return;
            }

            // ✅ 5. Validate Season Selection
            if (newSeason == null || newSeason.isEmpty()) {
                showError("Please select a season.");
                return;
            }

            // ✅ 6. Validate Location Selection
            if (newLocation == null || newLocation.isEmpty()) {
                showError("Please select a location.");
                return;
            }

            // ✅ 7. Update Occasion in Database
            boolean success = OccasionLogic.getInstance().updateOccasion(selectedOccasionName, newDescription, newSeason, newLocation);

            if (success) {
                JOptionPane.showMessageDialog(this, "Occasion updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOccasionNames(); // ✅ Refresh occasion list
            } else {
                showError("Failed to update occasion!");
            }
        } catch (Exception e) {
            showError("Error updating occasion: " + e.getMessage());
            e.printStackTrace();
        }
    }
 // ✅ Helper Method to Show Errors
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }


    private void deleteOccasion() {
        try {
            String selectedOccasionName = (String) cbOccasionName.getSelectedItem();

            if (selectedOccasionName == null || selectedOccasionName.equals("Select Occasion")) {
                JOptionPane.showMessageDialog(this, "Please select an Occasion to delete!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this Occasion?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = OccasionLogic.getInstance().deleteOccasion(selectedOccasionName);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Occasion deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOccasionNames(); // Refresh the list after deletion
                    clearFields(); // Reset input fields
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete occasion!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            handleError("Error deleting occasion", e);
        }
    }


    public void loadOccasionNames() {
        try {
            cbOccasionName.removeAllItems(); // ✅ Clear existing items
            cbOccasionName.addItem("Select Occasion"); // ✅ Add default selection

            List<Occasion> occasions = OccasionLogic.getInstance().getAllOccasions1(); // ✅ Fetch all occasion objects

            for (Occasion occasion : occasions) {
                cbOccasionName.addItem(occasion.getOccasionName()); // ✅ Add occasion name
            }

        } catch (Exception e) {
            handleError("Failed to load Occasion Names", e);
        }
    }


    private void loadAllOccasions() {
        occasionTableModel.setRowCount(0); // Clear previous data

        ArrayList<Occasion> occasions = OccasionLogic.getInstance().getAllOccasions();

        for (Occasion occasion : occasions) {
            occasionTableModel.addRow(new Object[]{
                occasion.getOccasionName(),
                occasion.getDescription(),
                occasion.getSeason(),
                occasion.getLocation()
            });
        }
    }

    private void searchOccasion() {
        try {
            String selectedOccasionName = (String) cbOccasionName.getSelectedItem();

            if (selectedOccasionName == null || selectedOccasionName.equals("Select Occasion")) {
                JOptionPane.showMessageDialog(this, "Please select an Occasion Name", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Occasion occasion = OccasionLogic.getInstance().getOccasionByName(selectedOccasionName);

            if (occasion != null) {
                tfDescription.setText(occasion.getDescription());

                // ✅ Set season from occasion data
                cbSeason.setSelectedItem(occasion.getSeason());

                // ✅ Set location from occasion data
                cbLocation.setSelectedItem(occasion.getLocation());
            } else {
                JOptionPane.showMessageDialog(this, "Occasion not found in the database", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        } catch (Exception e) {
            handleError("Error searching occasion", e);
        }
    }

    private void clearFields() {
        cbOccasionName.setSelectedIndex(0); // ✅ Reset dropdown without reloading
        tfDescription.setText("");
        occasionTableModel.setRowCount(0); 
        cbSeason.setSelectedIndex(0);
        cbLocation.setSelectedIndex(0);// ✅ Clear the table without refreshing the list
    }


    private void handleError(String message, Exception ex) {
        System.err.println(message + ": " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                message + "\nError: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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
    public void refreshOccasionNames() {
        cbOccasionName.removeAllItems(); // ✅ Clear existing items
        cbOccasionName.addItem("Select Occasion"); // ✅ Add default selection

        ArrayList<Occasion> occasions = OccasionLogic.getInstance().getAllOccasions();
        for (Occasion occasion : occasions) {
            cbOccasionName.addItem(occasion.getOccasionName()); // ✅ Add new occasion names
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OccasionManagment().setVisible(true));
    }
}
