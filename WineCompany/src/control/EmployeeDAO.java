package control;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.Employee;
import entity.Order;
import entity.Consts;

public class EmployeeDAO {

    private static EmployeeDAO _instance;

    private EmployeeDAO() {}

    public static EmployeeDAO getInstance() {
        if (_instance == null) {
            _instance = new EmployeeDAO();
        }
        return _instance;
    }

    /**
     * Retrieves all employees from the database.
     * @return A list of all employees.
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); // ✅ Ensure driver is loaded

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_GET_ALL_EMPLOYEES);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    employees.add(new Employee(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        rs.getString("officeAddress"),
                        rs.getDate("employmentStartDate")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Ensure error logging
        }

        return employees;
    }


    /**
     * Retrieves a single employee by ID.
     * @param employeeId The ID of the employee.
     * @return Employee object or null if not found.
     */
    public Employee getEmployeeById(String employeeId) {
        String query = Consts.SQL_GET_EMPLOYEE_BY_ID;
        Employee employee = null;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                employee = new Employee(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("phoneNumber"),
                    rs.getString("email"),
                    rs.getString("officeAddress"),
                    rs.getDate("employmentStartDate")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employee;
    }

    /**
     * Adds a new employee to the database.
     * @param employee The Employee object to be added.
     * @return true if successful, false otherwise.
     */
    public boolean addPerson(Employee employee) {
        String query = "INSERT INTO PersonsTable (personId, name, phoneNumber, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getPersonId());
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getPhoneNumber());
            stmt.setString(4, employee.getEmail());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addEmployee(Employee employee) {
        String query = "INSERT INTO EmployeesTable (id, officeAddress, employmentStartDate) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getPersonId()); // Use ID from Person
            stmt.setString(2, employee.getOfficeAddress());
            stmt.setDate(3, employee.getEmploymentStartDate());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing employee's details.
     * @param employee The Employee object with updated details.
     * @return true if successful, false otherwise.
     */
    public boolean updateEmployee(Employee employee) {
        String updatePersonQuery = "UPDATE PersonsTable SET name = ?, phoneNumber = ?, email = ? WHERE personId = ?";
        String updateEmployeeQuery = "UPDATE EmployeesTable SET officeAddress = ?, employmentStartDate = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmtPerson = conn.prepareStatement(updatePersonQuery);
             PreparedStatement stmtEmployee = conn.prepareStatement(updateEmployeeQuery)) {

            // ✅ Update PersonsTable
            stmtPerson.setString(1, employee.getName());
            stmtPerson.setString(2, employee.getPhoneNumber());
            stmtPerson.setString(3, employee.getEmail());
            stmtPerson.setString(4, employee.getPersonId());
            stmtPerson.executeUpdate();

            // ✅ Update EmployeesTable
            stmtEmployee.setString(1, employee.getOfficeAddress());
            stmtEmployee.setDate(2, employee.getEmploymentStartDate());
            stmtEmployee.setString(3, employee.getPersonId());
            stmtEmployee.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an employee from the database.
     * @param employeeId The ID of the employee to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteEmployee(String employeeId) {
        String deleteEmployeeQuery = "DELETE FROM EmployeesTable WHERE id = ?";
        String deletePersonQuery = "DELETE FROM PersonsTable WHERE personId = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmtEmployee = conn.prepareStatement(deleteEmployeeQuery);
             PreparedStatement stmtPerson = conn.prepareStatement(deletePersonQuery)) {

            // ✅ Delete from EmployeesTable first
            stmtEmployee.setString(1, employeeId);
            stmtEmployee.executeUpdate();

            // ✅ Then delete from PersonsTable
            stmtPerson.setString(1, employeeId);
            stmtPerson.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Order> getOrdersForEmployee(String employeeId) {
        ArrayList<Order> orders = new ArrayList<>();
        String query = "SELECT orderNumber, orderDate, orderStatus, shipmentDate FROM OrdersTable WHERE employeesId = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                    rs.getString("orderNumber"),
                    rs.getDate("orderDate"),
                    rs.getString("orderStatus"),
                    rs.getDate("shipmentDate"),
                    employeeId
                );
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
    public ArrayList<String> getOrderNumbersForEmployee(String employeeId) {
        ArrayList<String> orderNumbers = new ArrayList<>();
        String query = "SELECT orderNumber FROM OrdersTable WHERE employeesId = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orderNumbers.add(rs.getString("orderNumber"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderNumbers;
    }
    public List<Employee> getUnproductiveEmployees(String startDate, String endDate) {
        List<Employee> unproductiveEmployees = new ArrayList<>();
        String query = """
            SELECT e.id, p.name, p.phoneNumber, p.email, e.officeAddress, e.employmentStartDate
            FROM EmployeesTable e
            JOIN PersonsTable p ON e.id = p.personId
            LEFT JOIN OrdersTable o ON e.id = o.employeeId AND o.orderDate BETWEEN ? AND ?
            LEFT JOIN RegularOrderTable r ON o.orderNumber = r.id
            LEFT JOIN UrgentOrderTable u ON o.orderNumber = u.id
            GROUP BY e.id, p.name, p.phoneNumber, p.email, e.officeAddress, e.employmentStartDate
            HAVING COUNT(DISTINCT u.id) < 2 OR COUNT(DISTINCT r.id) < 4;
        """;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        rs.getString("officeAddress"),
                        rs.getDate("employmentStartDate")
                );
                unproductiveEmployees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unproductiveEmployees;
    }
    public boolean employeeExists(String employeeId) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_EMPLOYEE_EXISTS)) {

            stmt.setString(1, employeeId); // Set the employee ID as a parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0; // If count > 0, the employee exists
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }


}
