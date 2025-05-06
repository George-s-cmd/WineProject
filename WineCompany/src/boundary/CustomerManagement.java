
package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;

import boundary.RoundedComboBoxUI;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import control.CustomerLogic;
import control.OrderLogic;
import entity.Customer;
import entity.Order;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CustomerManagement extends MainFrame {
    private static final long serialVersionUID = 1L;
    public static CustomerManagement instance; 
    private JComboBox<String> cbCustomerId;
    private JTextField tfName, tfPhoneNumber, tfEmail, tfFirstContactDate, tfDeliveryAddress;
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JPanel customerPanel, orderDetailsPanel;

    public CustomerManagement() {
    	instance = this;  // ✅ Assign the instance when created
        initializeUI();
    }

    private void initializeUI() {
        try {
            System.out.println("Initializing CustomerManagement...");

            // ✅ Set the Background Panel
            BackgroundPanel backgroundPanel = new BackgroundPanel();
            backgroundPanel.setLayout(new BorderLayout());
            setContentPane(backgroundPanel);

            JPanel sidebarPanel = new JPanel();
            sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
            sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
            sidebarPanel.setBackground(new Color(255, 255, 255, 100)); // Transparent effect

            sidebarPanel.add(Box.createVerticalStrut(50)); // Push buttons lower

            String[] sidebarItems = {
                "Search Customer", "Add Customer", "Edit Customer",
                "Delete Customer", "Show All Customers", "Clear Fields","Joint Orders Managment",
            };

            for (String item : sidebarItems) {
                RoundedButton button = createSidebarButton(item);
                sidebarPanel.add(button);
                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
            }
            sidebarPanel.add(Box.createVerticalGlue()); // Push buttons upwards to center them


            // ✅ Initialize Components Before Adding Listeners
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);

            // ✅ Ensure details panel and table are initialized
            initializeComponents();
            setupLayout(contentPanel);
           
            loadCustomerIds();

            // ✅ Add Sidebar and Content Panel to Background
            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

            setSize(1200, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            System.out.println("CustomerManagement initialization complete");

        } catch (Exception e) {
            handleError("Critical error during initialization", e);
        }
    }
    private RoundedButton createSidebarButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 92, 92)); // Solid pink
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setMinimumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        // ✅ **Assign functionality**
        button.addActionListener(e -> {
            switch (text) {
                case "Search Customer":
                    searchCustomer();
                    break;
                case "Add Customer":
                    SwingUtilities.invokeLater(() -> {
                        AddCustomerFrame addCustomerFrame = new AddCustomerFrame();
                        addCustomerFrame.setVisible(true);
                    });
                    break;
                case "Edit Customer":
                    editCustomer();
                    break;
                case "Delete Customer":
                    deleteCustomer();
                    break;
                case "Show All Customers":
                    SwingUtilities.invokeLater(() -> {
                        ShowAllCustomersFrame showAllFrame = new ShowAllCustomersFrame();
                        showAllFrame.setVisible(true);
                    });
                    break;
                case "Clear Fields":
                    clearFields();
                    break;
                case "Joint Orders Managment":
                	 SwingUtilities.invokeLater(() -> new RegularOrderCustomerUI().setVisible(true));
                    break;
               
                default:
                    JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
            }
        });

        return button;
    }



    private void initializeComponents() {
        // ✅ Create and Style ComboBox
        cbCustomerId = createModernComboBox(new String[]{"Select Customer"});

        // ✅ Create and Style TextFields
        tfName = createTextField();
        tfPhoneNumber = createTextField();
        tfEmail = createTextField();
        tfFirstContactDate = createTextField();
        tfDeliveryAddress = createTextField();

        // ✅ Define Table Columns
        String[] orderColumns = {"Order Number", "Order Date", "Order Status", "Shipment Date", "Order Type"};
        orderTableModel = new DefaultTableModel(orderColumns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
       
        // ✅ Create and Style Table with Forced Transparency
        orderTable = new JTable(orderTableModel) ;
          styleTable(orderTable);
       

        
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
        customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        customerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        addInputField(customerPanel, gbc, "Customer ID:", cbCustomerId, 0);
        addInputField(customerPanel, gbc, "Name:", tfName, 1);
        addInputField(customerPanel, gbc, "Phone Number:", tfPhoneNumber, 2);
        addInputField(customerPanel, gbc, "Email:", tfEmail, 3);
        addInputField(customerPanel, gbc, "First Contact Date:", tfFirstContactDate, 4);
        addInputField(customerPanel, gbc, "Delivery Address:", tfDeliveryAddress, 5);

        orderDetailsPanel = new JPanel(new BorderLayout());
        orderDetailsPanel.setBorder(BorderFactory.createTitledBorder("Orders Made by Customer"));
        orderDetailsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Removes any borders
		orderDetailsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(customerPanel, BorderLayout.NORTH);
        mainPanel.add(orderDetailsPanel, BorderLayout.CENTER);
    }


   
    
    private void deleteCustomer() {
        try {
            String selectedCustomerId = (String) cbCustomerId.getSelectedItem();

            if (selectedCustomerId == null || selectedCustomerId.equals("Select Customer")) {
                JOptionPane.showMessageDialog(this, "Please select a Customer ID", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if the customer has any associated orders
            ArrayList<Order> customerOrders = OrderLogic.getInstance().getOrdersForCustomer(selectedCustomerId);

            if (!customerOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cannot delete customer! They have existing orders.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm before deleting
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = CustomerLogic.getInstance().deleteCustomer(selectedCustomerId);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadCustomerIds(); // Refresh list after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete customer!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            handleError("Error deleting customer", e);
        }
    }




    public void loadCustomerIds() {
        try {
            cbCustomerId.removeAllItems(); // ✅ Clear existing items
            cbCustomerId.addItem("Select Customer"); // ✅ Add default selection

            List<Customer> customers = OrderLogic.getInstance().getAllCustomers(); // ✅ Fetch all customers

            for (Customer customer : customers) {
                cbCustomerId.addItem(customer.getPersonId()); // ✅ Load Customer IDs correctly
            }

        } catch (Exception e) {
            handleError("Failed to load Customer IDs", e);
        }
    }



    private void searchCustomer() {
        try {
            String selectedCustomerId = (String) cbCustomerId.getSelectedItem();

            if (selectedCustomerId == null || selectedCustomerId.equals("Select Customer")) {
                JOptionPane.showMessageDialog(this, "Please select a Customer ID", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer customer = OrderLogic.getInstance().getCustomerById(selectedCustomerId);

            if (customer != null) {
                tfName.setText(customer.getName());
                tfPhoneNumber.setText(customer.getPhoneNumber());
                tfEmail.setText(customer.getEmail());
                tfFirstContactDate.setText(customer.getDayOfFirstContact().toString());
                tfDeliveryAddress.setText(customer.getDeliveryAddress());

                // Load orders for this customer
                loadOrdersForCustomer(selectedCustomerId);
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found in the database", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        } catch (Exception e) {
            handleError("Error searching customer", e);
        }
    }

    private void loadOrdersForCustomer(String customerId) {
        orderTableModel.setRowCount(0);

        ArrayList<Order> orders = OrderLogic.getInstance().getOrdersForCustomer(customerId);

        for (Order order : orders) {
            orderTableModel.addRow(new Object[]{
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getShipmentDate(),
                (order instanceof entity.UrgentOrder) ? "Urgent Order" : "Regular Order"
            });
        }
    }
    private void editCustomer() {
        try {
            String selectedCustomerId = (String) cbCustomerId.getSelectedItem();

            if (selectedCustomerId == null || selectedCustomerId.equals("Select Customer")) {
                showError("Please select a Customer ID.");
                return;
            }

            String name = tfName.getText().trim();
            String phoneNumber = tfPhoneNumber.getText().trim();
            String email = tfEmail.getText().trim();
            String firstContactDateStr = tfFirstContactDate.getText().trim();
            String deliveryAddress = tfDeliveryAddress.getText().trim();

            // ✅ 1. Validate Customer ID (9 digits)
            if (!selectedCustomerId.matches("^\\d{9}$")) {
                showError("Customer ID must be exactly 9 digits.");
                return;
            }

            // ✅ 2. Validate Name (Only letters, min 2 characters)
            if (name.isEmpty() || !name.matches("^[a-zA-Z\\s]+$") || name.length() < 2) {
                showError("Name must contain only letters and be at least 2 characters long.");
                return;
            }

            // ✅ 3. Validate Phone Number (Exactly 10 digits)
            if (!phoneNumber.matches("^\\d{10}$")) {
                showError("Phone Number must be exactly 10 digits.");
                return;
            }

            // ✅ 4. Validate Email Format
            if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
                showError("Invalid Email format.");
                return;
            }

            // ✅ 5. Validate First Contact Date (Valid format, cannot be in the future)
            java.sql.Date firstContactDate;
            try {
                firstContactDate = java.sql.Date.valueOf(firstContactDateStr);
                if (firstContactDate.after(Date.valueOf(LocalDate.now()))) {
                    showError("First Contact Date cannot be in the future.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                showError("Invalid date format! Please use YYYY-MM-DD.");
                return;
            }

            // ✅ 6. Validate Delivery Address (At least 5 characters)
            if (deliveryAddress.length() < 5) {
                showError("Delivery Address must be at least 5 characters long.");
                return;
            }

            // ✅ Create Customer Object
            Customer updatedCustomer = new Customer(selectedCustomerId, name, phoneNumber, email, firstContactDate, deliveryAddress);

            // ✅ Update Customer in Database
            boolean success = CustomerLogic.getInstance().updateCustomer(updatedCustomer);

            if (success) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadCustomerIds(); // Refresh customer list
            } else {
                showError("Failed to update customer! Please check the details.");
            }

        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
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

   
    private void clearFields() {
        cbCustomerId.setSelectedIndex(0);
        tfName.setText("");
        tfPhoneNumber.setText("");
        tfEmail.setText("");
        tfFirstContactDate.setText("");
        tfDeliveryAddress.setText("");
        orderTableModel.setRowCount(0);
    }
    private void handleError(String message, Exception ex) {
        System.err.println(message + ": " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                message + "\nError: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerManagement().setVisible(true));
    }
}