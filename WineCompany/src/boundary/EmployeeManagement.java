package boundary;

import javax.swing.*;
import java.util.List;

import javax.swing.plaf.basic.BasicComboBoxUI;

import boundary.RoundedComboBoxUI;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import control.EmployeeDAO;
import entity.Employee;
import entity.Order;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class EmployeeManagement extends MainFrame {
	public static EmployeeManagement instance;

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbEmployeeId;
	private JTextField tfName, tfPhone, tfEmail, tfOfficeAddress, tfStartDate;
	private JTable employeeTable;
	private DefaultTableModel employeeTableModel;
	private JPanel employeePanel;

	public EmployeeManagement() {
		instance = this;
		initializeUI();
	}

	private void initializeUI() {
		try {
			System.out.println("Initializing EmployeeManagement...");

			// ✅ Set the Background Panel
			BackgroundPanel backgroundPanel = new BackgroundPanel();
			backgroundPanel.setLayout(new BorderLayout());
			setContentPane(backgroundPanel);

			// ✅ Create Sidebar Panel
			JPanel sidebarPanel = new JPanel();
			sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
			sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
			sidebarPanel.setBackground(new Color(255, 255, 255, 100));

			sidebarPanel.add(Box.createVerticalStrut(50)); // Push buttons lower

			String[] sidebarItems = { "Search Employee", "Add Employee", "Edit Employee", "Delete Employee",
					"Show All Employees", "Clear Fields","Manage Regular Orders","Manage Urgent Orders" };

			for (String item : sidebarItems) {
				JButton button = createSidebarButton(item);
				sidebarPanel.add(button);
				sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
			}
			sidebarPanel.add(Box.createVerticalGlue()); // Push buttons upwards to center them

			// ✅ Initialize Components
			JPanel contentPanel = new JPanel(new BorderLayout());
			contentPanel.setOpaque(false);
			initializeComponents();
			setupLayout(contentPanel);
			loadEmployeeIds();

			// ✅ Add Sidebar and Content Panel to Background
			backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
			backgroundPanel.add(contentPanel, BorderLayout.CENTER);

			setSize(1200, 700);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			System.out.println("EmployeeManagement initialization complete");

		} catch (Exception e) {
			handleError("Critical error during initialization", e);
		}
	}

	private JButton createSidebarButton(String text) {
		JButton button = new RoundedButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setForeground(Color.white);
		button.setBackground(new Color(255, 92, 92));
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		button.setPreferredSize(new Dimension(200, 40));
		button.setMaximumSize(new Dimension(200, 40));
		button.setMinimumSize(new Dimension(200, 40));
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setHorizontalAlignment(SwingConstants.CENTER);

		button.addActionListener(e -> {
			switch (text) {
			case "Search Employee":
				searchEmployee();
				break;
			case "Add Employee":
				SwingUtilities.invokeLater(() -> new AddEmployeeFrame().setVisible(true));
				break;
			case "Edit Employee":
				editEmployee();
				break;
			case "Delete Employee":
				deleteEmployee();
				break;
			case "Show All Employees":
				SwingUtilities.invokeLater(() -> new ShowAllEmployeesFrame().setVisible(true));
				break;
			case "Clear Fields":
				clearFields();
				break;
			case "Manage Regular Orders":
				SwingUtilities.invokeLater(() -> new RegularOrderManagement().setVisible(true));
				break;
			case "Manage Urgent Orders":
				SwingUtilities.invokeLater(() -> new UrgentOrderManagement().setVisible(true));
				break;
			default:
				JOptionPane.showMessageDialog(null, "Feature not implemented: " + text);
			}
		});

		return button;
	}

	private void initializeComponents() {
		// ✅ Create and Style ComboBox
		cbEmployeeId = createModernComboBox(new String[] {});

		// ✅ Create and Style TextFields
		tfName = createTextField();
		tfPhone = createTextField();
		tfEmail = createTextField();
		tfOfficeAddress = createTextField();
		tfStartDate = createTextField();

		// ✅ Define Table Columns
		String[] orderColumns = { "Order Number", "Order Date", "Order Status", "Shipment Date" };
		employeeTableModel = new DefaultTableModel(orderColumns, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// ✅ Create and Style Table with Forced Transparency
		employeeTable = new JTable(employeeTableModel);
		styleTable(employeeTable);

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

	private JTextField createTextField() {
		JTextField textField = new JTextField(15);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textField.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return textField;
	}

	private void setupLayout(JPanel mainPanel) {
		employeePanel = new JPanel(new GridBagLayout());
		employeePanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
		employeePanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;

		addInputField(employeePanel, gbc, "Employee ID:", cbEmployeeId, 0);
		addInputField(employeePanel, gbc, "Name:", tfName, 1);
		addInputField(employeePanel, gbc, "Phone:", tfPhone, 2);
		addInputField(employeePanel, gbc, "Email:", tfEmail, 3);
		addInputField(employeePanel, gbc, "Office Address:", tfOfficeAddress, 4);
		addInputField(employeePanel, gbc, "Start Date:", tfStartDate, 5);

		JScrollPane scrollPane = new JScrollPane(employeeTable);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		mainPanel.add(employeePanel, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
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

	private void loadOrdersForEmployee(String employeeId) {
		employeeTableModel.setRowCount(0); // ✅ Clear previous results before loading new ones

		ArrayList<Order> orders = EmployeeDAO.getInstance().getOrdersForEmployee(employeeId);

		for (Order order : orders) {
			employeeTableModel.addRow(new Object[] { order.getOrderNumber(), order.getOrderDate(),
					order.getOrderStatus(), order.getShipmentDate() });
		}
	}

	private void loadEmployeeIds() {
	    try {
	        cbEmployeeId.removeAllItems(); // ✅ Clear existing items first
	        cbEmployeeId.addItem("Select Employee"); // ✅ Add default selection

	        List<Employee> employees = EmployeeDAO.getInstance().getAllEmployees(); // ✅ Fetch all employee objects

	        for (Employee employee : employees) {
	            cbEmployeeId.addItem(employee.getPersonId()); // ✅ Add Employee ID to dropdown
	        }

	    } catch (Exception e) {
	        handleError("Failed to load Employee IDs", e);
	    }
	}

	private void searchEmployee() {
		try {
			String selectedEmployeeId = (String) cbEmployeeId.getSelectedItem();

			if (selectedEmployeeId == null || selectedEmployeeId.equals("Select Employee")) {
				JOptionPane.showMessageDialog(this, "Please select an Employee ID", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			Employee employee = EmployeeDAO.getInstance().getEmployeeById(selectedEmployeeId);

			if (employee != null) {
				tfName.setText(employee.getName());
				tfPhone.setText(employee.getPhoneNumber());
				tfEmail.setText(employee.getEmail());
				tfOfficeAddress.setText(employee.getOfficeAddress());
				tfStartDate.setText(employee.getEmploymentStartDate().toString());

				// 🔹 Load orders for this employee
				loadOrdersForEmployee(selectedEmployeeId);
			} else {
				JOptionPane.showMessageDialog(this, "Employee not found in the database", "Search Result",
						JOptionPane.INFORMATION_MESSAGE);
				clearFields();
			}
		} catch (Exception e) {
			handleError("Error searching employee", e);
		}
	}

	private void editEmployee() {
	    try {
	        String employeeId = (String) cbEmployeeId.getSelectedItem();
	        String name = tfName.getText().trim();
	        String phone = tfPhone.getText().trim();
	        String email = tfEmail.getText().trim();
	        String officeAddress = tfOfficeAddress.getText().trim();
	        String startDateText = tfStartDate.getText().trim();

	        // ✅ 1. Validate Employee Selection
	        if (employeeId == null || employeeId.equals("Select Employee")) {
	            showError("Please select a valid Employee ID.");
	            return;
	        }

	        // ✅ 2. Validate Name (Only Hebrew/English letters, at least 2 characters)
	        if (!name.matches("^[א-תa-zA-Z\\s]+$") || name.length() < 2) {
	            showError("Name must contain only Hebrew/English letters and be at least 2 characters long.");
	            return;
	        }

	        // ✅ 3. Validate Phone Number (Israeli Format: Starts with 05 and 10 digits)
	        if (!phone.matches("^05\\d{8}$")) {
	            showError("Phone number must be in Israeli format (e.g., 0501234567).");
	            return;
	        }

	        // ✅ 4. Validate Email
	        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
	            showError("Invalid email format.");
	            return;
	        }

	        // ✅ 5. Validate Office Address (Minimum 5 characters)
	        if (officeAddress.length() < 5) {
	            showError("Office address must be at least 5 characters long.");
	            return;
	        }

	        // ✅ 6. Validate Start Date (Valid format & not in the future)
	        java.sql.Date startDate;
	        try {
	            startDate = java.sql.Date.valueOf(startDateText);
	            if (startDate.after(Date.valueOf(LocalDate.now()))) {
	                showError("Start date cannot be in the future.");
	                return;
	            }
	        } catch (IllegalArgumentException e) {
	            showError("Invalid date format! Please use YYYY-MM-DD.");
	            return;
	        }

	        // ✅ Create Updated Employee Object
	        Employee updatedEmployee = new Employee(employeeId, name, phone, email, officeAddress, startDate);

	        // ✅ Update Employee in Database
	        boolean success = EmployeeDAO.getInstance().updateEmployee(updatedEmployee);

	        if (success) {
	            JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	            refreshEmployeeList(); // ✅ Refresh JComboBox after update
	        } else {
	            showError("Failed to update employee! Please check the details.");
	        }
	    } catch (Exception e) {
	        showError("Unexpected error: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	private void showError(String message) {
	    JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
	}


	private void deleteEmployee() {
		try {
			String employeeId = (String) cbEmployeeId.getSelectedItem();

			if (employeeId == null || employeeId.equals("Select Employee")) {
				JOptionPane.showMessageDialog(this, "Please select an Employee ID!", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?",
					"Confirm Deletion", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				// ✅ Delete employee from both tables
				boolean success = EmployeeDAO.getInstance().deleteEmployee(employeeId);

				if (success) {
					JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					clearFields();
					refreshEmployeeList(); // ✅ Refresh JComboBox after deletion
				} else {
					JOptionPane.showMessageDialog(this, "Failed to delete employee!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			handleError("Error deleting employee", e);
		}
	}

	private void clearFields() {
		cbEmployeeId.setSelectedIndex(0); // Reset dropdown
		tfName.setText("");
		tfPhone.setText("");
		tfEmail.setText("");
		tfOfficeAddress.setText("");
		tfStartDate.setText("");

		// Clear orders table
		employeeTableModel.setRowCount(0);

		// Reload employees to refresh everything
		refreshEmployeeList();
	}

	private void handleError(String message, Exception ex) {
		System.err.println(message + ": " + ex.getMessage());
		ex.printStackTrace();
		JOptionPane.showMessageDialog(this, message + "\nError: " + ex.getMessage(), "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void styleTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		table.getTableHeader().setBackground(new Color(255, 92, 92));
		table.getTableHeader().setForeground(Color.white);
		table.setOpaque(false);
		((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(true);
	}

	public void refreshEmployeeList() {
	    try {
	        cbEmployeeId.removeAllItems(); // ✅ Clear existing items first
	        cbEmployeeId.addItem("Select Employee"); // ✅ Add default selection

	        List<Employee> employees = EmployeeDAO.getInstance().getAllEmployees(); // ✅ Fetch all employee objects

	        for (Employee employee : employees) {
	            cbEmployeeId.addItem(employee.getPersonId()); // ✅ Load Employee IDs correctly
	        }

	    } catch (Exception e) {
	        handleError("Failed to load Employee IDs", e);
	    }
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

	class BackgroundPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Image backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg"))
				.getImage();

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
		SwingUtilities.invokeLater(() -> new EmployeeManagement().setVisible(true));
	}
}
