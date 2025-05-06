package control;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Consts;
import entity.Customer;
import entity.Order;
import entity.RegularOrder;
import entity.UrgentOrder;

public class OrderLogic {
    private static OrderLogic _instance;

    private OrderLogic() {}

    public static OrderLogic getInstance() {
        if (_instance == null) {
            _instance = new OrderLogic();
        }
        return _instance;
    }

    public List<UrgentOrder> getAllUrgentOrders() {
        List<UrgentOrder> orders = new ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_URGENT_ORDERS);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    orders.add(new UrgentOrder(
                        rs.getString("orderNumber"),               // Order Number
                        rs.getDate("orderDate"),         // Inherited from Order
                        rs.getString("orderStatus"),     // Inherited from Order
                        rs.getDate("shipmentDate"),      // Inherited from Order
                        rs.getString("employeesId"),     // Inherited from Order
                        rs.getInt("priority"),           // UrgentOrder-specific
                        rs.getDate("expectedDelivery"),  // UrgentOrder-specific
                        rs.getString("customerId")       // UrgentOrder-specific
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public UrgentOrder getUrgentOrderByNumber(String orderNumber) {
        String query = Consts.SQL_SELECT_URGENT_ORDER_BY_ID;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UrgentOrder(
                        rs.getString("id"),
                        rs.getDate("orderDate"),
                        rs.getString("orderStatus"),
                        rs.getDate("shipmentDate"),
                        rs.getString("employeesId"),
                        rs.getInt("priority"),
                        rs.getDate("expectedDelivery"),
                        rs.getString("customerId")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean addUrgentOrder(UrgentOrder order) {
        String insertOrderQuery = "INSERT INTO OrdersTable (orderNumber, orderDate, orderStatus, shipmentDate, employeesId) VALUES (?, ?, ?, ?, ?)";
        String insertUrgentOrderQuery = "INSERT INTO UrgentOrderTable (id, priority, expectedDelivery, customerId) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR)) {
            // Disable auto-commit for transaction
            conn.setAutoCommit(false);

            try (
                PreparedStatement stmtOrder = conn.prepareStatement(insertOrderQuery);
                PreparedStatement stmtUrgentOrder = conn.prepareStatement(insertUrgentOrderQuery)
            ) {
                // **Step 1: Insert into OrdersTable**
                stmtOrder.setString(1, order.getOrderNumber());
                stmtOrder.setDate(2, order.getOrderDate());
                stmtOrder.setString(3, order.getOrderStatus());
                stmtOrder.setDate(4, order.getShipmentDate());
                stmtOrder.setString(5, order.getEmployeeId());
                stmtOrder.executeUpdate();

                // **Step 2: Insert into UrgentOrderTable**
                stmtUrgentOrder.setString(1, order.getOrderNumber()); // id matches orderNumber
                stmtUrgentOrder.setInt(2, order.getPriority());
                stmtUrgentOrder.setDate(3, order.getExpectedDelivery());
                stmtUrgentOrder.setString(4, order.getCustomerId());
                stmtUrgentOrder.executeUpdate();

                // **Commit transaction**
                conn.commit();
                return true;

            } catch (SQLException e) {
                // **Rollback transaction if any error occurs**
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                // **Enable auto-commit back**
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean updateUrgentOrder(UrgentOrder order) {
        String updateOrdersTableSQL = "UPDATE OrdersTable SET orderDate = ?, orderStatus = ?, shipmentDate = ?, employeesId = ? WHERE orderNumber = ?";
        String updateUrgentOrderTableSQL = "UPDATE UrgentOrderTable SET priority = ?, expectedDelivery = ?, customerId = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt1 = conn.prepareStatement(updateOrdersTableSQL);
             PreparedStatement stmt2 = conn.prepareStatement(updateUrgentOrderTableSQL)) {

            conn.setAutoCommit(false); // ✅ Start transaction

            // ✅ First UPDATE: OrdersTable
            stmt1.setDate(1, order.getOrderDate());
            stmt1.setString(2, order.getOrderStatus());
            stmt1.setDate(3, order.getShipmentDate());
            stmt1.setString(4, order.getEmployeeId());
            stmt1.setString(5, order.getOrderNumber());

            int rowsUpdated1 = stmt1.executeUpdate();

            // ✅ Second UPDATE: UrgentOrderTable
            stmt2.setInt(1, order.getPriority());
            stmt2.setDate(2, order.getExpectedDelivery());
            stmt2.setString(3, order.getCustomerId());
            stmt2.setString(4, order.getOrderNumber()); // Ensure this matches the actual column

            int rowsUpdated2 = stmt2.executeUpdate();

            conn.commit(); // ✅ Commit transaction if both succeed

            return rowsUpdated1 > 0 && rowsUpdated2 > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteUrgentOrder(String orderNumber) {
        String deleteUrgentOrderQuery = "DELETE FROM UrgentOrderTable WHERE id = ?";
        String deleteOrdersQuery = "DELETE FROM OrdersTable WHERE orderNumber = ?";
        
        Connection conn = null; // ✅ Declare connection outside try

        try {
            conn = DriverManager.getConnection(Consts.CONN_STR);
            conn.setAutoCommit(false); // ✅ Start transaction

            try (PreparedStatement stmtUrgentOrder = conn.prepareStatement(deleteUrgentOrderQuery);
                 PreparedStatement stmtOrders = conn.prepareStatement(deleteOrdersQuery)) {

                // Delete from UrgentOrderTable
                stmtUrgentOrder.setString(1, orderNumber);
                int urgentDeleted = stmtUrgentOrder.executeUpdate();

                // Delete from OrdersTable
                stmtOrders.setString(1, orderNumber);
                int ordersDeleted = stmtOrders.executeUpdate();

                conn.commit(); // ✅ Commit transaction if both deletions succeed
                return urgentDeleted > 0 && ordersDeleted > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) { // ✅ Ensure conn is not null before calling rollback
                try {
                    conn.rollback(); // ✅ Rollback on failure
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) { // ✅ Close connection safely
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }


  
    public ArrayList<Customer> getCustomersForOrder(String orderNumber) {
        ArrayList<Customer> customers = new ArrayList<>();
        String query = "SELECT c.id, p.name, p.phoneNumber, p.email, c.dayOfFirstContact, c.deliveryAddress FROM CustomersTable c " +
                       "INNER JOIN PersonsTable p ON c.id = p.personId " +
                       "INNER JOIN UrgentOrderTable uo ON c.id = uo.customerId " +
                       "INNER JOIN OrdersTable o ON uo.id = o.orderNumber " +
                       "WHERE o.orderNumber = ?";
        
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public ArrayList<RegularOrder> getAllRegularOrders() {
        ArrayList<RegularOrder> regularOrders = new ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_GET_ALL_REGULAR_ORDERS);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String orderNumber = rs.getString("orderNumber");
                    Date orderDate = rs.getDate("orderDate");
                    String orderStatus = rs.getString("orderStatus");
                    Date shipmentDate = rs.getDate("shipmentDate");
                    String employeeId = rs.getString("employeesId");
                    String mainCustomerId = rs.getString("mainCustomerId"); // ✅ Fetching from RegularOrderTable

                    // ✅ Ensure non-null values before creating the object
                    if (orderNumber != null && mainCustomerId != null) {
                        regularOrders.add(new RegularOrder(
                            orderNumber, orderDate, orderStatus, shipmentDate, employeeId, mainCustomerId
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return regularOrders;
    }


    public RegularOrder getRegularOrderByNumber(String orderNumber) {
        String query = Consts.SQL_SELECT_REGULAR_ORDER_BY_ID;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Customer mainCustomer = new Customer(
                        rs.getString("mainCustomerId"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        rs.getDate("dayOfFirstContact"),
                        rs.getString("deliveryAddress")
                );

                return new RegularOrder(
                        rs.getString("id"),
                        rs.getDate("orderDate"),
                        rs.getString("orderStatus"),
                        rs.getDate("shipmentDate"),
                        rs.getString("employeesId"),
                        mainCustomer
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addRegularOrder(RegularOrder order) {
        String orderQuery = "INSERT INTO OrdersTable (orderNumber, orderDate, orderStatus, shipmentDate, employeesId) VALUES (?, ?, ?, ?, ?)";
        String regularOrderQuery = "INSERT INTO RegularOrderTable (id, mainCustomerId) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR)) {
            // First, insert into OrdersTable
            try (PreparedStatement stmtOrder = conn.prepareStatement(orderQuery)) {
                stmtOrder.setString(1, order.getOrderNumber());
                stmtOrder.setDate(2, order.getOrderDate());
                stmtOrder.setString(3, order.getOrderStatus());
                stmtOrder.setDate(4, order.getShipmentDate());
                stmtOrder.setString(5, order.getEmployeeId());

                stmtOrder.executeUpdate();  // Execute first insert
            }

            // Now, insert into RegularOrderTable
            try (PreparedStatement stmtRegularOrder = conn.prepareStatement(regularOrderQuery)) {
                stmtRegularOrder.setString(1, order.getOrderNumber()); // The orderNumber is the ID in RegularOrderTable
                stmtRegularOrder.setString(2, order.getCustomerId());

                stmtRegularOrder.executeUpdate();  // Execute second insert
            }

            return true; // If both inserts succeed, return true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateRegularOrder(RegularOrder order) {
        String updateOrdersTableSQL = "UPDATE OrdersTable SET orderDate = ?, orderStatus = ?, shipmentDate = ?, employeesId = ? WHERE orderNumber = ?";
        String updateRegularOrderTableSQL = "UPDATE RegularOrderTable SET mainCustomerId = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement checkCustomerStmt = conn.prepareStatement("SELECT 1 FROM CustomersTable WHERE id = ?");
             PreparedStatement stmt1 = conn.prepareStatement(updateOrdersTableSQL);
             PreparedStatement stmt2 = conn.prepareStatement(updateRegularOrderTableSQL)) {

            conn.setAutoCommit(false); // ✅ Start transaction

            // ✅ Check if the customer exists before updating
            checkCustomerStmt.setString(1, order.getCustomerId());
            ResultSet rs = checkCustomerStmt.executeQuery();
            if (!rs.next()) {
                System.err.println("Error: Customer ID does not exist in CustomersTable.");
                return false; // Prevent update if customer does not exist
            }

            // ✅ First Update Statement: OrdersTable
            stmt1.setDate(1, order.getOrderDate());
            stmt1.setString(2, order.getOrderStatus());
            stmt1.setDate(3, order.getShipmentDate());
            stmt1.setString(4, order.getEmployeeId());
            stmt1.setString(5, order.getOrderNumber());

            int rowsUpdated1 = stmt1.executeUpdate();

            // ✅ Second Update Statement: RegularOrderTable
            stmt2.setString(1, order.getCustomerId());
            stmt2.setString(2, order.getOrderNumber());

            int rowsUpdated2 = stmt2.executeUpdate();

            conn.commit(); // ✅ Commit transaction if both succeed
            return rowsUpdated1 > 0 && rowsUpdated2 > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRegularOrder(String orderNumber) {
        String deleteRegularOrderSQL = "DELETE FROM RegularOrderTable WHERE id = ?";
        String deleteOrdersSQL = "DELETE FROM OrdersTable WHERE orderNumber = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt1 = conn.prepareStatement(deleteRegularOrderSQL);
             PreparedStatement pstmt2 = conn.prepareStatement(deleteOrdersSQL)) {

            conn.setAutoCommit(false); // ✅ Start transaction

            // ✅ First delete from RegularOrderTable
            pstmt1.setString(1, orderNumber);
            pstmt1.executeUpdate();

            // ✅ Then delete from OrdersTable
            pstmt2.setString(1, orderNumber);
            pstmt2.executeUpdate();

            conn.commit(); // ✅ Commit transaction if both deletes succeed
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        String query = Consts.GET_ALL_CUSTOMERS;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer getCustomerById(String customerId) {
        String query = Consts.SQL_SELECT_CUSTOMER_BY_ID;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        rs.getDate("dayOfFirstContact"),
                        rs.getString("deliveryAddress")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<Order> getOrdersForCustomer(String customerId) {
        ArrayList<Order> orders = new ArrayList<>();

        String regularOrderQuery = "SELECT o.orderNumber, o.orderDate, o.orderStatus, o.shipmentDate, o.employeesId " +
                                   "FROM OrdersTable o " +
                                   "JOIN RegularOrderTable r ON o.orderNumber = r.id " +
                                   "WHERE r.mainCustomerId = ?";

        String urgentOrderQuery = "SELECT o.orderNumber, o.orderDate, o.orderStatus, o.shipmentDate, o.employeesId, " +
                                  "u.priority, u.expectedDelivery " +
                                  "FROM OrdersTable o " +
                                  "JOIN UrgentOrderTable u ON o.orderNumber = u.id " +
                                  "WHERE u.customerId = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR)) {
            
            // Fetch Regular Orders
            try (PreparedStatement stmt = conn.prepareStatement(regularOrderQuery)) {
                stmt.setString(1, customerId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    orders.add(new RegularOrder(
                        rs.getString("orderNumber"),
                        rs.getDate("orderDate"),
                        rs.getString("orderStatus"),
                        rs.getDate("shipmentDate"),
                        rs.getString("employeesId"), // ✅ FIXED column alias
                        customerId
                    ));
                }
            }

            // Fetch Urgent Orders
            try (PreparedStatement stmt = conn.prepareStatement(urgentOrderQuery)) {
                stmt.setString(1, customerId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    orders.add(new UrgentOrder(
                        rs.getString("orderNumber"),
                        rs.getDate("orderDate"),
                        rs.getString("orderStatus"),
                        rs.getDate("shipmentDate"),
                        rs.getString("employeesId"), // ✅ FIXED column alias
                        rs.getInt("priority"),
                        rs.getDate("expectedDelivery"),
                        customerId
                    ));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }
    public ArrayList<String> getOrderNumbersForCustomer(String customerId) {
        ArrayList<String> orderNumbers = new ArrayList<>();

        String query = "SELECT o.orderNumber FROM OrdersTable o "
                     + "LEFT JOIN RegularOrderTable r ON o.orderNumber = r.id "
                     + "LEFT JOIN UrgentOrderTable u ON o.orderNumber = u.id "
                     + "WHERE r.mainCustomerId = ? OR u.customerId = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            stmt.setString(2, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orderNumbers.add(rs.getString("orderNumber"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderNumbers;
    }
    public boolean addRegularOrderCustomer(String regularOrderId, String customerId) {
        String query = "INSERT INTO RegularOrderCustomer (regularOrderId, customerId) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, regularOrderId);
            stmt.setString(2, customerId);
            
            return stmt.executeUpdate() > 0; // ✅ Returns true if insertion is successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean regularOrderExists(String orderNumber) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_REGULAR_ORDER_EXISTS)) {

            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }
    public boolean urgentOrderExists(String orderNumber) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_URGENT_ORDER_EXISTS)) {

            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }








}
   

