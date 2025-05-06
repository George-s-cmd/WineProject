package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import control.GeneralControl;
import entity.RegularOrderWine;

public class RegularOrderWineUI extends MainFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbRegularOrder, cbWine;
    private JTable orderWineTable;
    private DefaultTableModel orderWineTableModel;

    public RegularOrderWineUI() {
        initializeUI();
    }

    private void initializeUI() {
        try {
            System.out.println("Initializing RegularOrderWineUI...");

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
            	    "Search Order-Wine", "Add Order-Wine", "Show All", "Delete Pair", "Edit Pair", "Clear Fields"
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
            loadRegularOrders();
            loadWines();
            

            // ✅ Add Sidebar and Content Panel
            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

            setSize(1200, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            System.out.println("RegularOrderWineUI initialization complete");

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
            case "Search Order-Wine":
                searchOrderWine();
                break;
            case "Add Order-Wine":
                SwingUtilities.invokeLater(() -> new AddRegularOrderWineFrame().setVisible(true));
                break;
            case "Show All":
                loadAllOrderWineData();
                break;
            case "Delete Pair":
                deleteOrderWinePair();
                break;
            case "Edit Pair":
                editOrderWinePair();
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
  

    private void addActionListeners() {
        cbRegularOrder.addActionListener(e -> loadWinesForOrder());
        cbWine.addActionListener(e -> loadOrdersForWine());
    }

    private void initializeComponents() {
        cbRegularOrder = createModernComboBox();
        cbWine = createModernComboBox();
       

        String[] columns = {"Regular Order ID", "Wine ID", "Quantity"};
        orderWineTableModel = new DefaultTableModel(columns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderWineTable = new JTable(orderWineTableModel);
        styleTable(orderWineTable);
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

    private void setupLayout(JPanel mainPanel) {
        JPanel orderWinePanel = new JPanel(new GridBagLayout());
        orderWinePanel.setBorder(BorderFactory.createTitledBorder("Order-Wine Details"));
        orderWinePanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        addInputField(orderWinePanel, gbc, "Regular Order:", cbRegularOrder, 0);
        addInputField(orderWinePanel, gbc, "Wine:", cbWine, 1);
      

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Order-Wine List"));
        tablePanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(orderWineTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(orderWinePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
    }

    private void loadRegularOrders() {
        cbRegularOrder.removeAllItems();
        cbRegularOrder.addItem("Select Order");

        ArrayList<String> orders = GeneralControl.getInstance().getAllRegularOrderIds();
        for (String order : orders) {
            cbRegularOrder.addItem(order);
        }
    }

    private void loadWines() {
        cbWine.removeAllItems();
        cbWine.addItem("Select Wine");

        ArrayList<String> wines = GeneralControl.getInstance().getAllWineIds();
        for (String wine : wines) {
            cbWine.addItem(wine);
        }
    }
    private void loadWinesForOrder() {
        try {
            String selectedOrderId = (String) cbRegularOrder.getSelectedItem();
            if (selectedOrderId == null || "Select Order".equals(selectedOrderId)) {
                return;
            }

            String previousWine = (String) cbWine.getSelectedItem();

            cbWine.removeAllItems();
            cbWine.addItem("Select Wine");

            ArrayList<String> wines = GeneralControl.getInstance().getWinesForRegularOrder(selectedOrderId);
            for (String wine : wines) {
                cbWine.addItem(wine);
            }

            if (previousWine != null) {
                cbWine.setSelectedItem(previousWine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrdersForWine() {
        try {
            String selectedWineId = (String) cbWine.getSelectedItem();
            if (selectedWineId == null || "Select Wine".equals(selectedWineId)) {
                return;
            }

            String previousOrder = (String) cbRegularOrder.getSelectedItem();

            cbRegularOrder.removeAllItems();
            cbRegularOrder.addItem("Select Order");

            ArrayList<String> orders = GeneralControl.getInstance().getRegularOrdersForWine(selectedWineId);
            for (String order : orders) {
                cbRegularOrder.addItem(order);
            }

            if (previousOrder != null) {
                cbRegularOrder.setSelectedItem(previousOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchOrderWine() {
        try {
            String selectedOrderId = (String) cbRegularOrder.getSelectedItem();
            String selectedWineId = (String) cbWine.getSelectedItem();

            if ("Select Order".equals(selectedOrderId) && "Select Wine".equals(selectedWineId)) {
                JOptionPane.showMessageDialog(this, "Please select either an Order or a Wine.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<RegularOrderWine> orderWineList = GeneralControl.getInstance().getRegularOrderWineDetails(selectedOrderId, selectedWineId);

            orderWineTableModel.setRowCount(0);
            for (RegularOrderWine orderWine : orderWineList) {
                orderWineTableModel.addRow(new Object[]{
                        orderWine.getRegularOrderNumber(),
                        orderWine.getWineId(),
                        orderWine.getQuantity()
                });
            }
        } catch (Exception e) {
            handleError("Error searching order-wine data", e);
        }
    }


    private void loadAllOrderWineData() {
        orderWineTableModel.setRowCount(0);

        ArrayList<RegularOrderWine> orderWineList = GeneralControl.getInstance().getAllRegularOrderWineData();

        for (RegularOrderWine orderWine : orderWineList) {
            orderWineTableModel.addRow(new Object[]{
                    orderWine.getRegularOrderNumber(),
                    orderWine.getWineId(),
                    orderWine.getQuantity()
            });
        }
    }
    private void deleteOrderWinePair() {
        String orderId = (String) cbRegularOrder.getSelectedItem();
        String wineId = (String) cbWine.getSelectedItem();

        if (orderId == null || wineId == null || "Select Order".equals(orderId) || "Select Wine".equals(wineId)) {
            JOptionPane.showMessageDialog(this, "Please select a valid order and wine pair to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this order-wine pair?\nOrder ID: " + orderId + "\nWine ID: " + wineId, 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = GeneralControl.getInstance().deleteRegularOrderWine(orderId, wineId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Pair deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllOrderWineData(); // Refresh table
                clearFields(); // Reset selection
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete pair.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editOrderWinePair() {
        String orderId = (String) cbRegularOrder.getSelectedItem();
        String wineId = (String) cbWine.getSelectedItem();

        if (orderId == null || wineId == null || "Select Order".equals(orderId) || "Select Wine".equals(wineId)) {
            JOptionPane.showMessageDialog(this, "Please select a valid order and wine pair to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int currentQuantity = GeneralControl.getInstance().getQuantityForOrderWine(orderId, wineId);

        if (currentQuantity == -1) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve quantity for the selected pair.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Ensure current quantity is valid
        if (currentQuantity < 0) {
            JOptionPane.showMessageDialog(this, "Invalid quantity retrieved from database. Quantity cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open quantity edit dialog
        openEditQuantityDialog(orderId, wineId, currentQuantity);
    }

    private void openEditQuantityDialog(String orderId, String wineId, int currentQuantity) {
        JDialog dialog = new JDialog(this, "Edit Quantity", true);
        dialog.setSize(300, 150);
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

                boolean success = GeneralControl.getInstance().updateRegularOrderWine(orderId, wineId, newQuantity);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Quantity updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllOrderWineData(); // Refresh table
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






    private void clearFields() {
        cbRegularOrder.setSelectedIndex(0);
        cbWine.setSelectedIndex(0);
        orderWineTableModel.setRowCount(0);

        loadRegularOrders();
        loadWines();
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
        SwingUtilities.invokeLater(() -> new RegularOrderWineUI().setVisible(true));
    }
}
