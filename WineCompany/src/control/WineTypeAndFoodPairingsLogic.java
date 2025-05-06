package control;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import entity.Consts;
import entity.FoodPairing;

public class WineTypeAndFoodPairingsLogic {
	private static WineTypeAndFoodPairingsLogic _instance;

	private WineTypeAndFoodPairingsLogic() {
	}

	public static WineTypeAndFoodPairingsLogic getInstance() {
		if (_instance == null)
			_instance = new WineTypeAndFoodPairingsLogic();
		return _instance;
	}


	public HashMap<String, ArrayList<String>> getWineTypeByFoodDishName() {
	    HashMap<String, ArrayList<String>> wineTypeByDishName = new HashMap<>();
	    String query = Consts.GET_WINETYPE_BY_DISHNAME;

	    try {
	        // Load UCanAccess JDBC driver
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

	        // Establish connection to the database
	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement pstmt = conn.prepareStatement(query);
	             Statement stmt = conn.createStatement()) {

	            // Fetch distinct dish names from the wineTypeAndFoodPairingTable
	            ResultSet dishesResult = stmt.executeQuery("SELECT DISTINCT dishName FROM wineTypeAndFoodPairingTable");

	            // Iterate through the distinct dish names
	            while (dishesResult.next()) {
	                String dishName = dishesResult.getString("dishName");

	                // Set the dishName parameter in the prepared statement
	                pstmt.setString(1, dishName);

	                // Execute the query for the current dish name
	                try (ResultSet rs = pstmt.executeQuery()) {
	                    ArrayList<String> wineTypes = new ArrayList<>();

	                    while (rs.next()) {
	                        // Retrieve wine type serial numbers as strings
	                        String wineTypeSerialNumber = rs.getString("serialNumber");
	                        wineTypes.add(wineTypeSerialNumber);
	                    }

	                    // Add the dish name and corresponding wine type IDs to the HashMap
	                    if (!wineTypes.isEmpty()) {
	                        wineTypeByDishName.put(dishName, wineTypes);
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return wineTypeByDishName;
	}
	public boolean removeAllWineTypesForDish(String dishName) {
	    String query = "DELETE FROM wineTypeAndFoodPairingTable WHERE dishName = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement pstmt = conn.prepareStatement(query)) {

	        pstmt.setString(1, dishName);

	        return pstmt.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public boolean removeWineTypeForDish(String dishName, String wineSerialNumber) {
	    String query = "DELETE FROM wineTypeAndFoodPairingTable WHERE dishName = ? AND WineTypeSerialNumber = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement pstmt = conn.prepareStatement(query)) {

	        pstmt.setString(1, dishName);
	        pstmt.setString(2, wineSerialNumber);

	        return pstmt.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public boolean addWineTypeForDish(String dishName, String wineSerialNumber) {
	    String query = "INSERT INTO wineTypeAndFoodPairingTable (dishName, WineTypeSerialNumber) VALUES (?, ?)";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement pstmt = conn.prepareStatement(query)) {

	        pstmt.setString(1, dishName);
	        pstmt.setString(2, wineSerialNumber);

	        return pstmt.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
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
	   public FoodPairing getFoodPairingByName(String dishName) {
	        String query = "SELECT * FROM foodPairingsTable WHERE dishName = ?";
	        FoodPairing foodPairing = null;

	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setString(1, dishName);
	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                foodPairing = new FoodPairing(
	                        rs.getString("dishName"),
	                        rs.getString("recipe1"),
	                        rs.getString("recipe2"),
	                        rs.getString("recipe3"),
	                        rs.getString("recipe4"),
	                        rs.getString("recipe5")
	                );
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return foodPairing;
	    }




}