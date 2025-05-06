package control;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.Consts;
import entity.FoodPairing;

public class FoodPairingsLogic {
    private static FoodPairingsLogic _instance;

    private FoodPairingsLogic() {}

    public static FoodPairingsLogic getInstance() {
        if (_instance == null) {
            _instance = new FoodPairingsLogic();
        }
        return _instance;
    }

    /**
     * Retrieves all distinct food names from the `foodPairingsTable` in the database.
     * @return A list of all distinct food names.
     */
    public ArrayList<String> getAllFoodNames() {
        ArrayList<String> foodNames = new ArrayList<>();
        String query = "SELECT DISTINCT dishName FROM foodPairingsTable";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                foodNames.add(rs.getString("dishName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foodNames;
    }
    public ArrayList<String> getAllFoodNames1() { 
        ArrayList<String> foodNames = new ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); // ✅ Ensure driver is loaded

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_GET_ALL_FOOD_NAMES);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    foodNames.add(rs.getString("dishName")); // ✅ Fetch food names from the result set
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Print any errors for debugging
        }

        return foodNames;
    }


    /**
     * Retrieves all food pairings from the database.
     * @return A list of FoodPairing objects.
     */
    public ArrayList<FoodPairing> getAllFoodPairings() {
        ArrayList<FoodPairing> foodPairings = new ArrayList<>();
        String query = "SELECT * FROM foodPairingsTable";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                foodPairings.add(new FoodPairing(
                        rs.getString("dishName"),
                        rs.getString("recipe1"),
                        rs.getString("recipe2"),
                        rs.getString("recipe3"),
                        rs.getString("recipe4"),
                        rs.getString("recipe5")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foodPairings;
    }
    public boolean updateFoodPairing(String dishName, String recipe1, String recipe2, String recipe3, String recipe4, String recipe5) {
        String query = "UPDATE foodPairingsTable SET recipe1=?, recipe2=?, recipe3=?, recipe4=?, recipe5=? WHERE dishName=?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, recipe1);
            stmt.setString(2, recipe2);
            stmt.setString(3, recipe3);
            stmt.setString(4, recipe4);
            stmt.setString(5, recipe5);
            stmt.setString(6, dishName);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

  

    /**
     * Retrieves a single food pairing by dish name.
     * @param dishName The dish name.
     * @return FoodPairing object or null if not found.
     */
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

    /**
     * Adds a new food pairing to the database.
     * @param foodPairing The FoodPairing object to add.
     * @return true if successful, false otherwise.
     */
    public boolean addFoodPairing(FoodPairing foodPairing) {
        String query = "INSERT INTO FoodPairingsTable (dishName, recipe1, recipe2, recipe3, recipe4, recipe5) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, foodPairing.getDishName());
            stmt.setString(2, foodPairing.getRecipe1());
            stmt.setString(3, foodPairing.getRecipe2());
            stmt.setString(4, foodPairing.getRecipe3());
            stmt.setString(5, foodPairing.getRecipe4());
            stmt.setString(6, foodPairing.getRecipe5());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Updates an existing food pairing in the database.
     * @param foodPairing The FoodPairing object with updated values.
     * @return true if successful, false otherwise.
     */
    public boolean updateFoodPairing(FoodPairing foodPairing) {
        String query = "UPDATE foodPairingsTable SET recipe1 = ?, recipe2 = ?, recipe3 = ?, recipe4 = ?, recipe5 = ? WHERE dishName = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, foodPairing.getRecipe1());
            stmt.setString(2, foodPairing.getRecipe2());
            stmt.setString(3, foodPairing.getRecipe3());
            stmt.setString(4, foodPairing.getRecipe4());
            stmt.setString(5, foodPairing.getRecipe5());
            stmt.setString(6, foodPairing.getDishName());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a food pairing from the database.
     * @param dishName The name of the dish to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteFoodPairing(String dishName) {
        String query = "DELETE FROM foodPairingsTable WHERE dishName = ?";

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dishName);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean foodPairingExists(String dishName) {
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_FOOD_PAIRING_EXISTS)) {

            stmt.setString(1, dishName); // Set the dish name as a parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0; // If count > 0, the dish already exists
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    

}
