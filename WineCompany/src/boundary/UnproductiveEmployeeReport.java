package boundary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UnproductiveEmployeeReport {
    private JFrame frame;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public UnproductiveEmployeeReport(List<Object[]> employeeData, String startDate, String endDate) {
        frame = new JFrame("Unproductive Employee Report");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(220, 220, 220));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Unproductive Employee Report (" + startDate + " - " + endDate + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames = {"Employee ID", "Name", "Phone", "Email", "Office Address", "Start Date", "Total Orders", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        employeeTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        populateTable(employeeData);
        
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void populateTable(List<Object[]> employeeData) {
        for (Object[] rowData : employeeData) {
            tableModel.addRow(rowData);
        }
    }
}
