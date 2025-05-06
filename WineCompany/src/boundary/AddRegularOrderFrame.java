package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import control.OrderLogic;
import control.EmployeeDAO;
import entity.RegularOrder;
import entity.Customer;
import entity.Employee;

public class AddRegularOrderFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cbEmployeeId, cbCustomerId, cbOrderStatus; // ✅ Order Status ComboBox
    private JTextField tfOrderNumber, tfOrderDate, tfShipmentDate;
    private RoundedButton btnAdd, btnClear, btnBack;

    public AddRegularOrderFrame() {
        setTitle("Add New Regular Order");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Adding New Regular Order");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // ✅ Initialize Components
        tfOrderNumber = new JTextField(15);
        tfOrderDate = new JTextField(15);
        tfShipmentDate = new JTextField(15);
        cbEmployeeId = createModernComboBox();
        cbCustomerId = createModernComboBox();
        cbOrderStatus = createOrderStatusComboBox(); // ✅ Order Status Styled Dropdown

        btnAdd = createRoundedButton("Add Order");
        btnClear = createRoundedButton("Clear");
        btnBack = createRoundedButton("Close");

        // ✅ Load dropdown values
        loadEmployeeIds();
        loadCustomerIds();

        // ✅ Add components to layout
        addInputField("Order Number:", tfOrderNumber, gbc, backgroundPanel, 1);
        addInputField("Order Date (YYYY-MM-DD):", tfOrderDate, gbc, backgroundPanel, 2);
        addInputField("Order Status:", cbOrderStatus, gbc, backgroundPanel, 3); // ✅ Changed from TextField to ComboBox
        addInputField("Shipment Date (YYYY-MM-DD):", tfShipmentDate, gbc, backgroundPanel, 4);
        addInputField("Employee ID:", cbEmployeeId, gbc, backgroundPanel, 5);
        addInputField("Customer ID:", cbCustomerId, gbc, backgroundPanel, 6);

        // ✅ Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        backgroundPanel.add(buttonPanel, gbc);

        // ✅ Button Actions
        btnAdd.addActionListener(e -> addOrder());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void addInputField(String label, JComponent field, GridBagConstraints gbc, JPanel panel, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.black);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadEmployeeIds() {
	    try {
	        cbEmployeeId.removeAllItems(); // ✅ Clear existing items first
	        cbEmployeeId.addItem("Select Employee"); // ✅ Add default selection

	        List<Employee> employees = EmployeeDAO.getInstance().getAllEmployees(); // ✅ Fetch all employee objects

	        for (Employee employee : employees) {
	            cbEmployeeId.addItem(employee.getPersonId()); // ✅ Add Employee ID to dropdown
	        }

	    } catch (Exception e) {
	        handleError("Failed to load Employee IDs", e);
	    }
	}
    private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}

    private void loadCustomerIds() {
        cbCustomerId.addItem("Select Customer");
        ArrayList<Customer> customers = OrderLogic.getInstance().getAllCustomers();
        for (Customer customer : customers) {
            cbCustomerId.addItem(customer.getPersonId());
        }
    }

    private void addOrder() {
        try {
            String orderNumber = tfOrderNumber.getText().trim();
            String orderDateText = tfOrderDate.getText().trim();
            String shipmentDateText = tfShipmentDate.getText().trim();
            String orderStatus = (String) cbOrderStatus.getSelectedItem();
            String employeeId = (String) cbEmployeeId.getSelectedItem();
            String mainCustomerId = (String) cbCustomerId.getSelectedItem();

            

            // ✅ 2. Check if Order Number Already Exists
            if (OrderLogic.getInstance().regularOrderExists(orderNumber)) {
                showError("An order with this number already exists.");
                return;
            }

            // ✅ 3. Validate Order Date
            java.sql.Date orderDate;
            try {
                orderDate = java.sql.Date.valueOf(orderDateText);
                if (orderDate.after(new java.sql.Date(System.currentTimeMillis()))) {
                    showError("Order Date cannot be in the future.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                showError("Invalid Order Date format! Use YYYY-MM-DD.");
                return;
            }

            // ✅ 4. Validate Shipment Date
            java.sql.Date shipmentDate;
            try {
                shipmentDate = java.sql.Date.valueOf(shipmentDateText);
                if (shipmentDate.before(orderDate)) {
                    showError("Shipment Date cannot be before Order Date.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                showError("Invalid Shipment Date format! Use YYYY-MM-DD.");
                return;
            }

            // ✅ 5. Validate Order Status
            if (orderStatus == null || orderStatus.isEmpty()) {
                showError("Please select an Order Status.");
                return;
            }

            // ✅ 6. Validate Employee ID & Customer ID
            if (employeeId.equals("Select Employee") || mainCustomerId.equals("Select Customer")) {
                showError("Please select valid Employee and Customer IDs.");
                return;
            }

            // ✅ Create and Save Regular Order
            RegularOrder newOrder = new RegularOrder(orderNumber, orderDate, orderStatus, shipmentDate, employeeId, mainCustomerId);
            boolean success = OrderLogic.getInstance().addRegularOrder(newOrder);

            if (success) {
                // ✅ Insert into RegularOrderCustomer using mainCustomerId
                boolean addedToRegularOrderCustomer = OrderLogic.getInstance().addRegularOrderCustomer(orderNumber, mainCustomerId);

                if (!addedToRegularOrderCustomer) {
                    showError("Order added, but failed to associate customer.");
                }

                JOptionPane.showMessageDialog(this, "Order added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // ✅ Refresh the Order-Customer UI if it's open
                if (RegularOrderCustomerUI.instance != null) {
                    RegularOrderCustomerUI.instance.refreshOrderCustomerData();
                }

                clearFields();
                dispose();
            } else {
                showError("Failed to add order! Please check the details.");
            }
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }


    private void clearFields() {
        tfOrderNumber.setText("");
        tfOrderDate.setText("");
        cbOrderStatus.setSelectedIndex(0); // ✅ Reset dropdown
        tfShipmentDate.setText("");
        cbEmployeeId.setSelectedIndex(0);
        cbCustomerId.setSelectedIndex(0);
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

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setUI(new RoundedComboBoxUI());
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return comboBox;
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
		private final Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg")).getImage();
            setLayout(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddRegularOrderFrame().setVisible(true));
    }
}
