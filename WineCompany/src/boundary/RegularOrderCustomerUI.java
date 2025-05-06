package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import control.GeneralControl;
import entity.RegularOrderCustomer;

public class RegularOrderCustomerUI extends MainFrame {
	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbRegularOrder, cbCustomer;
	private JTable orderCustomerTable;
	private DefaultTableModel orderCustomerTableModel;

	
	   
	    public static RegularOrderCustomerUI instance; // ✅ Singleton Instance

	    public RegularOrderCustomerUI() {
	        instance = this; // ✅ Assign instance to allow refreshing
	        initializeUI();
	    }
	


	private void initializeUI() {
		try {
			System.out.println("Initializing RegularOrderCustomerUI...");

			// ✅ Background Panel
			BackgroundPanel backgroundPanel = new BackgroundPanel();
			backgroundPanel.setLayout(new BorderLayout());
			setContentPane(backgroundPanel);

			// ✅ Initialize Components BEFORE Adding Buttons
			initializeComponents();

			// ✅ Sidebar Panel
			JPanel sidebarPanel = new JPanel();
			sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
			sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
			sidebarPanel.setBackground(new Color(255, 255, 255, 100));

			sidebarPanel.add(Box.createVerticalStrut(50)); // Space at the top

			String[] sidebarItems = { "Search Order-Customer", "Add Order-Customer", "Show All", "Delete Pair",
					 "Clear Fields" };

			for (String item : sidebarItems) {
				RoundedButton button = createSidebarButton(item);
				sidebarPanel.add(button);
				sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
			}

			// ✅ Add Edit & Delete Buttons to Sidebar
			sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Extra space

			sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons

			sidebarPanel.add(Box.createVerticalGlue()); // Pushes everything up

			// ✅ Content Panel
			JPanel contentPanel = new JPanel(new BorderLayout());
			contentPanel.setOpaque(false);
			setupLayout(contentPanel);

			// ✅ Add Sidebar and Content Panel
			backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
			backgroundPanel.add(contentPanel, BorderLayout.CENTER);

			// ✅ Load dropdown data
			addActionListeners();
			loadRegularOrders();
			loadCustomers();

			setSize(1200, 700);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			System.out.println("RegularOrderCustomerUI initialization complete");

		} catch (Exception e) {
			handleError("Critical error during initialization", e);
		}
	}

