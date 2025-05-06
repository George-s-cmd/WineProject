package boundary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import control.OrderLogic;
import entity.RegularOrder;

public class ShowAllRegularOrdersFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    public ShowAllRegularOrdersFrame() {
        setTitle("All Regular Orders");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Background Panel with Image
        BackgroundPanel backgroundPanel = new BackgroundPanel();

        // ✅ Content Panel with Transparency
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 255, 255, 200));
        contentPanel.setOpaque(false);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 255, 255, 200));
        JLabel titleLabel = new JLabel("All Regular Orders ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        // ✅ Define Table Columns
        String[] orderColumns = {"Order Number", "Order Date", "Order Status", "Shipment Date", "Employee ID", "Customer ID"};
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
        styleTable(orderTable);

        // ✅ Adjust column widths for better visibility
        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150); // Order Number
        columnModel.getColumn(1).setPreferredWidth(120); // Order Date
        columnModel.getColumn(2).setPreferredWidth(120); // Order Status
        columnModel.getColumn(3).setPreferredWidth(150); // Shipment Date
        columnModel.getColumn(4).setPreferredWidth(120); // Employee ID
        columnModel.getColumn(5).setPreferredWidth(120); // Customer ID

        // ✅ Scroll Pane for Table
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // ✅ Load Data into Table
        loadAllOrders();

        // ✅ Close Button (Matching `showAllUrgentOrderFrame`)
        JButton btnClose = new RoundedButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setBackground(new Color(255, 92, 92));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        // ✅ Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 255, 255, 200));
        buttonPanel.add(btnClose);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ✅ Add Everything to the Frame
        backgroundPanel.add(contentPanel);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void loadAllOrders() {
        orderTableModel.setRowCount(0);
        ArrayList<RegularOrder> orders = OrderLogic.getInstance().getAllRegularOrders();

        for (RegularOrder order : orders) {
            orderTableModel.addRow(new Object[]{
                order.getOrderNumber(),
                order.getOrderDate().toString(),
                order.getOrderStatus(),
                order.getShipmentDate().toString(),
                order.getEmployeeId(),
                order.getCustomerId()
            });
        }
    }

    // ✅ Style Table
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
        SwingUtilities.invokeLater(() -> new ShowAllRegularOrdersFrame().setVisible(true));
    }
}
