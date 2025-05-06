package control;

import entity.StorageLocation;
import entity.Consts;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StorageLocationLogic {

    private static StorageLocationLogic _instance;

    private StorageLocationLogic() {
    }

    public static StorageLocationLogic getInstance() {
        if (_instance == null) {
            _instance = new StorageLocationLogic();
        }
        return _instance;
    }
    public List<StorageLocation> getAllStorageLocations1() {
        List<StorageLocation> storageLocations = new ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_GET_ALL_STORAGE_LOCATIONS);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    storageLocations.add(new StorageLocation(
                        rs.getString("storageLocationId"), 
                        rs.getString("name")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return storageLocations;
    }


    /**
     * Retrieves all storage locations from the database.
     * @return A list of all storage locations.
     */
    public ArrayList<StorageLocation> getAllStorageLocations() {
        ArrayList<StorageLocation> locations = new ArrayList<>();
        String query = "SELECT * FROM StorageLocationTable";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                locations.add(new StorageLocation(
                        rs.getString("storageLocationId"),
                        rs.getString("name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }

    /**
     * Retrieves a single storage location by ID.
     * @param storageNumber The ID of the storage location.
     * @return StorageLocation object or null if not found.
     */
    public StorageLocation getStorageLocationByNumber(String storageNumber) {
        String query = "SELECT * FROM StorageLocationTable WHERE storageLocationId = ?";
        StorageLocation storageLocation = null;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, storageNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                storageLocation = new StorageLocation(
                        rs.getString("storageLocationId"),
                        rs.getString("name")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storageLocation;
    }

    /**
     * Adds a new storage location to the database.
     * @param location The StorageLocation object to be added.
     * @return true if successful, false otherwise.
     */
    public boolean addStorageLocation(StorageLocation location) {
        String query = "INSERT INTO StorageLocationTable (storageLocationId, name) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, location.getStorageNumber());
            stmt.setString(2, location.getName());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing storage location's details.
     * @param location The StorageLocation object with updated details.
     * @return true if successful, false otherwise.
     */
    public boolean updateStorageLocation(StorageLocation location) {
        String query = "UPDATE StorageLocationTable SET name = ? WHERE storageLocationId = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, location.getName());
            stmt.setString(2, location.getStorageNumber());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a storage location from the database.
     * @param storageNumber The ID of the storage location to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteStorageLocation(String storageNumber) {
        String query = "DELETE FROM StorageLocationTable WHERE storageLocationId = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, storageNumber);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean storageLocationExistsById(String storageNumber) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_STORAGE_LOCATION_ID_EXISTS)) {

            stmt.setString(1, storageNumber);
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
