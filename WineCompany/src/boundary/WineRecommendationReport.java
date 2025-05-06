package boundary;

import entity.Wine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class WineRecommendationReport {
    private JFrame frame;
    private JTable wineTable;
    private DefaultTableModel tableModel;

    public WineRecommendationReport(List<Wine> wines) {
        frame = new JFrame("Wine Report");
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create a title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(220, 220, 220));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add space
        JLabel titleLabel = new JLabel("Wine Recommendation Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        String[] columnNames = {"Catalog Number", "Producer ID", "Name", "Production Year", "Price", "Sweetness", "Description", "Image URL"};
        tableModel = new DefaultTableModel(columnNames, 0);
        wineTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        wineTable.setRowHeight(25);
        wineTable.getColumnModel().getColumn(6).setPreferredWidth(300); // Ensure description is fully visible

        // Set table header font and visibility
        JTableHeader tableHeader = wineTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 30));

        JScrollPane scrollPane = new JScrollPane(wineTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add space from title panel

        populateTable(wines);
        
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void populateTable(List<Wine> wines) {
        for (Wine wine : wines) {
            Object[] rowData = {
                wine.getCatalogNum(),
                wine.getProducerId(),
                wine.getName(),
                wine.getProductionYear(),
                "$" + wine.getPricePerBottle(),
                wine.getSweetnessLevel(),
                wine.getDescription(),
                wine.getProductImage() // Now treated as a string, not an image
            };
            tableModel.addRow(rowData);
        }
    }
}