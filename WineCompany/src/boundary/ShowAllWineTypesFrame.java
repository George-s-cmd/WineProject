package boundary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import control.WineTypeLogic;
import entity.WineType;

public class ShowAllWineTypesFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable wineTypeTable;
    private DefaultTableModel wineTypeTableModel;

    public ShowAllWineTypesFrame() {
        setTitle("All Wine Types");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("All Wine Types");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Define Table Columns
        String[] columns = {"Wine Type ID", "Wine Type Name"};
        wineTypeTableModel = new DefaultTableModel(columns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        wineTypeTable = new JTable(wineTypeTableModel);
        wineTypeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        wineTypeTable.setRowHeight(30);
        wineTypeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        wineTypeTable.getTableHeader().setBackground(new Color(255, 92, 92));
        wineTypeTable.getTableHeader().setForeground(Color.black);
        wineTypeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(wineTypeTable);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        RoundedButton btnClose = new RoundedButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setBackground(new Color(255, 92, 92));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnClose);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load Wine Types
        loadWineTypes();

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void loadWineTypes() {
        wineTypeTableModel.setRowCount(0); // Clear existing data
        ArrayList<WineType> wineTypes = WineTypeLogic.getInstance().getAllWineTypes();

        for (WineType wineType : wineTypes) {
            wineTypeTableModel.addRow(new Object[]{
                wineType.getSerialNumber(),
                wineType.getName()
            });
        }
    }

    // Background Panel with Image
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
        SwingUtilities.invokeLater(() -> new ShowAllWineTypesFrame().setVisible(true));
    }
}
