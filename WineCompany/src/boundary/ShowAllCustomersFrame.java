package boundary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import control.OrderLogic;
import entity.Customer;

public class ShowAllCustomersFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JButton btnClose;

    public ShowAllCustomersFrame() {
        setTitle("All Customers");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // ✅ Fixed Title Panel (Added Directly to Background Panel)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(getWidth(), 60)); // Ensure proper height
        titlePanel.setBackground(new Color(255, 255, 255, 200));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Centered text

        JLabel titleLabel = new JLabel("All Customers");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));

        titlePanel.add(titleLabel);
        backgroundPanel.add(titlePanel, BorderLayout.NORTH); // ✅ Now properly added!

        // ✅ Initialize Components
        initializeComponents(backgroundPanel);
        loadCustomers();

        setVisible(true);
    }

    private void initializeComponents(JPanel backgroundPanel) {
        // Define column names for the table
        String[] columns = {
            "Customer ID", "Name", "Phone Number", "Email", 
            "First Contact Date", "Delivery Address", "Order Numbers"
        };

        customerTableModel = new DefaultTableModel(columns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(customerTableModel);
        styleTable(customerTable);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // ✅ Close Button
        btnClose = new RoundedButton("Close");
        btnClose.setBackground(new Color(255, 92, 92));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnClose.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnClose);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        customerTableModel.setRowCount(0); // Clear table before loading

        ArrayList<Customer> customers = OrderLogic.getInstance().getAllCustomers();

        for (Customer customer : customers) {
            // Fetch all order numbers for this customer
            ArrayList<String> orderNumbers = OrderLogic.getInstance().getOrderNumbersForCustomer(customer.getPersonId());
            String orderNumbersString = String.join(", ", orderNumbers); // Convert list to a string

            customerTableModel.addRow(new Object[]{
                customer.getPersonId(),
                customer.getName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getDayOfFirstContact(),
                customer.getDeliveryAddress(),
                orderNumbersString // Add order numbers column
            });
        }
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(255, 92, 92));
        table.getTableHeader().setForeground(Color.white);
        table.setOpaque(false);
        ((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);
    }

    // ✅ Background Panel with Image
    class BackgroundPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg")).getImage();
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShowAllCustomersFrame().setVisible(true));
    }
}
