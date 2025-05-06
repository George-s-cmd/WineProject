package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Consts;

public class WineTypeAndOccasionLogic {
    private static WineTypeAndOccasionLogic _instance;

    private WineTypeAndOccasionLogic() {
    }

    public static WineTypeAndOccasionLogic getInstance() {
        if (_instance == null)
            _instance = new WineTypeAndOccasionLogic();
        return _instance;
    }

    public HashMap<String, ArrayList<String>> getWineTypeByOccasionName() {
        HashMap<String, ArrayList<String>> wineTypeByOccasionName = new HashMap<>();
        String query = Consts.GET_WINETYPE_BY_OCCASIONNAME;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 Statement stmt = conn.createStatement()) {

                ResultSet occasionResult = stmt.executeQuery("SELECT DISTINCT occasionName FROM occasionAndWineTypeTable");
                while (occasionResult.next()) {
                    String occasionName = occasionResult.getString("occasionName");
                    System.out.println("Processing occasion: " + occasionName); // Debug

                    pstmt.setString(1, occasionName);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        ArrayList<String> wineTypes = new ArrayList<>();

                        while (rs.next()) {
                            String wineTypeSerialNumber = rs.getString("serialNumber");
                            System.out.println("Occasion: " + occasionName + ", WineTypeID: " + wineTypeSerialNumber); // Debug
                            wineTypes.add(wineTypeSerialNumber);
                        }

                        if (!wineTypes.isEmpty()) {
                            wineTypeByOccasionName.put(occasionName, wineTypes);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wineTypeByOccasionName;
    }
    public boolean deleteWineTypeOccasionPairing(String wineTypeSerialNumber, String occasionName) {
        String query = "DELETE FROM occasionAndWineTypeTable WHERE wineTypeId = ? AND occasionName = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, wineTypeSerialNumber);
            pstmt.setString(2, occasionName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // ✅ Returns true if a row was deleted
        } catch (Exception e) {
            e.printStackTrace();
            return false; // ❌ Returns false if an error occurs
        }
    }
    public ArrayList<String> getAllWineTypeIds() {
        ArrayList<String> wineTypeIds = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.GET_ALL_WINE_TYPE_IDS)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                wineTypeIds.add(rs.getString("serialNumber"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return wineTypeIds;
    }
    public ArrayList<String> getAllOccasionNames() {
        ArrayList<String> occasionNames = new ArrayList<>();
        String query = Consts.GET_OCCASSION_NAMES; // Query to fetch distinct dish names

        try {
            // Load UCanAccess JDBC driver
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // Establish connection to the database
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                // Iterate through the result set and add each dish name to the list
                while (rs.next()) {
                    occasionNames.add(rs.getString("occasionName"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return occasionNames;
    }
    
}
