package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.util.List;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import control.OrderLogic;
import entity.Customer;
import entity.RegularOrder;
import java.sql.Date;
import java.util.ArrayList;

public class RegularOrderManagement extends MainFrame {
	private static final long serialVersionUID = 1L;

    private JComboBox<String> cbOrderNumber, cbOrderStatus; // ✅ Order Status ComboBox
	private JTextField tfOrderDate, tfShipmentDate, tfEmployeeId,tfMainCustomerId;
	private JTable customerTable;
	private DefaultTableModel orderTableModel;
	private JPanel  customerDetailsPanel, orderPanel;

	public RegularOrderManagement() {
		initializeUI();
	}

	private void initializeUI() {
		try {
			System.out.println("Initializing RegularOrderManagement...");

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
					"Clear Fields","Joint Orders Managment","RegularOrder And Wine" };

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
			System.out.println("RegularOrderManagement initialization complete");

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
				new AddRegularOrderFrame().setVisible(true);
				break;
			case "Edit Order":
				updateOrder();
				break;
			case "Delete Order":
				deleteOrder();
				break;
			case "Show All Orders":
				new ShowAllRegularOrdersFrame();
				break;
			case "Clear Fields":
				clearFields();
				break;
			 case "Joint Orders Managment":
            	 SwingUtilities.invokeLater(() -> new RegularOrderCustomerUI().setVisible(true));
                break;
			 case "RegularOrder And Wine":
               	 SwingUtilities.invokeLater(() -> new RegularOrderWineUI().setVisible(true));
                   break;
			default:
				JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
			}
		});

		return button;
	}

	private void initializeComponents() {
	    cbOrderNumber = createModernComboBox(new String[]{});
	    cbOrderStatus = createOrderStatusComboBox(); // ✅ Ensure this is initialized before using it
	    tfOrderDate = createTextField();
	    tfShipmentDate = createTextField();
	    tfEmployeeId = createTextField();
	    tfMainCustomerId = createTextField(); // ✅ Ensure this is initialized


	    // ✅ Define Table Columns
	    String[] customerColumns = {"Customer ID", "Name", "Phone", "Email", "First Contact", "Delivery Address"};
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

	private JComboBox<String> createOrderStatusComboBox() {
        String[] statuses = {"Paid", "Suspended", "Delivered", "Canceled", "InProcess", "Dispatched"};
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

	 private void styleTable(JTable table) {
	        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
	        table.getTableHeader().setBackground(new Color(255, 92, 92));
	        table.getTableHeader().setForeground(Color.white);
	        table.setOpaque(false);
	        ((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);
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
		orderPanel = new JPanel(new GridBagLayout());
		orderPanel.setBorder(BorderFactory.createTitledBorder("Regular Order Details"));
		orderPanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(orderPanel, gbc, "Order Number:", cbOrderNumber, 0);
		addInputField(orderPanel, gbc, "Order Date:", tfOrderDate, 1);
        addInputField(orderPanel, gbc, "Order Status:", cbOrderStatus, 2); // ✅ Updated from JTextField
		addInputField(orderPanel, gbc, "Shipment Date:", tfShipmentDate, 3);
		addInputField(orderPanel, gbc, "Employee ID:", tfEmployeeId, 4);
        addInputField(orderPanel, gbc, "Main Customer ID:", tfMainCustomerId, 5); // ✅ Added Main Customer ID field

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
	        SwingUtilities.invokeLater(() -> {
	            String selectedOrder = (String) cbOrderNumber.getSelectedItem(); // ✅ Save the currently selected item
	            cbOrderNumber.removeAllItems(); // ✅ Clear previous items
	            cbOrderNumber.addItem("Select Order"); // ✅ Add placeholder item

	            // ✅ Fetch the latest list of regular orders
	            List<RegularOrder> regularOrders = OrderLogic.getInstance().getAllRegularOrders();

	            if (regularOrders.isEmpty()) {
	                System.out.println("⚠ No regular orders found in the database.");
	            } else {
	                for (RegularOrder order : regularOrders) {
	                    String orderNumber = order.getOrderNumber(); // ✅ Ensure order number is retrieved correctly

	                    if (orderNumber != null && ((DefaultComboBoxModel<String>) cbOrderNumber.getModel()).getIndexOf(orderNumber) == -1) {
	                        cbOrderNumber.addItem(orderNumber); // ✅ Add only unique order numbers
	                    }
	                }
	            }

	            // ✅ Restore previously selected order if it still exists
	            if (selectedOrder != null && regularOrders.stream().anyMatch(o -> o.getOrderNumber().equals(selectedOrder))) {
	                cbOrderNumber.setSelectedItem(selectedOrder);
	            }

	            System.out.println("✅ Regular orders loaded successfully!");

	        });
	    } catch (Exception e) {
	        handleError("❌ Failed to load regular order numbers", e);
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

            RegularOrder regularOrder = OrderLogic.getInstance().getRegularOrderByNumber(selectedOrderNumber);

            if (regularOrder != null) {
                tfOrderDate.setText(regularOrder.getOrderDate().toString());
                cbOrderStatus.setSelectedItem(regularOrder.getOrderStatus());
                tfShipmentDate.setText(regularOrder.getShipmentDate().toString());
                tfEmployeeId.setText(regularOrder.getEmployeeId());
                tfMainCustomerId.setText(regularOrder.getCustomerId()); // ✅ Added Main Customer ID

                Customer mainCustomer = OrderLogic.getInstance().getCustomerById(regularOrder.getCustomerId());

                if (mainCustomer != null) {
                    loadMainCustomer(mainCustomer);
                } else {
                    JOptionPane.showMessageDialog(this, "Customer details not found", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Order not found in the database", "Search Result",
                        JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        } catch (Exception e) {
            handleError("Error searching order", e);
        }
    }

	private void updateOrder() {
        try {
            String selectedOrderNumber = (String) cbOrderNumber.getSelectedItem();
            if (selectedOrderNumber == null || selectedOrderNumber.equals("Select Order")) {
                showError("Please select an Order Number to update.");
                return;
            }

            String updatedCustomerId = tfMainCustomerId.getText().trim(); // ✅ Get updated customer ID

            // ✅ Validate Customer ID (Must be exactly 9 digits)
            if (!updatedCustomerId.matches("^\\d{9}$")) {
                showError("Main Customer ID must be exactly 9 digits.");
                return;
            }

            RegularOrder updatedOrder = new RegularOrder(
                    selectedOrderNumber,
                    Date.valueOf(tfOrderDate.getText()),
                    (String) cbOrderStatus.getSelectedItem(),
                    Date.valueOf(tfShipmentDate.getText()),
                    tfEmployeeId.getText(),
                    updatedCustomerId); // ✅ Update customer ID in order

            boolean isUpdated = OrderLogic.getInstance().updateRegularOrder(updatedOrder);
            if (isUpdated) {
                JOptionPane.showMessageDialog(this, "Order updated successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Failed to update order.");
            }
        } catch (Exception e) {
            handleError("Error updating order", e);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

	private void deleteOrder() {
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
				boolean isDeleted = OrderLogic.getInstance().deleteRegularOrder(selectedOrderNumber); // 🔥 Regular
																										// Order
																										// Deletion

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

	private void loadMainCustomer(Customer mainCustomer) {
		orderTableModel.setRowCount(0);

		orderTableModel.addRow(new Object[] { mainCustomer.getPersonId(), mainCustomer.getName(),
				mainCustomer.getPhoneNumber(), mainCustomer.getEmail(), mainCustomer.getDayOfFirstContact(),
				mainCustomer.getDeliveryAddress() });
	}

	private void clearFields() {
	    cbOrderNumber.setSelectedIndex(0); // ✅ Reset without reloading
	    tfOrderDate.setText("");
	    cbOrderStatus.setSelectedIndex(0);
	    tfShipmentDate.setText("");
	    tfEmployeeId.setText("");
	    orderTableModel.setRowCount(0); // ✅ Clear table data
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
		SwingUtilities.invokeLater(() -> new RegularOrderManagement().setVisible(true));
	}
}
