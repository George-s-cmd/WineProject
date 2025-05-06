package control;

import entity.StockedWine;
import entity.Consts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

public class WineInventoryDAO {
    private static WineInventoryDAO _instance;

    private WineInventoryDAO() {
    }

    public static WineInventoryDAO getInstance() {
        if (_instance == null) {
            _instance = new WineInventoryDAO();
        }
        return _instance;
    }

    /**
     * Retrieves the current inventory of wines from the database.
     * 
     * @return A list of StockedWine objects representing inventory data.
     */
    public List<StockedWine> getCurrentInventory() {
        List<StockedWine> inventoryList = new ArrayList<>();

        String query = "SELECT ws.catalogNumber, ws.producerId, ws.storageLocationId, ws.quantity " +
                       "FROM WineStorage ws";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inventoryList.add(new StockedWine(
                        rs.getString("catalogNumber"),
                        rs.getString("producerId"),
                        rs.getString("storageLocationId"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventoryList;
    }
    public boolean exportInventoryToJSON() {
        try {
            // 1. Load UCanAccess driver
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // 2. Connect to database & fetch inventory
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT catalogNumber, producerId, storageLocationId, quantity FROM WineStorage");
                 ResultSet rs = stmt.executeQuery()) {

                // 3. Create a JSON array for the entire inventory
                JsonArray inventoryArray = new JsonArray();

                // 4. Iterate over each row in the ResultSet
                while (rs.next()) {
                    JsonObject item = new JsonObject();

                    // For each column in the row, add it to the JSON object
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        String columnValue = rs.getString(i);
                        item.put(columnName, columnValue);
                    }

                    // Add the item to the inventory array
                    inventoryArray.add(item);
                }

                // 5. Wrap array into a root JSON object
                JsonObject doc = new JsonObject();
                doc.put("CurrentInventory", inventoryArray);

                // 6. Prepare the output file path
                File file = new File("json/inventory.json");
                file.getParentFile().mkdirs(); // Ensure the folder exists

                // 7. Write JSON to the file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(Jsoner.prettyPrint(doc.toJson()));
                    writer.flush();
                    System.out.println("✅ Inventory data exported successfully to JSON!");
                    return true;  // ✅ Success
                } catch (IOException e) {
                    e.printStackTrace();
                    return false; // ❌ Failed to write file
                }

            } catch (SQLException | NullPointerException e) {
                e.printStackTrace();
                return false; // ❌ Failed SQL or NullPointer issue
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false; // ❌ UCanAccess Driver not found
        }
    }

    
    public static void main(String[] args) {
        try {
            System.out.println("Starting Inventory Export Test...");

            // Call the export method via the singleton instance
            WineInventoryDAO.getInstance().exportInventoryToJSON();

            System.out.println("Inventory export completed successfully.");
        } catch (Exception e) {
            System.err.println("An error occurred while exporting inventory to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
