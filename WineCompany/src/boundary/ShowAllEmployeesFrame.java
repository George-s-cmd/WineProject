package boundary;

import javax.swing.*;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import control.EmployeeDAO;
import entity.Employee;
import java.awt.*;
import java.util.List;

public class ShowAllEmployeesFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable employeeTable;
    private DefaultTableModel employeeTableModel;

    public ShowAllEmployeesFrame() {
        setTitle("All Employees & Orders");
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
        JLabel titleLabel = new JLabel("All Employees ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 70));
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

        // Define Table Columns (Added "Associated Orders")
        String[] columns = {"Employee ID", "Name", "Phone", "Email", "Office Address", "Start Date", "Associated Orders"};
        employeeTableModel = new DefaultTableModel(columns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(employeeTableModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeTable.setRowHeight(30);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeeTable.getTableHeader().setBackground(new Color(255, 92, 92));
        employeeTable.getTableHeader().setForeground(Color.black);
        employeeTable.setOpaque(false);
        ((JComponent) employeeTable.getDefaultRenderer(Object.class)).setOpaque(true);

        // Adjust column widths
        TableColumnModel columnModel = employeeTable.getColumnModel();
        columnModel.getColumn(4).setPreferredWidth(200); // Office Address column wider
        columnModel.getColumn(6).setPreferredWidth(300); // Associated Orders wider

        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.getTableHeader().setResizingAllowed(true);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Load employee data
        loadEmployeesData();

        // "Back to Employee Management" button
        JButton backButton = new RoundedButton("Close");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 92, 92));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose(); 
          
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 255, 255, 200));
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(contentPanel);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void loadEmployeesData() {
        try {
            employeeTableModel.setRowCount(0); // ✅ Clear table before adding new data

            List<Employee> employees = EmployeeDAO.getInstance().getAllEmployees(); // ✅ Fetch all employees

            for (Employee employee : employees) {
                // ✅ Fetch orders for this employee
                List<String> orderNumbers = EmployeeDAO.getInstance().getOrderNumbersForEmployee(employee.getPersonId());

                // ✅ Convert order numbers list to a comma-separated string
                String orderList = orderNumbers.isEmpty() ? "No Orders" : String.join(", ", orderNumbers);

                // ✅ Add employee data to the table
                employeeTableModel.addRow(new Object[]{
                    employee.getPersonId(),
                    employee.getName(),
                    employee.getPhoneNumber(),
                    employee.getEmail(),
                    employee.getOfficeAddress(),
                    employee.getEmploymentStartDate(),
                    orderList // ✅ Display associated orders
                });
            }

            System.out.println("✅ Employee data loaded successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // ✅ Fixed BackgroundPanel with proper constructor
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
        SwingUtilities.invokeLater(() -> new ShowAllEmployeesFrame().setVisible(true));
    }
}
