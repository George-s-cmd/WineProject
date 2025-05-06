package control;

import entity.Consts;

import entity.Wine;
import entity.WineType;
import entity.Consts.Manipulation;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WineLogic {

	private static WineLogic _instance;

	// Private constructor to implement Singleton Pattern
	public WineLogic() {
	}

	// Singleton instance method
	public static WineLogic getInstance() {
		if (_instance == null)
			_instance = new WineLogic();
		return _instance;
	}

	/**
	 * Retrieves all wines from the database.
	 *
	 * @return A list of wines as strings (for display purposes).
	 */

	public List<String> getAllCatalogNumbers() {
		List<String> catalogNumbers = new ArrayList<>();
		String query = "SELECT  catalogNumber FROM WineTable";

		try (Connection connection = DriverManager.getConnection(Consts.CONN_STR)) {
			System.out.println("DEBUG: Database connected successfully!");

			try (PreparedStatement statement = connection.prepareStatement(query);
					ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					catalogNumbers.add(resultSet.getString("catalogNumber"));
				}

				if (catalogNumbers.isEmpty()) {
					System.out.println("WARNING: No catalog numbers found in database!");
				} else {
					System.out.println("DEBUG: Catalog numbers found -> " + catalogNumbers);
				}

			}
		} catch (SQLException e) {
			System.err.println("ERROR: Failed to connect to database -> " + e.getMessage());
			e.printStackTrace();
		}

		return catalogNumbers;
	}

	public String getProducerIdByCatalog(String catalogNumber) {
		String query = "SELECT producerId FROM WineTable WHERE catalogNumber = ?";

		try (Connection connection = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, catalogNumber);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getString("producerId");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ""; // Return empty string if no producer found
	}

	public boolean addWine(Wine wine) {
		if (wine == null) {
			throw new IllegalArgumentException("Wine object cannot be null.");
		}

		// Validate required fields
		if (wine.getCatalogNum() == null || wine.getCatalogNum().trim().isEmpty() || wine.getProducerId() == null
				|| wine.getProducerId().trim().isEmpty() || wine.getName() == null || wine.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Catalog Number, Producer ID, and Name are required fields.");
		}

		try {
			// Load UCanAccess driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR)) {
				conn.setAutoCommit(false);

				try (PreparedStatement stmt = conn.prepareStatement(Consts.SQL_INS_WINE)) {
					stmt.setString(1, wine.getCatalogNum());
					stmt.setString(2, wine.getProducerId());
					stmt.setString(3, wine.getName());
					stmt.setInt(4, wine.getProductionYear());
					stmt.setDouble(5, wine.getPricePerBottle());
					stmt.setString(6, wine.getSweetnessLevel() != null ? wine.getSweetnessLevel().toString() : null);
					stmt.setString(7, wine.getDescription() != null ? wine.getDescription() : null);
					stmt.setString(8, wine.getProductImage() != null ? wine.getProductImage() : null);
					stmt.setString(9, wine.getWineTypeId()); // Assuming WineTypeId is available in Wine object

					int rowsAffected = stmt.executeUpdate();
					if (rowsAffected > 0) {
						conn.commit(); // Commit transaction
						System.out.println("Wine added successfully to the database.");
						return true;
					} else {
						System.err.println("Failed to add wine to the database.");
					}
				} catch (SQLException e) {
					conn.rollback(); // Rollback transaction in case of an error
					System.err.println("Database error during insertion: " + e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e) {
			System.err.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Database connection or SQL error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected error: " + e.getMessage());
			e.printStackTrace();
		}

		return false; // Return false if operation failed
	}

	public boolean removeWine(String catalogNum) { // Only use catalogNum if producerId is not needed
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					PreparedStatement stmt = conn.prepareStatement(Consts.SQL_DEL_WINE)) {

				stmt.setString(1, catalogNum); // Only one parameter

				System.out.println("Executing SQL: " + stmt); // Debugging
				int rowsAffected = stmt.executeUpdate();
				return rowsAffected > 0;
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean editWine(Wine wine) {
		if (wine.getCatalogNum() == null || wine.getCatalogNum().trim().isEmpty()) {
			throw new IllegalArgumentException("Catalog Number cannot be null or empty");
		}

		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					PreparedStatement stmt = conn.prepareStatement(Consts.SQL_UPD_WINE)) {

				int i = 1;
				stmt.setString(i++, wine.getName());
				stmt.setInt(i++, wine.getProductionYear());
				stmt.setDouble(i++, wine.getPricePerBottle());
				stmt.setString(i++, wine.getSweetnessLevel() != null ? wine.getSweetnessLevel().toString() : null);
				stmt.setString(i++, wine.getDescription() != null ? wine.getDescription() : null);
				stmt.setString(i++, wine.getProductImage() != null ? wine.getProductImage() : null);
				stmt.setString(i++, wine.getWineTypeId()); // Assuming WineTypeId is available in Wine object
				stmt.setString(i++, wine.getCatalogNum());
				stmt.setString(i++, wine.getProducerId());

				stmt.executeUpdate();
				return true;
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void importWineFromXML(String path) {
		try {
			// Load and parse XML
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
			doc.getDocumentElement().normalize();

			// Get all <wine> nodes inside <wines>
			NodeList nl = doc.getElementsByTagName("wine");

			int errors = 0;

			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) nl.item(i);

					// Construct a Wine object
					Wine w = new Wine(el.getElementsByTagName("catalogNumber").item(0).getTextContent(),
							el.getElementsByTagName("producerId").item(0).getTextContent(),
							el.getElementsByTagName("name").item(0).getTextContent(),
							Integer.parseInt(el.getElementsByTagName("productionYear").item(0).getTextContent()),
							Double.parseDouble(el.getElementsByTagName("pricePerBottle").item(0).getTextContent()),
							el.getElementsByTagName("sweetnessLevel").item(0).getTextContent(),
							el.getElementsByTagName("description").item(0).getTextContent(),
							el.getElementsByTagName("productImage").item(0).getTextContent(),
							el.getElementsByTagName("wineTypeId").item(0).getTextContent());

					// Try to insert; if it fails, try update
					if (!manipulateWine(w, Manipulation.INSERT) && !manipulateWine(w, Manipulation.UPDATE)) {
						errors++;
					}
				}
			}

			System.out.println((errors == 0) ? "Wine data imported successfully!"
					: String.format("Wine data imported with %d errors!", errors));

		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public Wine getWine(String catalogNum, String producerId) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_SINGLE_WINE)) {

				stmt.setString(1, catalogNum);
				stmt.setString(2, producerId);

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return new Wine(rs.getString("CatalogNumber"), rs.getString("ProducerId"), rs.getString("Name"),
								rs.getInt("ProductionYear"), rs.getDouble("PricePerBottle"),
								rs.getString("SweetnessLevel"), rs.getString("Description"),
								rs.getString("ProductImage"), rs.getString("WineTypeId"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean manipulateWine(Wine w, Manipulation manipulation) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
					CallableStatement stmt = conn.prepareCall((manipulation.equals(Manipulation.UPDATE))
							? Consts.SQL_UPD_WINE
							: (manipulation.equals(Manipulation.INSERT)) ? Consts.SQL_INS_WINE : Consts.SQL_DEL_WINE)) {

				allocateWineParams(stmt, w, manipulation);
				stmt.executeUpdate();
				return true;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void allocateWineParams(CallableStatement stmt, Wine w, Manipulation m) throws SQLException {
		int i = 1;

		if (!m.equals(Manipulation.UPDATE)) {
			stmt.setString(i++, w.getCatalogNum());

			if (m.equals(Manipulation.DELETE))
				return;
		}

		stmt.setString(i++, w.getProducerId());
		stmt.setString(i++, w.getName());
		stmt.setInt(i++, w.getProductionYear());
		stmt.setDouble(i++, w.getPricePerBottle());
		stmt.setString(i++, w.getSweetnessLevel());
		stmt.setString(i++, w.getDescription());
		stmt.setString(i++, w.getProductImage());
		stmt.setString(i++, w.getWineTypeId());

		if (m.equals(Manipulation.UPDATE))
			stmt.setString(i, w.getCatalogNum());
	}

	public ArrayList<String> getAllWineIds() {
		ArrayList<String> wineIds = new ArrayList<>();
		String query = "SELECT catalogNumber FROM WineTable";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement pstmt = conn.prepareStatement(query);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				wineIds.add(rs.getString("catalogNumber"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return wineIds;
	}

	public Wine getWineByCatalogNumber(String catalogNum) {
		String query = "SELECT * FROM WineTable WHERE catalogNumber = ?";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, catalogNum);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return new Wine(rs.getString("catalogNumber"), rs.getString("producerId"), rs.getString("name"),
						rs.getInt("productionYear"), rs.getDouble("pricePerBottle"), rs.getString("sweetnessLevel"),
						rs.getString("description"), rs.getString("productImage"), rs.getString("wineTypeId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public List<WineType> getWineTypeById(String wineTypeId) {
		List<WineType> wineTypes = new ArrayList<>();
		String query = "SELECT serialNumber, name FROM WineTypeTable WHERE serialNumber = ?";

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, wineTypeId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				WineType wineType = new WineType(rs.getString("serialNumber"), // Wine Type ID
						rs.getString("name") // Wine Type Name
				);
				wineTypes.add(wineType);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (wineTypes.isEmpty()) {
			System.out.println("No wine types found for ID: " + wineTypeId);
		} else {
			System.out.println("Wine Type(s) retrieved: " + wineTypes.size());
		}

		return wineTypes;
	}

	public boolean wineExists(String catalogNum) {
		boolean exists = false;

		try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
				PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_WINE_EXISTS)) {

			stmt.setString(1, catalogNum); // Set the parameter
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				exists = rs.getInt(1) > 0; // If count > 0, the wine exists
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return exists;
	}
	public ArrayList<Wine> getAllWines() {
	    ArrayList<Wine> wines = new ArrayList<>();

	    try {
	        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

	        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
	             PreparedStatement stmt = conn.prepareStatement(Consts.SQL_SEL_WINES);
	             ResultSet rs = stmt.executeQuery()) {

	            while (rs.next()) {
	                wines.add(new Wine(
	                    rs.getString("CatalogNumber"),
	                    rs.getString("ProducerId"),
	                    rs.getString("Name"),
	                    rs.getInt("ProductionYear"),
	                    rs.getDouble("PricePerBottle"),
	                    rs.getString("SweetnessLevel"), // ✅ Fetch directly as String
	                    rs.getString("Description"),
	                    rs.getString("ProductImage"),
	                    rs.getString("WineTypeId") // ✅ Now included
	                ));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return wines;
	}

}