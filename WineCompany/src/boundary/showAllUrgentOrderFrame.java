package boundary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import control.OrderLogic;
import entity.UrgentOrder;

public class showAllUrgentOrderFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable orderTable;
    private DefaultTableModel orderTableModel;

    public showAllUrgentOrderFrame() {
        setTitle("All Urgent Orders");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();

        // Create semi-transparent content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 255, 255, 200));
        contentPanel.setOpaque(false);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 255, 255, 200));
        JLabel titleLabel = new JLabel("All Urgent Orders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

        // Define Table Columns
        String[] orderColumns = {"Order Number", "Order Date", "Order Status", "Shipment Date", "Employee ID", "Priority", "Expected Delivery", "Customer ID"};

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

        orderTable = new JTable(orderTableModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderTable.getTableHeader().setBackground(new Color(255, 92, 92));
        orderTable.getTableHeader().setForeground(Color.black);
        orderTable.setOpaque(false);
        ((JComponent) orderTable.getDefaultRenderer(Object.class)).setOpaque(true);

        // Adjust column widths
        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150); // Order Number
        columnModel.getColumn(1).setPreferredWidth(120); // Order Date
        columnModel.getColumn(6).setPreferredWidth(180); // Expected Delivery
        columnModel.getColumn(7).setPreferredWidth(150); // Customer ID column


        orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Load Orders Data
        loadAllOrders();

        // "Back to Urgent Order Management" button
        JButton backButton = new RoundedButton("Close");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 92, 92));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> 
            dispose());
            ;

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 255, 255, 200));
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(contentPanel);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void loadAllOrders() {
        orderTableModel.setRowCount(0); // Clear existing rows

        try {
            // ✅ Fetch all urgent orders from the database
            List<UrgentOrder> orders = OrderLogic.getInstance().getAllUrgentOrders();

            if (orders.isEmpty()) {
                System.out.println("⚠ No urgent orders found in the database.");
            } else {
                for (UrgentOrder order : orders) {
                    orderTableModel.addRow(new Object[]{
                        order.getOrderNumber(),
                        (order.getOrderDate() != null) ? order.getOrderDate().toString() : "N/A", // ✅ Avoid null pointer exception
                        order.getOrderStatus(),
                        (order.getShipmentDate() != null) ? order.getShipmentDate().toString() : "N/A", // ✅ Avoid null pointer exception
                        order.getEmployeeId(),
                        order.getPriority(),
                        (order.getExpectedDelivery() != null) ? order.getExpectedDelivery().toString() : "N/A", // ✅ Avoid null pointer exception
                        order.getCustomerId()
                    });
                }
            }

            System.out.println("✅ Urgent orders loaded successfully.");

        } catch (Exception e) {
            System.err.println("❌ Error loading urgent orders: " + e.getMessage());
            e.printStackTrace();
        }
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
        SwingUtilities.invokeLater(() -> new showAllUrgentOrderFrame().setVisible(true));
    }
}
