package control;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.Consts;

public class UnproductiveEmployeeReportLogic {
    private static UnproductiveEmployeeReportLogic instance; // Singleton instance

    private UnproductiveEmployeeReportLogic() {} // Private constructor

    public static UnproductiveEmployeeReportLogic getInstance() {
        if (instance == null) {
            instance = new UnproductiveEmployeeReportLogic();
        }
        return instance;
    }

    public ArrayList<Object[]> getUnproductiveEmployees(String startDate, String endDate) {
        ArrayList<Object[]> employeeData = new ArrayList<>();
        String query = """
            SELECT p.personId, p.name, p.phoneNumber, p.email, e.officeAddress, e.employmentStartDate, 
                   COUNT(o.orderNumber) AS totalOrders
            FROM PersonsTable p
            JOIN EmployeesTable e ON p.personId = e.id
            LEFT JOIN OrdersTable o ON e.id = o.employeesId AND o.orderDate BETWEEN ? AND ?
            GROUP BY p.personId, p.name, p.phoneNumber, p.email, e.officeAddress, e.employmentStartDate
            HAVING COUNT(o.orderNumber) = 0  -- Only fetch employees with 0 orders
        """;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employeeData.add(new Object[]{
                    rs.getString("personId"),
                    rs.getString("name"),
                    rs.getString("phoneNumber"),
                    rs.getString("email"),
                    rs.getString("officeAddress"),
                    rs.getDate("employmentStartDate"),
                    0, // Unproductive employees (0 orders)
                    "Unproductive" // Status
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employeeData;
    }

}
