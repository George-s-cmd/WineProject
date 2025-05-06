package boundary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import control.ProducerLogic;
import entity.Producer;
import entity.Wine;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllProducersFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable producerTable;
    private DefaultTableModel producerTableModel;

    public AllProducersFrame() {
        setTitle("All Producers ");
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
        JLabel titleLabel = new JLabel("All Producers ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

        // Define Table Columns
        String[] columns = {"Producer ID", "Name", "Phone", "Address", "Email", "Associated Wines Catalog Number"};
        producerTableModel = new DefaultTableModel(columns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        producerTable = new JTable(producerTableModel);
        producerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        producerTable.setRowHeight(30);
        producerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        producerTable.getTableHeader().setBackground(new Color(255, 92, 92));
        producerTable.getTableHeader().setForeground(Color.black);
        producerTable.setOpaque(false);
        ((JComponent) producerTable.getDefaultRenderer(Object.class)).setOpaque(true);

        // Adjust column widths
        TableColumnModel columnModel = producerTable.getColumnModel();
        columnModel.getColumn(5).setPreferredWidth(300); // Wine list column wider

        producerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Ensure it adjusts properly

        JScrollPane scrollPane = new JScrollPane(producerTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Force header refresh
        producerTable.getTableHeader().setReorderingAllowed(false);
        producerTable.getTableHeader().setResizingAllowed(true);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Load producer data
        loadProducersData();

        // "Back to Producer Management" button
        JButton backButton = new RoundedButton("Close");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 92, 92));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> 
            dispose()); 
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 255, 255, 200));
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(contentPanel);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void loadProducersData() {
        producerTableModel.setRowCount(0);
        ArrayList<Producer> producers = ProducerLogic.getInstance().getAllProducers();

        for (Producer producer : producers) {
            List<Wine> wines = ProducerLogic.getInstance().getWinesForProducer(producer.getId());

            // Convert wine list to comma-separated string
            String wineList = wines.isEmpty() ? "No Wines" :
                    wines.stream().map(Wine::getCatalogNum).collect(Collectors.joining(", "));

            producerTableModel.addRow(new Object[]{
                    producer.getId(),
                    producer.getName(),
                    producer.getContactPhone(),
                    producer.getAddress(),
                    producer.getEmail(),
                    wineList
            });
        }
    }

    // Custom background panel with image
    class BackgroundPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine3.jpg")).getImage();
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AllProducersFrame().setVisible(true));
    }
}
