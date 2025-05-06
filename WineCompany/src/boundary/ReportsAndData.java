package boundary;


import control.ProducerLogic;
import control.UnproductiveEmployeeReportLogic;
import control.WineInventoryDAO;
import control.WineLogic;
import entity.Consts.Manipulation;
import entity.Producer;
import entity.Wine;
import net.sf.jasperreports.engine.JRException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.NodeList;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReportsAndData extends MainFrame {
    private static final long serialVersionUID = 1L;
    private static ReportsAndData instance; // ✅ Singleton instance

    // ✅ Get Singleton Instance
    public static ReportsAndData getInstance() {
        if (instance == null) {
            instance = new ReportsAndData();
        }
        return instance;
    }

    public ReportsAndData() {
        setTitle("Reports & Data Management");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // ✅ Buttons
        JButton btnImportXML = createStyledButton("Import XML from Gefen System", e -> importXML());
        JButton btnInventoryReport = createStyledButton("Generate Inventory Report", e -> generateInventoryReport());
        JButton btnEmployeeReport = createStyledButton("Generate Unproductive Employee Report", e -> generateEmployeeReport());
       

     // ✅ Add buttons to layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(btnImportXML, gbc);

        gbc.gridy = 1;
        backgroundPanel.add(btnInventoryReport, gbc);

        gbc.gridy = 2;
        backgroundPanel.add(btnEmployeeReport, gbc);

        // ✅ Add "Send to Gefen System" Button
        JButton btnWineRecommendation = createStyledButton("Generate Wine Recommendation Report", e -> generateWineRecommendationReport());
       
        gbc.gridy = 3;
        backgroundPanel.add(btnWineRecommendation, gbc);

        // ✅ Add "Generate Wine Recommendation Report" Button
        JButton btnSendToGefen = createStyledButton("Generate Json Format And sent fotr Inventory Report ", e -> displayJsonInventory());
        gbc.gridy = 4;
       
        backgroundPanel.add(btnSendToGefen, gbc);


//        backgroundPanel.add(btnWineRecommendation, gbc);
    }
   
    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(255, 127, 182));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }

  

    private void importXML() {
        try {
            WineLogic.getInstance().importWineFromXML("xml/new_wines.xml");
            ProducerLogic.getInstance().importProducersFromXML("xml/new_wines.xml");
            
            // Display success message
            JOptionPane.showMessageDialog(
                this, 
                "Import completed successfully!", 
                "Import Successful", 
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            // Display error message with the specific reason
            JOptionPane.showMessageDialog(
                this,
                "Import failed: " + e.getMessage(),
                "Import Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void generateInventoryReport() {
        SwingUtilities.invokeLater(() -> new InventoryReport());
    }
    private void displayJsonInventory() {
        try {
            // ✅ Call exportInventoryToJSON() method from WineInventoryDAO
            boolean success = WineInventoryDAO.getInstance().exportInventoryToJSON();

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "✅ Inventory JSON file generated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "⚠ JSON export failed! Check logs for details.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "❌ Unexpected error while generating JSON: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }





    private void generateEmployeeReport() {
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Date Range",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();

            // ✅ Validate the dates
            if (!isValidDate(startDate) || !isValidDate(endDate)) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isStartDateBeforeEndDate(startDate, endDate)) {
                JOptionPane.showMessageDialog(this, "Start date must be before or equal to end date.",
                        "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ Fetch unproductive employee data using EmployeeReportLogic
            List<Object[]> employeeData = UnproductiveEmployeeReportLogic.getInstance().getUnproductiveEmployees(startDate, endDate);

            // ✅ Open GUI with the fetched data
            SwingUtilities.invokeLater(() -> new UnproductiveEmployeeReport(employeeData, startDate, endDate));
        }
    }

    // Helper method to validate date format (YYYY-MM-DD)
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Helper method to check if startDate is before or equal to endDate
    private boolean isStartDateBeforeEndDate(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            return !startDate.after(endDate);  // startDate should be before or equal to endDate
        } catch (ParseException e) {
            return false;
        }
    }


    private void generateWineRecommendationReport() {
        SwingUtilities.invokeLater(() -> {
            WineFilterApp wineFilterApp = new WineFilterApp();
            wineFilterApp.createAndShowGUI(); // Ensures the GUI is properly displayed
        });
    }





    // ✅ Background Panel with Image
    class BackgroundPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg")).getImage();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportsAndData().setVisible(true));
    }
}
