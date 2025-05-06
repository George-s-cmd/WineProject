package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.util.List;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import control.OrderLogic;
import entity.Customer;
import entity.UrgentOrder;
import java.sql.Date;
import java.util.ArrayList;

public class UrgentOrderManagement extends MainFrame {
	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbOrderNumber, cbOrderStatus;
	private JTextField tfOrderDate, tfShipmentDate, tfEmployeeId, tfPriority, tfExpectedDelivery;
	private JTable customerTable;
	private DefaultTableModel orderTableModel;
	private JPanel customerDetailsPanel, orderPanel;
	private static UrgentOrderManagement instance; // ✅ Singleton Instance

	public UrgentOrderManagement() {
		initializeUI();
	}

	public static UrgentOrderManagement getInstance() {
		if (instance == null) {
			instance = new UrgentOrderManagement();
		}
		return instance;
	}

	private void initializeUI() {
		try {
			System.out.println("Initializing UrgentOrderManagement...");

			// ✅ Create Background Panel
			BackgroundPanel backgroundPanel = new BackgroundPanel();
			backgroundPanel.setLayout(new BorderLayout());

			// ✅ Create Sidebar Panel
			JPanel sidebarPanel = new JPanel();
			sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
			sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
			sidebarPanel.setBackground(new Color(255, 255, 255, 100));

			sidebarPanel.add(Box.createVerticalStrut(50));

			String[] sidebarItems = { "Search Order", "Add Order", "Edit Order", "Delete Order", "Show All Orders",
					"Clear Fields", "UrgentOrders And Wine" };

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
			loadOrdersNumber();

			// ✅ Add Sidebar & Content to Background Panel
			backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
			backgroundPanel.add(contentPanel, BorderLayout.CENTER);

			setContentPane(backgroundPanel);
			setSize(1200, 700);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			System.out.println("UrgentOrderManagement initialization complete");

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
			case "Search Order":
				searchOrder();
				break;
			case "Add Order":
				AddUrgentOrderFrame addOrderFrame = new AddUrgentOrderFrame();
				addOrderFrame.setVisible(true);

				// ✅ Listen for window close and refresh orders
				addOrderFrame.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosed(java.awt.event.WindowEvent windowEvent) {
						refreshOrders(); // ✅ Update order combo box when the window is closed
					}
				});
				break;

