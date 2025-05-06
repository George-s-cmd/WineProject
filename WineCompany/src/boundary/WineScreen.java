package boundary;

import entity.Wine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import control.WineLogic;

import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WineScreen extends MainFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final List<Wine> filteredWines;

    public WineScreen(List<Wine> filteredWines) {
        this.filteredWines = filteredWines;
        createAndShowGUI(); // ✅ Ensure GUI is initialized when the object is created
    }



    // Custom panel class for background image
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
        SwingUtilities.invokeLater(() -> {
            List<Wine> winesList = WineLogic.getInstance().getAllWines(); // ✅ Fetch all wine objects directly

            // ✅ Open WineScreen with correctly formatted data
            new WineScreen(winesList);
        });
    }


    public void createAndShowGUI() {
        setTitle("Filtered Wines");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();

        // Create semi-transparent overlay panel for content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 255, 255, 200));
        contentPanel.setOpaque(false);

        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 255, 255, 200));
        JLabel titleLabel = new JLabel("Wine List");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

     /// Define column headers
        String[] columnNames = {
        	    "Catalog Number", "Producer ID", "Name", "Production Year",
        	    "Price Per Bottle", "Sweetness Level", "Description", "Product Image", "Wine Type ID"
        	};

        	// Create table model with column headers
        	DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);


        if (filteredWines != null && !filteredWines.isEmpty()) {
            for (Wine wine : filteredWines) {
                Object[] row = new Object[]{
                    wine.getCatalogNum(),
                    wine.getProducerId(),
                    wine.getName(),
                    wine.getProductionYear(),
                    wine.getPricePerBottle(),
                    wine.getSweetnessLevel(),
                    wine.getDescription(),
                    wine.getProductImage(),
                    wine.getWineTypeId()
                };
                tableModel.addRow(row);
            }
        }

        JTable wineTable = new JTable(tableModel);


       
        wineTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        wineTable.setRowHeight(30);
        wineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        wineTable.getTableHeader().setBackground(new Color(255, 92, 92));
        wineTable.getTableHeader().setForeground(Color.black);
        wineTable.setOpaque(false);
        ((JComponent)wineTable.getDefaultRenderer(Object.class)).setOpaque(true);

        // Adjust column widths
        TableColumnModel columnModel = wineTable.getColumnModel();
        columnModel.getColumn(6).setPreferredWidth(200);

        wineTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Ensure it adjusts properly

        JScrollPane scrollPane = new JScrollPane(wineTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Force header refresh
        wineTable.getTableHeader().setReorderingAllowed(false);
        wineTable.getTableHeader().setResizingAllowed(true);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(252, 201, 113);
                this.trackColor = new Color(200, 200, 200);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JButton goToFilterAppButton = new JButton("Close") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        goToFilterAppButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        goToFilterAppButton.setBackground(new Color(255, 92, 92));
        goToFilterAppButton.setForeground(Color.WHITE);
        goToFilterAppButton.setFocusPainted(false);
        goToFilterAppButton.setContentAreaFilled(false);
        goToFilterAppButton.setOpaque(false);

        FontMetrics metrics = goToFilterAppButton.getFontMetrics(goToFilterAppButton.getFont());
        int textWidth = metrics.stringWidth(goToFilterAppButton.getText());
        int padding = 40;
        goToFilterAppButton.setPreferredSize(new Dimension(textWidth + padding, 50));

        goToFilterAppButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToFilterAppButton.addActionListener(e -> 
            dispose()); // Close the current WineScreen
            
      

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 255, 255, 200));
        buttonPanel.add(goToFilterAppButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(contentPanel);
        setContentPane(backgroundPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}