package control;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entity.Consts;
import entity.RegularOrderCustomer;
import entity.RegularOrderWine;
import entity.StockedWine;
import entity.UrgentOrderWine;
import entity.Wine;
import entity.WineTypeAndFoodPairing;
import entity.WineTypeOccasion;

public class GeneralControl {
	private static GeneralControl _instance;

	public GeneralControl() {
	}

	public static GeneralControl getInstance() {
		if (_instance == null) {
			_instance = new GeneralControl();
		}
		return _instance;
	}

	public ArrayList<Wine> getWinesByDishesOccasionsAndWineTypes(ArrayList<String> dishNames,
			ArrayList<String> occasionNames, ArrayList<String> wineTypeIds) {

		ArrayList<Wine> wines = new ArrayList<>();

		// Get the instances of the logic classes
		WineTypeAndFoodPairingsLogic foodPairingLogic = WineTypeAndFoodPairingsLogic.getInstance();
		WineTypeAndOccasionLogic occasionLogic = WineTypeAndOccasionLogic.getInstance();
		WineTypeAndWineLogic wineLogic = WineTypeAndWineLogic.getInstance();

		// Collect wine type IDs for the provided dish names
		HashMap<String, ArrayList<String>> wineTypeByDish = foodPairingLogic.getWineTypeByFoodDishName();
		ArrayList<String> dishWineTypeIds = new ArrayList<>();
		for (String dishName : dishNames) {
			if (dishName != null && wineTypeByDish.containsKey(dishName)) {
				dishWineTypeIds.addAll(wineTypeByDish.get(dishName));
			}
		}

		// Collect wine type IDs for the provided occasion names
		HashMap<String, ArrayList<String>> wineTypeByOccasion = occasionLogic.getWineTypeByOccasionName();
		ArrayList<String> occasionWineTypeIds = new ArrayList<>();
		for (String occasionName : occasionNames) {
			if (occasionName != null && wineTypeByOccasion.containsKey(occasionName)) {
				occasionWineTypeIds.addAll(wineTypeByOccasion.get(occasionName));
			}
		}

		// Combine wine type IDs from dishes, occasions, and user-provided wine types
		// Combine wine type IDs from dishes, occasions, and user-provided wine types
		ArrayList<String> combinedWineTypeIds = new ArrayList<>();

		// Add all dish-based wine type IDs
		combinedWineTypeIds.addAll(dishWineTypeIds);

		// Add all occasion-based wine type IDs
		for (String wineTypeId : occasionWineTypeIds) {
			if (!combinedWineTypeIds.contains(wineTypeId)) {
				combinedWineTypeIds.add(wineTypeId);
			}
		}

		// Add all user-selected wine type IDs
		for (String wineTypeId : wineTypeIds) {
			if (!combinedWineTypeIds.contains(wineTypeId)) {
				combinedWineTypeIds.add(wineTypeId);
			}
		}

		// If there are no wine types to search for, return an empty list
		if (combinedWineTypeIds.isEmpty()) {
			return wines;
		}

		// Fetch wines matching any of the combined wine type IDs
		HashMap<String, ArrayList<Wine>> wineByWineTypeId = wineLogic.getWineByWineTypeId();

		for (String wineTypeId : combinedWineTypeIds) {
			if (wineByWineTypeId.containsKey(wineTypeId)) {
				wines.addAll(wineByWineTypeId.get(wineTypeId));
			}
		}
		return wines;
	}