			case "Edit Order":
				updateOrder();
				break;
			case "Delete Order":
				deleteOrder();
				break;
			case "Show All Orders":
				new showAllUrgentOrderFrame();
				break;
			case "Clear Fields":
				clearFields();
				break;
			case "UrgentOrders And Wine":
				SwingUtilities.invokeLater(() -> new UrgentOrderWineUI().setVisible(true));
				break;
			default:
				JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
			}
		});

		return button;
	}

	private void initializeComponents() {
		cbOrderNumber = createModernComboBox(new String[] {});
		cbOrderStatus = createOrderStatusComboBox(); // ✅ Order Status ComboBox
		tfOrderDate = createTextField();
		tfShipmentDate = createTextField();
		tfEmployeeId = createTextField();
		tfPriority = createTextField();
		tfExpectedDelivery = createTextField();

		// ✅ Define Table Columns
		String[] customerColumns = { "Customer ID", "Name", "Phone", "Email", "First Contact", "Delivery Address" };
		orderTableModel = new DefaultTableModel(customerColumns, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		customerTable = new JTable(orderTableModel);
		styleTable(customerTable);
	}

	private JTextField createTextField() {
		JTextField textField = new JTextField(15);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textField.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return textField;
	}

	private JComboBox<String> createOrderStatusComboBox() {
		String[] statuses = { "Paid", "Suspended", "Delivered", "Canceled", "InProcess", "Dispatched" };
		JComboBox<String> comboBox = new JComboBox<>(statuses);
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		comboBox.setPreferredSize(new Dimension(200, 30));
		comboBox.setUI(new RoundedComboBoxUI());
		comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		return comboBox;
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
		orderPanel = new JPanel(new GridBagLayout());
		orderPanel.setBorder(BorderFactory.createTitledBorder("Urgent Order Details"));
		orderPanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(orderPanel, gbc, "Order Number:", cbOrderNumber, 0);
		addInputField(orderPanel, gbc, "Order Date:", tfOrderDate, 1);
		addInputField(orderPanel, gbc, "Order Status:", cbOrderStatus, 2); // ✅ Updated to JComboBox
		addInputField(orderPanel, gbc, "Shipment Date:", tfShipmentDate, 3);
		addInputField(orderPanel, gbc, "Employee ID:", tfEmployeeId, 4);
		addInputField(orderPanel, gbc, "Priority:", tfPriority, 5);
		addInputField(orderPanel, gbc, "Expected Delivery Date:", tfExpectedDelivery, 6);

		customerDetailsPanel = new JPanel(new BorderLayout());
		customerDetailsPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
		customerDetailsPanel.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(customerTable);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		customerDetailsPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(orderPanel, BorderLayout.NORTH);
		mainPanel.add(customerDetailsPanel, BorderLayout.CENTER);
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

	public void loadOrdersNumber() {
	    try {
	        cbOrderNumber.removeAllItems(); // ✅ Clear existing items
	        cbOrderNumber.addItem("Select Order"); // ✅ Add default selection

	        List<UrgentOrder> urgentOrders = OrderLogic.getInstance().getAllUrgentOrders(); // ✅ Fetch all urgent orders

	        for (UrgentOrder order : urgentOrders) {
	            cbOrderNumber.addItem(order.getOrderNumber() ); // ✅ Show ID & Priority
	        }

	    } catch (Exception e) {
	        handleError("Failed to load order numbers", e);
	    }
	}



	private void searchOrder() {
		try {
			String selectedOrderNumber = (String) cbOrderNumber.getSelectedItem();

			if (selectedOrderNumber == null || selectedOrderNumber.equals("Select Order")) {
				JOptionPane.showMessageDialog(this, "Please select an Order Number", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			UrgentOrder urgentOrder = OrderLogic.getInstance().getUrgentOrderByNumber(selectedOrderNumber);

			if (urgentOrder != null) {
				tfOrderDate.setText(urgentOrder.getOrderDate().toString());
				cbOrderStatus.setSelectedItem(urgentOrder.getOrderStatus()); // ✅ Updated to ComboBox
				tfShipmentDate.setText(urgentOrder.getShipmentDate().toString());
				tfEmployeeId.setText(urgentOrder.getEmployeeId());
				tfPriority.setText(String.valueOf(urgentOrder.getPriority()));
				tfExpectedDelivery.setText(urgentOrder.getExpectedDelivery().toString());

				// ✅ Load customers for this urgent order (existing logic)
				loadOrdersForCustomer(selectedOrderNumber);
			} else {
				JOptionPane.showMessageDialog(this, "Order not found in the database", "Search Result",
						JOptionPane.INFORMATION_MESSAGE);
				clearFields();
			}
		} catch (Exception e) {
			handleError("Error searching order", e);
		}
	}

	private void loadOrdersForCustomer(String orderNumber) {
		orderTableModel.setRowCount(0);

		ArrayList<Customer> customers = OrderLogic.getInstance().getCustomersForOrder(orderNumber);

		for (Customer customer : customers) {
			orderTableModel.addRow(new Object[] { customer.getPersonId(), customer.getName(), customer.getPhoneNumber(),
					customer.getEmail(), customer.getDayOfFirstContact(), customer.getDeliveryAddress() });
		}
	}

	private void clearFields() {
		// ✅ Reset Combo Boxes
		cbOrderNumber.setSelectedIndex(0); // Set to "Select Order"
		cbOrderStatus.setSelectedIndex(0); // Set to first status option

		// ✅ Reset Text Fields
		tfOrderDate.setText("");
		tfShipmentDate.setText("");
		tfEmployeeId.setText("");
		tfPriority.setText("");
		tfExpectedDelivery.setText("");

		// ✅ Clear Table Data
		orderTableModel.setRowCount(0);

		System.out.println("✅ Fields cleared to default state.");
	}

	private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	void deleteOrder() {
		try {
			String selectedOrderNumber = (String) cbOrderNumber.getSelectedItem();

			if (selectedOrderNumber == null || selectedOrderNumber.equals("Select Order")) {
				JOptionPane.showMessageDialog(this, "Please select an Order Number to delete", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int confirmation = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to delete order: " + selectedOrderNumber + "?", "Confirm Delete",
					JOptionPane.YES_NO_OPTION);

			if (confirmation == JOptionPane.YES_OPTION) {
				boolean isDeleted = OrderLogic.getInstance().deleteUrgentOrder(selectedOrderNumber);

				if (isDeleted) {
					JOptionPane.showMessageDialog(this, "Order deleted successfully.", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					loadOrdersNumber(); // Reload orders after deletion
					clearFields();
				} else {
					JOptionPane.showMessageDialog(this, "Failed to delete order. It may not exist.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			handleError("Error deleting order", e);
		}
	}

	private void updateOrder() {
	    try {
	        String selectedOrderNumber = (String) cbOrderNumber.getSelectedItem();

	        if (selectedOrderNumber == null || selectedOrderNumber.equals("Select Order")) {
	            showError("Please select an Order Number to update.");
	            return;
	        }

	        // Retrieve updated values
	        String orderDateText = tfOrderDate.getText().trim();
	        String shipmentDateText = tfShipmentDate.getText().trim();
	        String expectedDeliveryText = tfExpectedDelivery.getText().trim();
	        String updatedOrderStatus = (String) cbOrderStatus.getSelectedItem();
	        String updatedEmployeeId = tfEmployeeId.getText().trim();
	        String priorityText = tfPriority.getText().trim();

	        // ✅ 1. Validate Order Date
	        java.sql.Date updatedOrderDate;
	        try {
	            updatedOrderDate = java.sql.Date.valueOf(orderDateText);
	            if (updatedOrderDate.after(new java.sql.Date(System.currentTimeMillis()))) {
	                showError("Order Date cannot be in the future.");
	                return;
	            }
	        } catch (IllegalArgumentException e) {
	            showError("Invalid Order Date format! Use YYYY-MM-DD.");
	            return;
	        }

	        // ✅ 2. Validate Shipment Date
	        java.sql.Date updatedShipmentDate;
	        try {
	            updatedShipmentDate = java.sql.Date.valueOf(shipmentDateText);
	            if (updatedShipmentDate.before(updatedOrderDate)) {
	                showError("Shipment Date cannot be before Order Date.");
	                return;
	            }
	        } catch (IllegalArgumentException e) {
	            showError("Invalid Shipment Date format! Use YYYY-MM-DD.");
	            return;
	        }

	        // ✅ 3. Validate Expected Delivery Date
	        java.sql.Date updatedExpectedDelivery;
	        try {
	            updatedExpectedDelivery = java.sql.Date.valueOf(expectedDeliveryText);
	            if (updatedExpectedDelivery.before(updatedOrderDate)) {
	                showError("Expected Delivery Date cannot be before Order Date.");
	                return;
	            }
	        } catch (IllegalArgumentException e) {
	            showError("Invalid Expected Delivery Date format! Use YYYY-MM-DD.");
	            return;
	        }

	        // ✅ 4. Validate Priority (Must be 1-5)
	        int updatedPriority;
	        try {
	            updatedPriority = Integer.parseInt(priorityText);
	            if (updatedPriority < 1 || updatedPriority > 5) {
	                showError("Priority must be between 1 and 5.");
	                return;
	            }
	        } catch (NumberFormatException e) {
	            showError("Priority must be a number between 1 and 5.");
	            return;
	        }

	        // ✅ 5. Validate Order Status
	        if (updatedOrderStatus == null || updatedOrderStatus.isEmpty()) {
	            showError("Please select an Order Status.");
	            return;
	        }

	        // ✅ Retrieve the existing order to get the Customer ID
	        UrgentOrder existingOrder = OrderLogic.getInstance().getUrgentOrderByNumber(selectedOrderNumber);
	        if (existingOrder == null) {
	            showError("Order not found.");
	            return;
	        }

	        String customerId = existingOrder.getCustomerId(); // Fetch existing customer ID

	        // ✅ Update Order in Database
	        UrgentOrder updatedOrder = new UrgentOrder(selectedOrderNumber, updatedOrderDate, updatedOrderStatus,
	                updatedShipmentDate, updatedEmployeeId, updatedPriority, updatedExpectedDelivery, customerId);

	        boolean isUpdated = OrderLogic.getInstance().updateUrgentOrder(updatedOrder);

	        if (isUpdated) {
	            JOptionPane.showMessageDialog(this, "Order updated successfully.", "Success",
	                    JOptionPane.INFORMATION_MESSAGE);
	        } else {
	            showError("Failed to update order. Please check the details.");
	        }
	    } catch (Exception e) {
	        handleError("Error updating order", e);
	    }
	}
	private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
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

	public void refreshOrders() {
	    try {
	        SwingUtilities.invokeLater(() -> {
	            String selectedOrder = (String) cbOrderNumber.getSelectedItem(); // ✅ Save currently selected item
	            cbOrderNumber.removeAllItems(); // ✅ Clear previous items
	            cbOrderNumber.addItem("Select Order"); // ✅ Add placeholder item

	            // ✅ Fetch the latest list of urgent orders
	            List<UrgentOrder> urgentOrders = OrderLogic.getInstance().getAllUrgentOrders();

	            for (UrgentOrder order : urgentOrders) {
	                String orderDisplay = order.getOrderNumber() ; // ✅ Show Order ID & Priority
	                cbOrderNumber.addItem(orderDisplay);
	            }

	            // ✅ Restore selection if it still exists
	            if (selectedOrder != null) {
	                for (int i = 0; i < cbOrderNumber.getItemCount(); i++) {
	                    if (cbOrderNumber.getItemAt(i).contains(selectedOrder)) {
	                        cbOrderNumber.setSelectedIndex(i);
	                        break;
	                    }
	                }
	            }

	            System.out.println("✅ Order list refreshed successfully!");
	        });

	    } catch (Exception e) {
	        handleError("❌ Failed to refresh orders", e);
	    }
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new UrgentOrderManagement().setVisible(true));
	}
}
