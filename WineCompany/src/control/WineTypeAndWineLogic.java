package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Consts;
import entity.Wine;

public class WineTypeAndWineLogic {
	
	
	    private static  WineTypeAndWineLogic _instance;

	    // Private constructor to implement Singleton Pattern
	    private WineTypeAndWineLogic() {
	    }

	    // Singleton instance method
	    public static WineTypeAndWineLogic getInstance() {
	        if (_instance == null)
	            _instance = new  WineTypeAndWineLogic();
	        return _instance;
	    }
	
	public HashMap<String, ArrayList<Wine>> getWineByWineTypeId() {
	    HashMap<String, ArrayList<Wine>> wineByWineTypeId = new HashMap<>();

	    // SQL query to fetch wines by wine type
	    String query = Consts.GET_WINES_BY_WINE_TYPE;

	    try {
	        // Load UCanAccess JDBC driver
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

	        // Establish connection to the database
	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement pstmt = conn.prepareStatement(query)) {

	            // Fetch unique wine type IDs dynamically
	            Statement stmt = conn.createStatement();
	            ResultSet wineTypeResult = stmt.executeQuery("SELECT serialNumber FROM WineTypeTable");

	            // Iterate through wine type IDs
	            while (wineTypeResult.next()) {
	                String wineTypeId = wineTypeResult.getString("serialNumber");

	                // Set the wineTypeId parameter in the query
	                pstmt.setString(1, wineTypeId);

	                // Execute the query and process the result set
	                try (ResultSet rs = pstmt.executeQuery()) {
	                    ArrayList<Wine> wines = new ArrayList<>();

	                    while (rs.next()) {
	                        // Create Wine object for each row
	                        Wine wine = new Wine(
	                                rs.getString("catalogNumber"),    // Catalog Number
	                                rs.getString("producerId"),    // Producer ID
	                                rs.getString("WineName"),      // Wine Name
	                                rs.getInt("productionYear"),      // Production Year
	                                rs.getDouble("pricePerBottle"),   // Price per Bottle
	                                rs.getString("sweetnessLevel"),    // Sweetness Level
	                                rs.getString("description"),   // Description
	                                rs.getString("productImage"),  // Product Image
	                                wineTypeId                     // Wine Type ID
	                        );

	                        // Add the wine object to the list
	                        wines.add(wine);
	                    }

	                    // Add the list of wines to the HashMap if not empty
	                    if (!wines.isEmpty()) {
	                        wineByWineTypeId.put(wineTypeId, wines);
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return wineByWineTypeId;
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
	    

	}

