package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import control.ProducerLogic;
import entity.Producer;
import entity.Wine;
import java.util.ArrayList;

public class FrameProducer extends MainFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbProducerId; // Producer ID ComboBox
    private JTextField tfName, tfContactPhone, tfAddress, tfEmail; 
    private JTable wineTable;
    private DefaultTableModel wineTableModel;
    private JPanel producerPanel, wineDetailsPanel;
    private static FrameProducer instance; // ✅ Singleton instance

    public static FrameProducer getInstance() {
        return instance;
    }

    public FrameProducer() {
        instance = this; // ✅ Store instance reference
        initializeUI();
    }
    private void initializeUI() {
        try {
            System.out.println("Initializing FrameProducer...");

            // ✅ Create Background Panel
            BackgroundPanel backgroundPanel = new BackgroundPanel();
            backgroundPanel.setLayout(new BorderLayout());

            // ✅ Create Sidebar Panel
            JPanel sidebarPanel = new JPanel();
            sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
            sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
            sidebarPanel.setBackground(new Color(255, 255, 255, 100));

            sidebarPanel.add(Box.createVerticalStrut(50));

            String[] sidebarItems = {
                "Search Producer", "Add Producer", "Edit Producer",
                "Delete Producer", "Show All Producers", "Clear Fields"
            };

            for (String item : sidebarItems) {
                RoundedButton button = createSidebarButton(item);
                sidebarPanel.add(button);
                sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
            }
            sidebarPanel.add(Box.createVerticalGlue());

            // ✅ Initialize Components
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);

            initializeComponents();
            setupLayout(contentPanel);
            loadProducerIds();

            // ✅ Add Sidebar & Content to Background Panel
            backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
            backgroundPanel.add(contentPanel, BorderLayout.CENTER);

            setContentPane(backgroundPanel);
            setSize(1200, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            System.out.println("FrameProducer initialization complete");

        } catch (Exception e) {
            handleError("Critical error during initialization", e);
        }
    }
    private RoundedButton createSidebarButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.white);
        button.setBackground(new Color(255, 92, 92)); // Pink theme
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setMinimumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        // ✅ Assign functionality
        button.addActionListener(e -> {
            switch (text) {
                case "Search Producer":
                    searchProducer();
                    break;
                case "Add Producer":
                    new AddProducerFrame().setVisible(true);
                    break;
                case "Edit Producer":
                    editProducer();
                    break;
                case "Delete Producer":
                    deleteProducer();
                    break;
                case "Show All Producers":
                   new AllProducersFrame().setVisible(true);
                    break;
                case "Clear Fields":
                    clearFields();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
            }
        });

        return button;
    }

    private void initializeComponents() {
    	cbProducerId = createModernComboBox(new String[]{}); 
        tfName = createTextField();
        tfContactPhone = createTextField();
        tfAddress = createTextField();
        tfEmail = createTextField();

        // ✅ Define Table Columns
        String[] wineColumns = {"Catalog Number", "Name", "Year", "Price", "Sweetness", "Description"};
        wineTableModel = new DefaultTableModel(wineColumns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        wineTable = new JTable(wineTableModel);
        styleTable(wineTable);
    }

    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setUI(new RoundedComboBoxUI());
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        comboBox.setOpaque(false);
        return comboBox;
    }


    private void setupLayout(JPanel mainPanel) {
        producerPanel = new JPanel(new GridBagLayout());
        producerPanel.setBorder(BorderFactory.createTitledBorder("Producer Details"));
        producerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        addInputField(producerPanel, gbc, "Producer ID:", cbProducerId, 0);
        addInputField(producerPanel, gbc, "Name:", tfName, 1);
        addInputField(producerPanel, gbc, "Phone:", tfContactPhone, 2);
        addInputField(producerPanel, gbc, "Address:", tfAddress, 3);
        addInputField(producerPanel, gbc, "Email:", tfEmail, 4);

        wineDetailsPanel = new JPanel(new BorderLayout());
        wineDetailsPanel.setBorder(BorderFactory.createTitledBorder("Producer Wines"));
        wineDetailsPanel.setOpaque(false);

        JScrollPane wineScrollPane = new JScrollPane(wineTable);
        wineScrollPane.setOpaque(false);
        wineScrollPane.getViewport().setOpaque(false);
        wineScrollPane.setBorder(BorderFactory.createEmptyBorder());
        wineDetailsPanel.add(wineScrollPane, BorderLayout.CENTER);

        mainPanel.add(producerPanel, BorderLayout.NORTH);
        mainPanel.add(wineDetailsPanel, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(255, 92, 92));
        table.getTableHeader().setForeground(Color.white);
        table.setOpaque(false);
        ((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);
    }
    private JTextField createTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

   

    private void addInputField(JPanel panel, GridBagConstraints gbc, String label, JComponent component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(component, gbc);
    }

   
   

    private void loadProducerIds() {
        try {
            cbProducerId.removeAllItems(); // ✅ Clear existing items first
            cbProducerId.addItem("Select Producer"); // ✅ Add only once

            ArrayList<Producer> producers = ProducerLogic.getInstance().getAllProducers();
            for (Producer producer : producers) {
                cbProducerId.addItem(producer.getId()); // ✅ Add only unique producer IDs
            }
        } catch (Exception e) {
            handleError("Failed to load producer IDs", e);
        }
    }

    private void handleError(String message, Exception ex) {
        System.err.println(message + ": " + ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            message + "\nError: " + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void searchProducer() {
        try {
            String selectedProducerId = (String) cbProducerId.getSelectedItem();

            if (selectedProducerId == null || selectedProducerId.equals("Select Producer")) {
                JOptionPane.showMessageDialog(this, "Please select a Producer ID", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producer producer = ProducerLogic.getInstance().searchProducer(selectedProducerId);

            if (producer != null) {
                tfName.setText(producer.getName());
                tfContactPhone.setText(producer.getContactPhone());
                tfAddress.setText(producer.getAddress());
                tfEmail.setText(producer.getEmail());

                showWinesForProducer(selectedProducerId);
            } else {
                JOptionPane.showMessageDialog(this, "Producer not found in the database", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        } catch (Exception e) {
            handleError("Error searching producer", e);
        }
    }
    private void editProducer() {
        try {
            String producerId = (String) cbProducerId.getSelectedItem();
            String name = tfName.getText().trim();
            String phone = tfContactPhone.getText().trim();
            String address = tfAddress.getText().trim();
            String email = tfEmail.getText().trim();

            // ✅ 1. Validate Producer Selection
            if (producerId == null || producerId.equals("Select Producer")) {
                showError("Please select a valid producer to edit.");
                return;
            }

            

            // ✅ 3. Ensure Producer ID Exists in Database
            if (!ProducerLogic.getInstance().producerExists(producerId)) {
                showError("This Producer ID does not exist. Please select an existing producer.");
                return;
            }

            // ✅ 4. Validate Name Format
            if (!name.matches("^[a-zA-Z\\s]+$") || name.length() < 2) {
                showError("Name must contain only English letters and be at least 2 characters long.");
                return;
            }

            // ✅ 5. Validate Phone Number Format (Israeli Format)
            if (!phone.matches("^05\\d{8}$")) {
                showError("Phone number must be in Israeli format (e.g., 0501234567).");
                return;
            }

            // ✅ 6. Validate Address Length
            if (address.length() < 5) {
                showError("Address must be at least 5 characters long.");
                return;
            }

            // ✅ 7. Validate Email Format
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showError("Invalid email format.");
                return;
            }

            // ✅ 8. Create and Save Updated Producer
            Producer updatedProducer = new Producer(producerId, name, phone, address, email);
            boolean success = ProducerLogic.getInstance().editProducer(updatedProducer);

            if (success) {
                JOptionPane.showMessageDialog(this, "Producer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducerIds(); // ✅ Refresh producer list
            } else {
                showError("Failed to update producer!");
            }
        } catch (Exception e) {
            showError("Error updating producer: " + e.getMessage());
            e.printStackTrace();
        }
    }
 // ✅ Helper Method to Show Errors
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }


    private void deleteProducer() {
        try {
            String producerId = (String) cbProducerId.getSelectedItem();

            if (producerId == null || producerId.equals("Select Producer")) {
                JOptionPane.showMessageDialog(this, "Please select a Producer ID!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this producer?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = ProducerLogic.getInstance().deleteProducer(producerId);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Producer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    loadProducerIds(); // Refresh list
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete producer!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            handleError("Error deleting producer", e);
        }
    }

    private void showWinesForProducer(String producerId) {
        wineTableModel.setRowCount(0);
        for (Wine wine : ProducerLogic.getInstance().getWinesForProducer(producerId)) {
            wineTableModel.addRow(new Object[]{
                wine.getCatalogNum(),
                wine.getName(),
                wine.getProductionYear(),
                wine.getPricePerBottle(),
                wine.getSweetnessLevel(),
                wine.getDescription(),
                wine.getProductImage(),
                wine.getWineTypeId()
            });
        }
    }

    private void clearFields() {
        cbProducerId.setSelectedIndex(0); // ✅ Reset dropdown without reloading
        tfName.setText("");
        tfContactPhone.setText("");
        tfAddress.setText("");
        tfEmail.setText("");
        wineTableModel.setRowCount(0); // ✅ Clear the table without refreshing the list
    }


    private static class RoundedComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼");
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFont(new Font("Proxima Nova", Font.BOLD, 12));
            button.setForeground(new Color(255, 92, 92));
            return button;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = c.getWidth();
            int height = c.getHeight();
            int arc = 20;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, width, height, arc, arc);

            g2d.setColor(new Color(255, 92, 92));
            g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

            super.paint(g, c);
        }
    }
    public void refreshProducerIds() {
        cbProducerId.removeAllItems(); // ✅ Clear existing items
        cbProducerId.addItem("Select Producer"); // ✅ Add default selection

        ArrayList<Producer> producers = ProducerLogic.getInstance().getAllProducers();
        for (Producer producer : producers) {
            cbProducerId.addItem(producer.getId()); // ✅ Add new producer IDs
        }
    }

    class BackgroundPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg")).getImage();

        public BackgroundPanel() {
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrameProducer().setVisible(true));
    }
}

