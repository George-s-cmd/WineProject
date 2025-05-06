package control;

import entity.Consts;
import entity.WineType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WineTypeLogic {
	 private static WineTypeLogic _instance;

	    public WineTypeLogic() {
	    }

	    public static WineTypeLogic getInstance() {
	        if (_instance == null) {
	            _instance = new WineTypeLogic();
	        }
	        return _instance;
	    }
	    public boolean addWineType(WineType wineType) {
	        String query = "INSERT INTO WineTypeTable (serialNumber, name) VALUES (?, ?)";

	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setString(1, wineType.getSerialNumber());
	            stmt.setString(2, wineType.getName());

	            return stmt.executeUpdate() > 0;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    public ArrayList<WineType> getAllWineTypes() {
	        ArrayList<WineType> wineTypes = new ArrayList<>();
	        String query = "SELECT * FROM WineTypeTable"; // SQL query to get all wine types

	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(query);
	             ResultSet rs = stmt.executeQuery()) {

	            while (rs.next()) {
	                WineType wineType = new WineType(
	                        rs.getString("serialNumber"),
	                        rs.getString("name")
	                );
	                wineTypes.add(wineType);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return wineTypes;
	    }
	    public WineType getWineTypeByNumber(String serialNumber) {
	        String query = "SELECT serialNumber, name FROM WineTypeTable WHERE serialNumber = ?";

	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setString(1, serialNumber);
	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                return new WineType(
	                        rs.getString("serialNumber"),
	                        rs.getString("name")
	                );
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    public List<WineType> getAllWineTypes1() {
	        List<WineType> wineTypes = new ArrayList<>();

	        try {
	            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

	            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_WINE_TYPES);
	                 ResultSet rs = stmt.executeQuery()) {

	                while (rs.next()) {
	                    wineTypes.add(new WineType(
	                        rs.getString("serialNumber"), 
	                        rs.getString("name")
	                    ));
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return wineTypes;
	    }


	    public boolean updateWineType(WineType wineType) {
	        String query = "UPDATE WineTypeTable SET name = ? WHERE serialNumber = ?";

	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setString(1, wineType.getName());
	            stmt.setString(2, wineType.getSerialNumber());

	            return stmt.executeUpdate() > 0;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    public boolean deleteWineType(String serialNumber) {
	        String query = "DELETE FROM WineTypeTable WHERE serialNumber = ?";

	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setString(1, serialNumber);
	            return stmt.executeUpdate() > 0;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    


    /**
     * Fetches all wine type IDs from the database.
     *
     * @return ArrayList of wine type IDs.
     */
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
    

    public boolean wineTypeExists(String serialNumber) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_WINE_TYPE_EXISTS)) {

            stmt.setString(1, serialNumber); // Set the parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0; // If count > 0, the wine type exists
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return exists;
    }


	
}
