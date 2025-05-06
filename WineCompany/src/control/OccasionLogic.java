package control;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.Consts;
import entity.Occasion;

public class OccasionLogic {

    private static OccasionLogic _instance;

    private OccasionLogic() {
    }

    public static OccasionLogic getInstance() {
        if (_instance == null) {
            _instance = new OccasionLogic();
        }
        return _instance;
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
    public List<Occasion> getAllOccasions1() {
        List<Occasion> occasions = new ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); // ✅ Load the database driver

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_GET_ALL_OCCASIONS);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    occasions.add(new Occasion(
                        rs.getString("occasionName"),  // ✅ Fetching the correct column
                        rs.getString("description"),   // ✅ Added Description column
                        rs.getString("season"),        // ✅ Added Season column
                        rs.getString("location")       // ✅ Added Location column
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return occasions;
    }

    /**
     * Retrieves all occasions from the `OccasionTable` in the database.
     *
     * @return A list of all occasions.
     */
    public ArrayList<Occasion> getAllOccasions() {
        ArrayList<Occasion> occasions = new ArrayList<>();
        String query = Consts.GET_ALL_OCCASIONS; // SQL Query to get all occasions

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Occasion occasion = new Occasion(
                        rs.getString("occasionName"),
                        rs.getString("description"),
                        rs.getString("season"),
                        rs.getString("location")
                );
                occasions.add(occasion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return occasions;
    }

    /**
     * Retrieves a specific occasion by name from the `OccasionTable`.
     *
     * @param occasionName The name of the occasion.
     * @return Occasion object if found, otherwise null.
     */
    public Occasion getOccasionByName(String occasionName) {
        String query = Consts.GET_OCCASION_BY_NAME;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, occasionName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Occasion(
                        rs.getString("occasionName"),
                        rs.getString("description"),
                        rs.getString("season"),
                        rs.getString("location")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Adds a new occasion to the database.
     *
     * @param occasion The Occasion object to be added.
     * @return true if successful, false otherwise.
     */
    public boolean addOccasion(Occasion occasion) {
        String query = Consts.INSERT_OCCASION;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, occasion.getOccasionName());
            stmt.setString(2, occasion.getDescription());
            stmt.setString(3, occasion.getSeason());
            stmt.setString(4, occasion.getLocation());

            return stmt.executeUpdate() > 0; // Returns true if at least one row is affected
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing occasion's details.
     *
     * @param occasion The Occasion object with updated details.
     * @return true if successful, false otherwise.
     */
    public boolean updateOccasion(String occasionName, String newDescription, String newSeason, String newLocation) {
        String query = Consts.UPDATE_OCCASION;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newDescription);
            stmt.setString(2, newSeason);
            stmt.setString(3, newLocation);
            stmt.setString(4, occasionName); // WHERE condition: updating based on occasion name

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an occasion from the database.
     *
     * @param occasionName The name of the occasion to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteOccasion(String occasionName) {
        String query = Consts.DELETE_OCCASION;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, occasionName);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean occasionExists(String occasionName) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_OCCASION_EXISTS)) {

            stmt.setString(1, occasionName); // Set the occasion name
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0; // If count > 0, it exists
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }


}