	public ArrayList<String> getAllWineIds() {
		ArrayList<String> wineIds = new ArrayList<>();
		String query = "SELECT DISTINCT catalogNumber FROM WineStorage";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				wineIds.add(rs.getString("catalogNumber"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wineIds;
	}

	public ArrayList<String> getAllStorageLocations() {
		ArrayList<String> storageLocations = new ArrayList<>();
		String query = "SELECT DISTINCT storageLocationId FROM WineStorage";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				storageLocations.add(rs.getString("storageLocationId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return storageLocations;
	}

	
	
	
	

	/**
	 * Retrieves all Regular Order IDs.
	 */
	public ArrayList<String> getAllRegularOrderIds() {
		ArrayList<String> orderIds = new ArrayList<>();
		String query = "SELECT DISTINCT id FROM RegularOrderTable";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				orderIds.add(rs.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderIds;
	}

	/**
	 * Retrieves all Wines from WineTable.
	 */
	public ArrayList<String> getAllWines() {
		ArrayList<String> wineIds = new ArrayList<>();
		String query = "SELECT DISTINCT catalogNumber FROM WineTable";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				wineIds.add(rs.getString("catalogNumber"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wineIds;
	}

	/**
	 * Retrieves all Wines associated with a given Regular Order.
	 */
	public ArrayList<String> getWinesForRegularOrder(String orderId) {
		ArrayList<String> wineIds = new ArrayList<>();

		if (orderId == null || orderId.equals("Select Order")) {
			return wineIds;
		}

		String query = "SELECT DISTINCT wineId FROM RegularOrderWine WHERE RegularOrderId = ?";
		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, orderId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					wineIds.add(rs.getString("wineId"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wineIds;
	}

	/**
	 * Retrieves all Regular Orders associated with a given Wine.
	 */
	public ArrayList<String> getRegularOrdersForWine(String wineId) {
		ArrayList<String> orderIds = new ArrayList<>();

		if (wineId == null || wineId.equals("Select Wine")) {
			return orderIds;
		}

		String query = "SELECT DISTINCT RegularOrderId FROM RegularOrderWine WHERE wineId = ?";
		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, wineId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					orderIds.add(rs.getString("RegularOrderId"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderIds;
	}

	/**
	 * Retrieves details of Regular Orders and Wines based on selected filters.
	 */
	public ArrayList<RegularOrderWine> getRegularOrderWineDetails(String orderId, String wineId) {
		ArrayList<RegularOrderWine> orderWineList = new ArrayList<>();
		StringBuilder query = new StringBuilder(
				"SELECT RegularOrderId, wineId, quantity FROM RegularOrderWine WHERE 1=1");

		boolean hasOrderId = orderId != null && !orderId.equals("Select Order");
		boolean hasWineId = wineId != null && !wineId.equals("Select Wine");

		if (!hasOrderId && !hasWineId) {
			return orderWineList; // No filtering applied, return empty list
		}

		if (hasOrderId) {
			query.append(" AND RegularOrderId = ?");
		}
		if (hasWineId) {
			query.append(" AND wineId = ?");
		}

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query.toString())) {

			int paramIndex = 1;
			if (hasOrderId) {
				stmt.setString(paramIndex++, orderId);
			}
			if (hasWineId) {
				stmt.setString(paramIndex, wineId);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					orderWineList.add(new RegularOrderWine(rs.getString("RegularOrderId"), rs.getString("wineId"),
							rs.getInt("quantity")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderWineList;
	}

	/**
	 * Retrieves all RegularOrderWine records.
	 */
	public ArrayList<RegularOrderWine> getAllRegularOrderWineData() {
		ArrayList<RegularOrderWine> orderWineList = new ArrayList<>();
		String query = "SELECT RegularOrderId, wineId, quantity FROM RegularOrderWine";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				orderWineList.add(new RegularOrderWine(rs.getString("RegularOrderId"), rs.getString("wineId"),
						rs.getInt("quantity")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderWineList;
	}

	/**
	 * Retrieves all Urgent Order IDs.
	 */
	public ArrayList<String> getAllUrgentOrderIds() {
		ArrayList<String> orderIds = new ArrayList<>();
		String query = "SELECT DISTINCT id FROM UrgentOrderTable";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				orderIds.add(rs.getString("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderIds;
	}

	/**
	 * Retrieves all Wines associated with a given Urgent Order.
	 */
	public ArrayList<String> getWinesForUrgentOrder(String orderId) {
		ArrayList<String> wineIds = new ArrayList<>();

		if (orderId == null || orderId.equals("Select Order")) {
			return wineIds;
		}

		String query = "SELECT DISTINCT wineId FROM UrgentOrderWine WHERE urgentOrderId = ?";
		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, orderId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					wineIds.add(rs.getString("wineId"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wineIds;
	}

	/**
	 * Retrieves all Urgent Orders associated with a given Wine.
	 */
	public ArrayList<String> getUrgentOrdersForWine(String wineId) {
		ArrayList<String> orderIds = new ArrayList<>();

		if (wineId == null || wineId.equals("Select Wine")) {
			return orderIds;
		}

		String query = "SELECT DISTINCT urgentOrderId FROM UrgentOrderWine WHERE wineId = ?";
		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, wineId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					orderIds.add(rs.getString("urgentOrderId"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderIds;
	}

	/**
	 * Retrieves details of Urgent Orders and Wines based on selected filters.
	 */
	public ArrayList<UrgentOrderWine> getUrgentOrderWineDetails(String orderId, String wineId) {
		ArrayList<UrgentOrderWine> orderWineList = new ArrayList<>();
		StringBuilder query = new StringBuilder(
				"SELECT urgentOrderId, wineId, quantity FROM UrgentOrderWine WHERE 1=1");

		boolean hasOrderId = orderId != null && !orderId.equals("Select Order");
		boolean hasWineId = wineId != null && !wineId.equals("Select Wine");

		if (!hasOrderId && !hasWineId) {
			return orderWineList; // No filtering applied, return empty list
		}

		if (hasOrderId) {
			query.append(" AND urgentOrderId = ?");
		}
		if (hasWineId) {
			query.append(" AND wineId = ?");
		}

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query.toString())) {

			int paramIndex = 1;
			if (hasOrderId) {
				stmt.setString(paramIndex++, orderId);
			}
			if (hasWineId) {
				stmt.setString(paramIndex, wineId);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					orderWineList.add(new UrgentOrderWine(rs.getString("urgentOrderId"), rs.getString("wineId"),
							rs.getInt("quantity")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderWineList;
	}

	/**
	 * Retrieves all UrgentOrderWine records.
	 */
	public ArrayList<UrgentOrderWine> getAllUrgentOrderWineData() {
		ArrayList<UrgentOrderWine> orderWineList = new ArrayList<>();
		String query = "SELECT urgentOrderId, wineId, quantity FROM UrgentOrderWine";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				orderWineList.add(new UrgentOrderWine(rs.getString("urgentOrderId"), rs.getString("wineId"),
						rs.getInt("quantity")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderWineList;
	}

	public List<String> getAllWineTypeSerialNumbers() {
		List<String> serialNumbers = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT serialNumber FROM WineTypeTable")) {

			while (rs.next()) {
				serialNumbers.add(rs.getString("serialNumber"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return serialNumbers;
	}

	// **Associate a dish with a wine type**
	public boolean addWineTypeForDish(String dishName, String wineSerialNumber) {
		if (wineSerialNumber == null || wineSerialNumber.trim().isEmpty()) {
			System.err.println("⚠ Skipping NULL or EMPTY Wine Serial Number for dish: " + dishName);
			return false;
		}

		String query = "INSERT INTO wineTypeAndFoodPairingTable (dishName, WineTypeSerialNumber) VALUES (?, ?)";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, dishName);
			pstmt.setString(2, wineSerialNumber);

			int rowsInserted = pstmt.executeUpdate();
			return rowsInserted > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addWineForProducer(String producerId, String catalogNumber) {
		if (producerId == null || producerId.trim().isEmpty()) {
			System.err.println("⚠ Skipping NULL or EMPTY Producer ID for wine: " + catalogNumber);
			return false;
		}

		String query = "UPDATE WineTable SET producerId = ? WHERE catalogNumber = ?";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, producerId);
			pstmt.setString(2, catalogNumber);

			int rowsUpdated = pstmt.executeUpdate();
			return rowsUpdated > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeWineForProducer(String producerId, String catalogNumber) {
		if (producerId == null || producerId.trim().isEmpty()) {
			System.err.println("⚠ Skipping NULL or EMPTY Producer ID for wine: " + catalogNumber);
			return false;
		}

		String query = "UPDATE WineTable SET producerId = NULL WHERE catalogNumber = ? AND producerId = ?";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, catalogNumber);
			pstmt.setString(2, producerId);

			int rowsUpdated = pstmt.executeUpdate();
			return rowsUpdated > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeAllWinesForProducer(String producerId) {
		if (producerId == null || producerId.trim().isEmpty()) {
			System.err.println("⚠ Skipping NULL or EMPTY Producer ID");
			return false;
		}

		String query = "UPDATE WineTable SET producerId = NULL WHERE producerId = ?";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, producerId);

			int rowsUpdated = pstmt.executeUpdate();
			return rowsUpdated > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<String> getWinesByProducer(String producerId) {
		List<String> wines = new ArrayList<>();

		if (producerId == null || producerId.trim().isEmpty()) {
			System.err.println("⚠ Skipping NULL or EMPTY Producer ID");
			return wines;
		}

		String query = "SELECT catalogNumber FROM WineTable WHERE producerId = ?";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, producerId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				wines.add(rs.getString("catalogNumber"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wines;
	}

	public boolean assignWinesToProducer(String producerId, List<String> catalogNumbers) {
		if (producerId == null || producerId.trim().isEmpty() || catalogNumbers.isEmpty()) {
			System.err.println("⚠ Skipping NULL or EMPTY Producer ID or Wine List");
			return false;
		}

		String query = "UPDATE WineTable SET producerId = ? WHERE catalogNumber = ?";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			for (String catalogNumber : catalogNumbers) {
				pstmt.setString(1, producerId);
				pstmt.setString(2, catalogNumber);
				pstmt.addBatch();
			}

			int[] rowsUpdated = pstmt.executeBatch();
			return rowsUpdated.length > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public HashMap<String, List<String>> getAllProducersWithWines() {
		HashMap<String, List<String>> producerWineMap = new HashMap<>();

		String query = "SELECT producerId, catalogNumber FROM WineTable WHERE producerId IS NOT NULL";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				String producerId = rs.getString("producerId");
				String catalogNumber = rs.getString("catalogNumber");

				producerWineMap.computeIfAbsent(producerId, k -> new ArrayList<>()).add(catalogNumber);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return producerWineMap;
	}

	public boolean addUrgentOrderToWine(String urgentOrderId, String wineId, int quantity) {
		String checkSql = "SELECT quantity FROM UrgentOrderWine WHERE urgentOrderId = ? AND wineId = ?";
		String updateSql = "UPDATE UrgentOrderWine SET quantity = quantity + ? WHERE urgentOrderId = ? AND wineId = ?";
		String insertSql = "INSERT INTO UrgentOrderWine (urgentOrderId, wineId, quantity) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement checkStmt = conn.prepareStatement(checkSql);
				PreparedStatement updateStmt = conn.prepareStatement(updateSql);
				PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

			// Check if record exists
			checkStmt.setString(1, urgentOrderId);
			checkStmt.setString(2, wineId);
			ResultSet rs = checkStmt.executeQuery();

			if (rs.next()) {
				// Record exists, update the quantity
				updateStmt.setInt(1, quantity);
				updateStmt.setString(2, urgentOrderId);
				updateStmt.setString(3, wineId);
				return updateStmt.executeUpdate() > 0;
			} else {
				// Record does not exist, insert new record
				insertStmt.setString(1, urgentOrderId);
				insertStmt.setString(2, wineId);
				insertStmt.setInt(3, quantity);
				return insertStmt.executeUpdate() > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addRegularOrderToWine(String regularOrderId, String wineId, int quantity) {
		String checkSql = "SELECT quantity FROM RegularOrderWine WHERE RegularOrderId = ? AND wineId = ?";
		String updateSql = "UPDATE RegularOrderWine SET quantity = quantity + ? WHERE RegularOrderId = ? AND wineId = ?";
		String insertSql = "INSERT INTO RegularOrderWine (RegularOrderId, wineId, quantity) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement checkStmt = conn.prepareStatement(checkSql);
				PreparedStatement updateStmt = conn.prepareStatement(updateSql);
				PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

			// Check if record exists
			checkStmt.setString(1, regularOrderId);
			checkStmt.setString(2, wineId);
			ResultSet rs = checkStmt.executeQuery();

			if (rs.next()) {
				// Record exists, update the quantity
				updateStmt.setInt(1, quantity);
				updateStmt.setString(2, regularOrderId);
				updateStmt.setString(3, wineId);
				return updateStmt.executeUpdate() > 0;
			} else {
				// Record does not exist, insert new record
				insertStmt.setString(1, regularOrderId);
				insertStmt.setString(2, wineId);
				insertStmt.setInt(3, quantity);
				return insertStmt.executeUpdate() > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addRegularOrderToCustomer(String regularOrderId, String customerId) {
		String checkSql = "SELECT * FROM RegularOrderCustomer WHERE regularOrderId = ? AND customerId = ?";
		String insertSql = "INSERT INTO RegularOrderCustomer (customerId, regularOrderId) VALUES (?, ?)";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement checkStmt = conn.prepareStatement(checkSql);
				PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

			// Check if the relationship already exists
			checkStmt.setString(1, regularOrderId);
			checkStmt.setString(2, customerId);
			ResultSet rs = checkStmt.executeQuery();

			if (!rs.next()) {
				// Insert new relationship
				insertStmt.setString(1, regularOrderId);
				insertStmt.setString(2, customerId);
				return insertStmt.executeUpdate() > 0;
			} else {
				return false; // Relationship already exists
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public ArrayList<RegularOrderCustomer> getAllRegularOrderCustomerData() {
	    ArrayList<RegularOrderCustomer> orderCustomerList = new ArrayList<>();
	    String sql = "SELECT * FROM RegularOrderCustomer";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            orderCustomerList.add(new RegularOrderCustomer(
	                    rs.getString("regularOrderId"),
	                    rs.getString("customerId")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return orderCustomerList;
	}
	public ArrayList<RegularOrderCustomer> getRegularOrderCustomerDetails(String regularOrderId, String customerId) {
	    ArrayList<RegularOrderCustomer> orderCustomerList = new ArrayList<>();
	    String sql = "SELECT * FROM RegularOrderCustomer WHERE regularOrderId = ? OR customerId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, regularOrderId);
	        stmt.setString(2, customerId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            orderCustomerList.add(new RegularOrderCustomer(
	                    rs.getString("regularOrderId"),
	                    rs.getString("customerId")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return orderCustomerList;
	}
	public ArrayList<String> getAllCustomerIds() {
	    ArrayList<String> customerIds = new ArrayList<>();
	    String sql = "SELECT id FROM CustomersTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            customerIds.add(rs.getString("id"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return customerIds;
	}
	public ArrayList<String> getCustomersForRegularOrder(String regularOrderId) {
	    ArrayList<String> customerList = new ArrayList<>();
	    String sql = "SELECT customerId FROM RegularOrderCustomer WHERE regularOrderId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, regularOrderId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            customerList.add(rs.getString("customerId"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return customerList;
	}
	public ArrayList<String> getRegularOrdersForCustomer(String customerId) {
	    ArrayList<String> orderList = new ArrayList<>();
	    String sql = "SELECT regularOrderId FROM RegularOrderCustomer WHERE customerId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, customerId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            orderList.add(rs.getString("regularOrderId"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return orderList;
	}
	public boolean addWineToStorage(String catalogNumber, String storageLocationId, int quantity) {
	    String checkQuery = "SELECT COUNT(*) FROM WineStorage WHERE catalogNumber = ? AND storageLocationId = ?";
	    String updateQuery = "UPDATE WineStorage SET quantity = quantity + ? WHERE catalogNumber = ? AND storageLocationId = ?";
	    String insertQuery = "INSERT INTO WineStorage (catalogNumber, producerId, storageLocationId, quantity) VALUES (?, ?, ?, ?)";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

	        checkStmt.setString(1, catalogNumber);
	        checkStmt.setString(2, storageLocationId);

	        ResultSet rs = checkStmt.executeQuery();
	        if (rs.next() && rs.getInt(1) > 0) { 
	            // ✅ Record exists → Update quantity
	            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
	                updateStmt.setInt(1, quantity);
	                updateStmt.setString(2, catalogNumber);
	                updateStmt.setString(3, storageLocationId);
	                updateStmt.executeUpdate();
	            }
	        } else { 
	            // ✅ Record does not exist → Insert new row with `producerId`
	            String producerId = getProducerIdByWine(catalogNumber); // Fetch producer ID
	            if (producerId == null) {
	                System.err.println("Error: Producer ID not found for catalog number: " + catalogNumber);
	                return false;
	            }
	            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
	                insertStmt.setString(1, catalogNumber);
	                insertStmt.setString(2, producerId);
	                insertStmt.setString(3, storageLocationId);
	                insertStmt.setInt(4, quantity);
	                insertStmt.executeUpdate();
	            }
	        }
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public String getProducerIdByWine(String catalogNumber) {
	    String query = "SELECT producerId FROM WineTable WHERE catalogNumber = ?";
	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, catalogNumber);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("producerId");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null; // If no producer found, return null
	}


	public ArrayList<StockedWine> getAllWineStorage() {
	    ArrayList<StockedWine> storageList = new ArrayList<>();
	    String sql = "SELECT catalogNumber, producerId, storageLocationId, quantity FROM WineStorage";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            storageList.add(new StockedWine(
	                rs.getString("catalogNumber"),
	                rs.getString("producerId"),
	                rs.getString("storageLocationId"),
	                rs.getInt("quantity")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return storageList;
	}
	public ArrayList<String> getStorageLocationsForWine(String wineId) {
	    ArrayList<String> storageLocations = new ArrayList<>();
	    String sql = "SELECT DISTINCT storageLocationId FROM WineStorage WHERE catalogNumber = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, wineId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            storageLocations.add(rs.getString("storageLocationId"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return storageLocations;
	}
	public ArrayList<String> getWinesForStorage(String storageId) {
	    ArrayList<String> wineIds = new ArrayList<>();
	    String sql = "SELECT DISTINCT catalogNumber FROM WineStorage WHERE storageLocationId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, storageId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            wineIds.add(rs.getString("catalogNumber"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return wineIds;
	}
	public ArrayList<StockedWine> getWineStorageDetails(String wineId, String storageId) {
	    ArrayList<StockedWine> storageList = new ArrayList<>();
	    StringBuilder sql = new StringBuilder("SELECT * FROM WineStorage WHERE 1=1");

	    if (!"Select Wine".equals(wineId)) {
	        sql.append(" AND catalogNumber = ?");
	    }
	    if (!"Select Storage Location".equals(storageId)) {
	        sql.append(" AND storageLocationId = ?");
	    }

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        int index = 1;
	        if (!"Select Wine".equals(wineId)) {
	            stmt.setString(index++, wineId);
	        }
	        if (!"Select Storage Location".equals(storageId)) {
	            stmt.setString(index, storageId);
	        }

	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            storageList.add(new StockedWine(
	                rs.getString("catalogNumber"),
	                rs.getString("producerId"),
	                rs.getString("storageLocationId"),
	                rs.getInt("quantity")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return storageList;
	}
	public ArrayList<String> getAllWineTypeIds() {
	    ArrayList<String> wineTypeIds = new ArrayList<>();
	    String query = "SELECT DISTINCT serialNumber FROM WineTypeTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            wineTypeIds.add(rs.getString("serialNumber"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return wineTypeIds;
	}

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
	public ArrayList<WineTypeAndFoodPairing> getWineFoodPairingDetails(String wineTypeSerialNumber, String dishName) {
	    ArrayList<WineTypeAndFoodPairing> pairingList = new ArrayList<>();
	    
	    // ✅ Allow searching by either or both fields
	    String query = "SELECT * FROM wineTypeAndFoodPairingTable " +
	                   "WHERE (? IS NULL OR WineTypeSerialNumber = ?) " +
	                   "AND (? IS NULL OR dishName = ?)";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, wineTypeSerialNumber);
	        stmt.setString(2, wineTypeSerialNumber);
	        stmt.setString(3, dishName);
	        stmt.setString(4, dishName);

	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            pairingList.add(new WineTypeAndFoodPairing(
	                rs.getString("WineTypeSerialNumber"),
	                rs.getString("dishName")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return pairingList;
	}

	public ArrayList<WineTypeAndFoodPairing> getAllWineFoodPairings() {
	    ArrayList<WineTypeAndFoodPairing> pairingList = new ArrayList<>();
	    String query = "SELECT * FROM wineTypeAndFoodPairingTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            pairingList.add(new WineTypeAndFoodPairing(
	                rs.getString("WineTypeSerialNumber"),
	                rs.getString("dishName")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return pairingList;
	}
	public boolean addWineFoodPairing(String wineTypeSerialNumber, String dishName) {
	    String checkQuery = "SELECT COUNT(*) FROM wineTypeAndFoodPairingTable WHERE WineTypeSerialNumber = ? AND dishName = ?";
	    String insertQuery = "INSERT INTO wineTypeAndFoodPairingTable (WineTypeSerialNumber, dishName) VALUES (?, ?)";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

	        checkStmt.setString(1, wineTypeSerialNumber);
	        checkStmt.setString(2, dishName);

	        ResultSet rs = checkStmt.executeQuery();
	        if (rs.next() && rs.getInt(1) > 0) { 
	            // ✅ Pairing already exists, no need to insert
	            System.out.println("Pairing already exists: " + wineTypeSerialNumber + " - " + dishName);
	            return false;
	        } else { 
	            // ✅ Pairing does not exist → Insert new row
	            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
	                insertStmt.setString(1, wineTypeSerialNumber);
	                insertStmt.setString(2, dishName);
	                insertStmt.executeUpdate();
	            }
	        }
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public ArrayList<String> getAllOccasionNames() {
	    ArrayList<String> occasions = new ArrayList<>();
	    String query = "SELECT DISTINCT occasionName FROM OccasionTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            occasions.add(rs.getString("occasionName"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return occasions;
	}
	public boolean addWineTypeOccasion(String wineTypeId, String occasionName) {
	    String checkQuery = "SELECT COUNT(*) FROM occasionAndWineTypeTable WHERE wineTypeId = ? AND occasionName = ?";
	    String insertQuery = "INSERT INTO occasionAndWineTypeTable (wineTypeId, occasionName) VALUES (?, ?)";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

	        checkStmt.setString(1, wineTypeId);
	        checkStmt.setString(2, occasionName);

	        ResultSet rs = checkStmt.executeQuery();
	        if (rs.next() && rs.getInt(1) > 0) { 
	            System.out.println("Pairing already exists between Wine Type and Occasion.");
	            return false; 
	        }

	        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
	            insertStmt.setString(1, wineTypeId);
	            insertStmt.setString(2, occasionName);
	            insertStmt.executeUpdate();
	        }
	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public ArrayList<WineTypeOccasion> getWineTypeOccasionDetails(String wineTypeId, String occasionName) {
	    ArrayList<WineTypeOccasion> occasionList = new ArrayList<>();
	    String query = "SELECT * FROM occasionAndWineTypeTable WHERE wineTypeId = ? OR occasionName = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, wineTypeId);
	        stmt.setString(2, occasionName);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            occasionList.add(new WineTypeOccasion(
	                rs.getString("wineTypeId"),
	                rs.getString("occasionName")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return occasionList;
	}
	public ArrayList<WineTypeOccasion> getAllWineTypeOccasions() {
	    ArrayList<WineTypeOccasion> occasionList = new ArrayList<>();
	    String query = "SELECT * FROM occasionAndWineTypeTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            occasionList.add(new WineTypeOccasion(
	                rs.getString("wineTypeId"),
	                rs.getString("occasionName")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return occasionList;
	}
	public String getWineTypeSerialNumber(String wineTypeName) {
	    String query = "SELECT serialNumber FROM WineTypeTable WHERE name = ?";
	    
	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, wineTypeName);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            return rs.getString("serialNumber");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	public ArrayList<String> getOccasionsForWineType(String wineTypeSerialNumber) {
	    ArrayList<String> occasions = new ArrayList<>();
	    String query = "SELECT occasionName FROM occasionAndWineTypeTable WHERE wineTypeId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, wineTypeSerialNumber);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            occasions.add(rs.getString("occasionName"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return occasions;
	}

	public ArrayList<String> getWineTypesForOccasion(String occasionName) {
	    ArrayList<String> wineTypes = new ArrayList<>();
	    String query = "SELECT wineTypeId FROM occasionAndWineTypeTable WHERE occasionName = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, occasionName);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            wineTypes.add(rs.getString("wineTypeId"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return wineTypes;
	}

	public ArrayList<String> getWineTypesForDish(String dishName) {
	    ArrayList<String> wineTypeIds = new ArrayList<>();

	    if (dishName == null || dishName.equals("Select Dish")) {
	        return wineTypeIds;
	    }

	    String query = "SELECT DISTINCT WineTypeSerialNumber FROM wineTypeAndFoodPairingTable WHERE dishName = ?";
	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, dishName);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                wineTypeIds.add(rs.getString("WineTypeSerialNumber"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return wineTypeIds;
	}

	public ArrayList<String> getDishesForWineType(String selectedWineType) {
	    ArrayList<String> dishNames = new ArrayList<>();

	    if (selectedWineType == null || selectedWineType.equals("Select Wine Type")) {
	        return dishNames;
	    }

	    // ✅ Correct table and column names
	    String query = "SELECT DISTINCT dishName FROM wineTypeAndFoodPairingTable WHERE WineTypeSerialNumber = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, selectedWineType);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                dishNames.add(rs.getString("dishName"));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("❌ SQL Error: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return dishNames;
	}

	public boolean deleteRegularOrderCustomer(String orderId, String customerId) {
	    String query = "DELETE FROM RegularOrderCustomer WHERE regularOrderId = ? AND customerId = ?";
	    
	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setString(1, orderId);
	        stmt.setString(2, customerId);
	        
	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0; // Return true if deletion was successful

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Return false if an error occurs
	    }
	}
	public boolean updateRegularOrderWine(String orderId, String wineId, int newQuantity) {
	    String query = "UPDATE RegularOrderWine SET quantity = ? WHERE RegularOrderId = ? AND wineId = ?";
	    
	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, newQuantity);
	        stmt.setString(2, orderId);
	        stmt.setString(3, wineId);

	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0; // Returns true if update was successful

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Returns false if an error occurs
	    }
	}
	public String getCustomerForRegularOrder(String orderId) {
	    String query = "SELECT customerId FROM RegularOrderCustomer WHERE regularOrderId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, orderId);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("customerId");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // Return null if not found
	}
	public boolean updateRegularOrderCustomer(String orderId, String oldCustomerId, String newOrderId, String newCustomerId) {
	    String query = "UPDATE RegularOrderCustomer SET customerId = ? WHERE RegularOrderId = ? AND customerId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, newCustomerId);
	        stmt.setString(2, orderId);
	        stmt.setString(3, oldCustomerId);

	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0; // Returns true if update was successful

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Returns false if an error occurs
	    }
	}


	public int getQuantityForOrderWine(String orderId, String wineId) {
	    String query = "SELECT quantity FROM RegularOrderWine WHERE RegularOrderId = ? AND wineId = ?";
	    
	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, orderId);
	        stmt.setString(2, wineId);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("quantity");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; // Return -1 if not found
	}
	public boolean deleteUrgentOrderWine(String orderId, String wineId) {
	    String query = "DELETE FROM UrgentOrderWine WHERE urgentOrderId = ? AND wineId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, orderId);
	        stmt.setString(2, wineId);

	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0; // Returns true if deletion was successful

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Returns false if an error occurs
	    }
	}
	public int getQuantityForUrgentOrderWine(String orderId, String wineId) {
	    String query = "SELECT quantity FROM UrgentOrderWine WHERE urgentOrderId = ? AND wineId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, orderId);
	        stmt.setString(2, wineId);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("quantity");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; // Return -1 if not found
	}
	public boolean updateUrgentOrderWine(String orderId, String wineId, int newQuantity) {
	    String query = "UPDATE UrgentOrderWine SET quantity = ? WHERE urgentOrderId = ? AND wineId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, newQuantity);
	        stmt.setString(2, orderId);
	        stmt.setString(3, wineId);

	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0; // Returns true if update was successful

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public boolean deleteWineFromStorage(String wineId, String storageLocationId) {
	    String query = "DELETE FROM WineStorage WHERE catalogNumber = ? AND storageLocationId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, wineId);
	        stmt.setString(2, storageLocationId);

	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0; // Returns true if deletion was successful

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Returns false if an error occurs
	    }
	}
	public int getQuantityForWineStorage(String wineId, String storageLocationId) {
	    String query = "SELECT quantity FROM WineStorage WHERE catalogNumber = ? AND storageLocationId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, wineId);
	        stmt.setString(2, storageLocationId);

	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("quantity");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; // Return -1 if not found
	}
	public boolean updateWineStorageQuantity(String wineId, String storageLocationId, int newQuantity) {
	    String query = "UPDATE WineStorage SET quantity = ? WHERE catalogNumber = ? AND storageLocationId = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, newQuantity);
	        stmt.setString(2, wineId);
	        stmt.setString(3, storageLocationId);

	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public ArrayList<String> getAllWineIdsFromWineTable() {
	    ArrayList<String> wineIds = new ArrayList<>();
	    String query = "SELECT catalogNumber FROM WineTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            wineIds.add(rs.getString("catalogNumber"));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    if (wineIds.isEmpty()) {
	        System.out.println("No Wine IDs found in the WineTable!");
	    } else {
	        System.out.println("Wine IDs retrieved: " + wineIds.size());
	    }

	    return wineIds;
	}
	public ArrayList<String> getAllStorageLocationsFromStorageTable() {
	    ArrayList<String> storageLocations = new ArrayList<>();
	    String query = "SELECT storageLocationId FROM StorageLocationTable";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            storageLocations.add(rs.getString("storageLocationId"));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    if (storageLocations.isEmpty()) {
	        System.out.println("No Storage Locations found in StorageLocationTable!");
	    } else {
	        System.out.println("Storage Locations retrieved: " + storageLocations.size());
	    }

	    return storageLocations;
	}
	public boolean deleteRegularOrderWine(String orderId, String wineId) {
	    String query = "DELETE FROM RegularOrderWine WHERE RegularOrderId = ? AND wineId = ?";
	    
	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setString(1, orderId);
	        stmt.setString(2, wineId);

	        int affectedRows = stmt.executeUpdate(); // Executes deletion
	        return affectedRows > 0; // Returns true if deletion was successful

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Returns false if an error occurred
	    }
	}
	public boolean deleteWineFoodPairing(String wineTypeSerialNumber, String dishName) {
	    String query = "DELETE FROM wineTypeAndFoodPairingTable WHERE WineTypeSerialNumber = ? AND dishName = ?";

	    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, wineTypeSerialNumber);
	        stmt.setString(2, dishName);

	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0; // ✅ Return true if a row was deleted
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}










}