	private void initializeComponents() {
		cbRegularOrder = createModernComboBox();
		cbCustomer = createModernComboBox();

		String[] columns = { "Regular Order ID", "Customer ID" };
		orderCustomerTableModel = new DefaultTableModel(columns, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		orderCustomerTable = new JTable(orderCustomerTableModel);
		styleTable(orderCustomerTable);
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
			case "Search Order-Customer":
				searchOrderCustomer();
				break;
			case "Add Order-Customer":
				SwingUtilities.invokeLater(() -> new AddRegularOrderCustomerFrame().setVisible(true));
				break;
			case "Show All":
				loadAllOrderCustomerData();
				break;
			case "Clear Fields":
				clearFields();
				break;
			case "Delete Pair":
				deleteSelectedOrderCustomer();
				break;

			default:
				JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
			}
		});

		return button;
	}

	private void deleteSelectedOrderCustomer() {
		String orderId = (String) cbRegularOrder.getSelectedItem();
		String customerId = (String) cbCustomer.getSelectedItem();

		if (orderId == null || customerId == null || "Select Order".equals(orderId)
				|| "Select Customer".equals(customerId)) {
			JOptionPane.showMessageDialog(this, "Please select a valid order and customer pair to delete.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete this order-customer pair?\nOrder ID: " + orderId + "\nCustomer ID: "
						+ customerId,
				"Confirm Delete", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			boolean success = GeneralControl.getInstance().deleteRegularOrderCustomer(orderId, customerId);
			if (success) {
				JOptionPane.showMessageDialog(this, "Pair deleted successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				loadAllOrderCustomerData(); // Refresh table
				clearFields(); // Reset selection
			} else {
				JOptionPane.showMessageDialog(this, "Failed to delete pair.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void setupLayout(JPanel mainPanel) {
		JPanel orderCustomerPanel = new JPanel(new GridBagLayout());
		orderCustomerPanel.setBorder(BorderFactory.createTitledBorder("Order-Customer Details"));
		orderCustomerPanel.setOpaque(false); // ✅ Make the input panel transparent

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(orderCustomerPanel, gbc, "Regular Order:", cbRegularOrder, 0);
		addInputField(orderCustomerPanel, gbc, "Customer:", cbCustomer, 1);

		// ✅ Modify table panel to be transparent
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(BorderFactory.createTitledBorder("Order-Customer List"));
		tablePanel.setOpaque(false); // ✅ Makes the panel transparent

		JScrollPane scrollPane = new JScrollPane(orderCustomerTable);
		scrollPane.setOpaque(false); // ✅ Makes the scrollpane transparent
		scrollPane.getViewport().setOpaque(false); // ✅ Ensures table background is transparent
		scrollPane.setBorder(BorderFactory.createEmptyBorder()); // ✅ Remove default border

		tablePanel.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false); // ✅ Make the button panel transparent

		mainPanel.add(orderCustomerPanel, BorderLayout.NORTH);
		mainPanel.add(tablePanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}
	private void addActionListeners() {
        cbRegularOrder.addActionListener(e -> loadCustomersForOrder());
        cbCustomer.addActionListener(e -> loadOrdersForCustomer());
    }

	private void loadCustomersForOrder() {
		try {
			String selectedOrderId = (String) cbRegularOrder.getSelectedItem();
			if (selectedOrderId == null || "Select Order".equals(selectedOrderId)) {
				return;
			}

			String previousCustomer = (String) cbCustomer.getSelectedItem();

			cbCustomer.removeAllItems();
			cbCustomer.addItem("Select Customer");

			ArrayList<String> customers = GeneralControl.getInstance().getCustomersForRegularOrder(selectedOrderId);
			for (String customer : customers) {
				cbCustomer.addItem(customer);
			}

			if (previousCustomer != null && customers.contains(previousCustomer)) {
				cbCustomer.setSelectedItem(previousCustomer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadOrdersForCustomer() {
		try {
			String selectedCustomerId = (String) cbCustomer.getSelectedItem();
			if (selectedCustomerId == null || "Select Customer".equals(selectedCustomerId)) {
				return;
			}

			String previousOrder = (String) cbRegularOrder.getSelectedItem();

			cbRegularOrder.removeAllItems();
			cbRegularOrder.addItem("Select Order");

			ArrayList<String> orders = GeneralControl.getInstance().getRegularOrdersForCustomer(selectedCustomerId);
			for (String order : orders) {
				cbRegularOrder.addItem(order);
			}

			if (previousOrder != null && orders.contains(previousOrder)) {
				cbRegularOrder.setSelectedItem(previousOrder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadRegularOrders() {
		cbRegularOrder.removeAllItems();
		cbRegularOrder.addItem("Select Order");

		ArrayList<String> orders = GeneralControl.getInstance().getAllRegularOrderIds();
		for (String order : orders) {
			cbRegularOrder.addItem(order);
		}
	}

	private void loadCustomers() {
		cbCustomer.removeAllItems();
		cbCustomer.addItem("Select Customer");

		ArrayList<String> customers = GeneralControl.getInstance().getAllCustomerIds();
		for (String customer : customers) {
			cbCustomer.addItem(customer);
		}
	}

	private void searchOrderCustomer() {
	    try {
	        String selectedOrderId = (String) cbRegularOrder.getSelectedItem();
	        String selectedCustomerId = (String) cbCustomer.getSelectedItem();

	        if ("Select Order".equals(selectedOrderId) && "Select Customer".equals(selectedCustomerId)) {
	            JOptionPane.showMessageDialog(this, "Please select either an Order or a Customer.", 
	                                          "Validation Error", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        // ✅ Retrieve matching data
	        ArrayList<RegularOrderCustomer> orderCustomerList = GeneralControl.getInstance()
	                .getRegularOrderCustomerDetails(selectedOrderId, selectedCustomerId);

	        // ✅ Clear previous data
	        orderCustomerTableModel.setRowCount(0);

	        // ✅ Populate table with the retrieved data
	        for (RegularOrderCustomer orderCustomer : orderCustomerList) {
	            orderCustomerTableModel.addRow(new Object[]{
	                    orderCustomer.getRegularOrderId(),
	                    orderCustomer.getCustomerId()
	            });
	        }

	    } catch (Exception e) {
	        handleError("Error searching order-customer data", e);
	    }
	}


	private void loadAllOrderCustomerData() {
		orderCustomerTableModel.setRowCount(0);

		ArrayList<RegularOrderCustomer> orderCustomerList = GeneralControl.getInstance()
				.getAllRegularOrderCustomerData();

		for (RegularOrderCustomer orderCustomer : orderCustomerList) {
			orderCustomerTableModel
					.addRow(new Object[] { orderCustomer.getRegularOrderId(), orderCustomer.getCustomerId() });
		}
	}

	private void clearFields() {
		cbRegularOrder.setSelectedIndex(0);
		cbCustomer.setSelectedIndex(0);
		orderCustomerTableModel.setRowCount(0);

		loadRegularOrders();
		loadCustomers();
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
	public void refreshOrderCustomerData() {
	    loadRegularOrders(); // 🔄 Reload Regular Orders
	    loadCustomers();     // 🔄 Reload Customers
	    loadAllOrderCustomerData(); // 🔄 Reload the table with all pairs
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
		SwingUtilities.invokeLater(() -> new RegularOrderCustomerUI().setVisible(true));
	}

}
