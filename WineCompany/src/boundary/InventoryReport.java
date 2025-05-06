package boundary;

import entity.StockedWine;
import control.WineInventoryDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryReport {
    private JFrame frame;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryReport() {
        frame = new JFrame("Inventory Report");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(220, 220, 220));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Current Wine Inventory Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Catalog Number", "Producer ID", "Storage Location", "Quantity"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        populateTable();
        
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void populateTable() {
        List<StockedWine> inventory = WineInventoryDAO.getInstance().getCurrentInventory();
        for (StockedWine wine : inventory) {
            Object[] rowData = {
                wine.getCatalogNumber(),
                wine.getProducerId(),
                wine.getStorageLocationId(),
                wine.getQuantity()
            };
            tableModel.addRow(rowData);
        }
    }
}
