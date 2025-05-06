package control;

import entity.Customer;

import entity.Consts;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerLogic {

    private static CustomerLogic _instance;

    private CustomerLogic() {
    }

    public static CustomerLogic getInstance() {
        if (_instance == null) {
            _instance = new CustomerLogic();
        }
        return _instance;
    }

    /**
     * Retrieves all customers along with their associated orders from the database.
     *
     * @return A list of all customers.
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); // ✅ Ensure UCanAccess Driver is loaded

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement pstmt = conn.prepareStatement(Consts.SQL_GET_ALL_CUSTOMERS);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    customers.add(new Customer(
                        rs.getString("id"), 
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        rs.getDate("dayOfFirstContact"),
                        rs.getString("deliveryAddress")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }

    public boolean addCustomer(Customer customer) {
        Connection conn = null;
        PreparedStatement stmtPerson = null;
        PreparedStatement stmtCustomer = null;
        
        String queryPerson = "INSERT INTO PersonsTable (personId, name, phoneNumber, email) VALUES (?, ?, ?, ?)";
        String queryCustomer = "INSERT INTO CustomersTable (id, dayOfFirstContact, deliveryAddress) VALUES (?, ?, ?)";

        try {
            conn = DriverManager.getConnection(Consts.CONN_STR);
            conn.setAutoCommit(false); // Start transaction

            // Insert into PersonsTable
            stmtPerson = conn.prepareStatement(queryPerson);
            stmtPerson.setString(1, customer.getPersonId());
            stmtPerson.setString(2, customer.getName());
            stmtPerson.setString(3, customer.getPhoneNumber());
            stmtPerson.setString(4, customer.getEmail());
            stmtPerson.executeUpdate();

            // Insert into CustomersTable
            stmtCustomer = conn.prepareStatement(queryCustomer);
            stmtCustomer.setString(1, customer.getPersonId());
            stmtCustomer.setDate(2, customer.getDayOfFirstContact());
            stmtCustomer.setString(3, customer.getDeliveryAddress());
            stmtCustomer.executeUpdate();

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction if error occurs
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmtPerson != null) stmtPerson.close();
                if (stmtCustomer != null) stmtCustomer.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateCustomer(Customer customer) {
        String updatePersonsQuery = "UPDATE PersonsTable SET name=?, phoneNumber=?, email=? WHERE personId=?";
        String updateCustomersQuery = "UPDATE CustomersTable SET dayOfFirstContact=?, deliveryAddress=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmtPerson = conn.prepareStatement(updatePersonsQuery);
             PreparedStatement stmtCustomer = conn.prepareStatement(updateCustomersQuery)) {

            // Update PersonsTable
            stmtPerson.setString(1, customer.getName());
            stmtPerson.setString(2, customer.getPhoneNumber());
            stmtPerson.setString(3, customer.getEmail());
            stmtPerson.setString(4, customer.getPersonId());
            stmtPerson.executeUpdate();

            // Update CustomersTable
            stmtCustomer.setDate(1, customer.getDayOfFirstContact());
            stmtCustomer.setString(2, customer.getDeliveryAddress());
            stmtCustomer.setString(3, customer.getPersonId());

            return stmtCustomer.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteCustomer(String customerId) {
        String deleteCustomersQuery = "DELETE FROM CustomersTable WHERE id=?";
        String deletePersonsQuery = "DELETE FROM PersonsTable WHERE personId=?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmtCustomer = conn.prepareStatement(deleteCustomersQuery);
             PreparedStatement stmtPerson = conn.prepareStatement(deletePersonsQuery)) {

            // Delete from CustomersTable first
            stmtCustomer.setString(1, customerId);
            stmtCustomer.executeUpdate();

            // Delete from PersonsTable after
            stmtPerson.setString(1, customerId);
            return stmtPerson.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean customerExists(String customerId) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_CUSTOMER_EXISTS)) {

            stmt.setString(1, customerId); // Set the parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0; // If count > 0, the customer exists
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return exists;
    }



 
}
