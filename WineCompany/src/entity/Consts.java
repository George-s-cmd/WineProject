package entity;

import java.io.File;
import java.net.URLDecoder;

public final class Consts {
	private Consts() {
		throw new AssertionError();
	}

	protected static final String DB_FILEPATH = getDBPath();
	public static final String GET_WINES_BY_WINE_TYPE = "SELECT WineTable.catalogNumber, WineTable.producerId, WineTable.name AS WineName, "
			+ "WineTable.productionYear, WineTable.pricePerBottle, WineTable.sweetnessLevel, WineTable.description, "
			+ "WineTable.productImage, WineTypeTable.name AS WineTypeName " + "FROM WineTable "
			+ "INNER JOIN WineTypeTable ON WineTable.wineTypeId = WineTypeTable.serialNumber "
			+ "WHERE WineTable.wineTypeId = ?";
	public static final String GET_WINETYPE_BY_DISHNAME = "SELECT WineTypeTable.serialNumber, WineTypeTable.name "
			+ "FROM WineTypeTable "
			+ "INNER JOIN wineTypeAndFoodPairingTable ON WineTypeTable.serialNumber = wineTypeAndFoodPairingTable.WineTypeSerialNumber "
			+ "WHERE wineTypeAndFoodPairingTable.dishName = ?";
	public static final String GET_WINETYPE_BY_OCCASIONNAME = "SELECT WineTypeTable.serialNumber, WineTypeTable.name "
			+ "FROM WineTypeTable " + "INNER JOIN occasionAndWineTypeTable "
			+ "ON WineTypeTable.serialNumber = occasionAndWineTypeTable.wineTypeId "
			+ "WHERE occasionAndWineTypeTable.occasionName = ?";
	public static final String CHECK_WINE_TYPE_EXISTS = "SELECT COUNT(*) FROM WineTypeTable WHERE serialNumber = ?";
	public static final String CHECK_WINE_EXISTS = "SELECT COUNT(*) FROM WineTable WHERE catalogNumber = ?";
	public static final String CHECK_CUSTOMER_EXISTS = "SELECT COUNT(*) FROM CustomersTable WHERE id = ?";
	public static final String CHECK_EMPLOYEE_EXISTS = "SELECT COUNT(*) FROM EmployeesTable WHERE id = ?";
	public static final String CHECK_FOOD_PAIRING_EXISTS = "SELECT COUNT(*) FROM FoodPairingsTable WHERE dishName = ?";
	public static final String CHECK_OCCASION_EXISTS = "SELECT COUNT(*) FROM OccasionTable WHERE occasionName = ?";
	public static final String CHECK_PRODUCER_EXISTS = "SELECT COUNT(*) FROM ProducersTable WHERE id = ?";
	public static final String CHECK_REGULAR_ORDER_EXISTS = "SELECT COUNT(*) FROM RegularOrderTable WHERE id = ?";
	public static final String CHECK_STORAGE_LOCATION_ID_EXISTS = "SELECT COUNT(*) FROM StorageLocationTable WHERE storageLocationId = ?";
	public static final String CHECK_URGENT_ORDER_EXISTS = "SELECT COUNT(*) FROM UrgentOrderTable WHERE id = ?";

	public static final String GET_ALL_DISH_NAMES = "SELECT DISTINCT dishName FROM foodPairingsTable";
	public static final String GET_ALL_WINE_TYPE_IDS = "SELECT serialNumber FROM WineTypeTable";
	public static final String GET_WINES = "SELECT * FROM WineTable";

	public static final String GET_OCCASSION_NAMES = "SELECT DISTINCT occasionName FROM OccasionTable";
	public static final String GET_ALL_CUSTOMERS1 = "SELECT * FROM CustomersTable";

	// Fetch urgent orders for a specific customer
	public static final String GET_URGENT_ORDERS_BY_CUSTOMER = "SELECT * FROM UrgentOrderTable WHERE customerId = ?";

	// Fetch regular orders for a specific customer using mainCustomerId
	public static final String GET_REGULAR_ORDERS_BY_CUSTOMER = "SELECT * FROM RegularOrderTable WHERE mainCustomerId = ?";
	public static final String SQL_SEL_PRODUCERS = "SELECT * FROM ProducersTable";
	public static final String SQL_INS_PRODUCER = "INSERT INTO ProducersTable (id, name, contactPhone, address, email) VALUES (?, ?, ?, ?, ?)";
	public static final String SQL_DEL_PRODUCER = "DELETE FROM ProducersTable WHERE id = ?";
	public static final String SQL_UPD_PRODUCER = "UPDATE ProducersTable SET name = ?, contactPhone = ?, address = ?, email = ? WHERE id = ?";
	public static final String SQL_GET_DISTINCT_PRODUCER_IDS = "SELECT DISTINCT ProducerId FROM ProducersTable";
	public static final String SQL_SEL_SINGLE_WINE = "SELECT * FROM WineTable WHERE catalogNumber = ? AND producerId = ?";
	public static final String SQL_DEL_WINE = "DELETE FROM WineTable WHERE catalogNumber = ? ";

	public static final String SQL_INS_WINE = "INSERT INTO WineTable (catalogNumber, producerId, name, productionYear, pricePerBottle, sweetnessLevel, description, productImage, wineTypeId) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String SQL_UPD_WINE = "UPDATE WineTable "
			+ "SET name = ?, productionYear = ?, pricePerBottle = ?, sweetnessLevel = ?, description = ?, productImage = ?, wineTypeId = ? "
			+ "WHERE catalogNumber = ? AND producerId = ?";
	public static final String GET_UNDERPERFORMING_EMPLOYEES = """
			SELECT e.id, e.officeAddress, e.employmentStartDate, COUNT(DISTINCT uo.id) AS urgentCount, COUNT(DISTINCT ro.id) AS regularCount
			FROM EmployeesTable e
			LEFT JOIN OrdersTable o ON e.id = o.employeesId
			LEFT JOIN UrgentOrderTable uo ON o.orderNumber = uo.id AND o.orderDate BETWEEN ? AND ?
			LEFT JOIN RegularOrderTable ro ON o.orderNumber = ro.id AND o.orderDate BETWEEN ? AND ?
			GROUP BY e.id, e.officeAddress, e.employmentStartDate
			HAVING COUNT(DISTINCT uo.id) < 2 OR COUNT(DISTINCT ro.id) < 4
			""";
	public static final String SQL_GET_ALL_EMPLOYEES = """
			SELECT e.id, e.officeAddress, e.employmentStartDate,
			       p.name, p.phoneNumber, p.email
			FROM EmployeesTable e
			JOIN PersonsTable p ON e.id = p.personId
			""";

	public static final String SQL_INSERT_EMPLOYEE = """
			INSERT INTO PersonsTable (personId, name, phoneNumber, email) VALUES (?, ?, ?, ?);
			INSERT INTO EmployeesTable (id, officeAddress, employmentStartDate) VALUES (?, ?, ?)
			""";

	public static final String SQL_UPDATE_EMPLOYEE = """
			UPDATE PersonsTable
			SET name = ?, phoneNumber = ?, email = ?
			WHERE personId = ?;

			UPDATE EmployeesTable
			SET officeAddress = ?, employmentStartDate = ?
			WHERE id = ?
			""";
	public static final String SQL_SEL_WINES = "SELECT CatalogNumber, ProducerId, Name, ProductionYear, "
			+ "PricePerBottle, SweetnessLevel, Description, ProductImage, WineTypeId " + "FROM WineTable";
	public static final String SQL_SEL_WINE_TYPES = "SELECT serialNumber, name FROM WineTypeTable";

	public static final String SQL_DELETE_EMPLOYEE = """
			DELETE FROM EmployeesTable WHERE id = ?;
			DELETE FROM PersonsTable WHERE personId = ?
			""";

	public static final String SQL_GET_EMPLOYEE_BY_ID = """
			SELECT e.id, e.officeAddress, e.employmentStartDate,
			       p.name, p.phoneNumber, p.email
			FROM EmployeesTable e
			JOIN PersonsTable p ON e.id = p.personId
			WHERE e.id = ?
			""";
	public static final String SQL_GET_ALL_CUSTOMERS = """
		    SELECT p.personId AS id, p.name, p.phoneNumber, p.email, 
		           c.dayOfFirstContact, c.deliveryAddress
		    FROM PersonsTable p
		    INNER JOIN CustomersTable c ON p.personId = c.id
		""";

	public static final String GET_ALL_ORDERS = "SELECT * FROM OrdersTable";
	public static final String SQL_SEL_URGENT_ORDERS = """
					 SELECT OrdersTable.orderNumber, OrdersTable.orderDate, OrdersTable.orderStatus,
			       OrdersTable.shipmentDate, OrdersTable.employeesId,
			       UrgentOrderTable.priority, UrgentOrderTable.expectedDelivery,
			       UrgentOrderTable.customerId
			FROM OrdersTable
			INNER JOIN UrgentOrderTable ON OrdersTable.orderNumber = UrgentOrderTable.id;

					""";
	public static final String SQL_GET_ALL_REGULAR_ORDERS = """
		    SELECT OrdersTable.orderNumber, OrdersTable.orderDate, OrdersTable.orderStatus, 
		           OrdersTable.shipmentDate, OrdersTable.employeesId, 
		           RegularOrderTable.mainCustomerId
		    FROM OrdersTable
		    INNER JOIN RegularOrderTable ON OrdersTable.orderNumber = RegularOrderTable.id
		""";
	public static final String SQL_GET_ALL_STORAGE_LOCATIONS = 
		    "SELECT storageLocationId, name FROM StorageLocationTable";


	public static final String GET_ALL_URGENT_ORDERS = "SELECT UrgentOrderTable.id, OrdersTable.orderDate, OrdersTable.orderStatus, "
			+ "OrdersTable.shipmentDate, OrdersTable.employeesId, UrgentOrderTable.priority, "
			+ "UrgentOrderTable.expectedDelivery, UrgentOrderTable.customerId " + "FROM UrgentOrderTable "
			+ "INNER JOIN OrdersTable ON UrgentOrderTable.id = OrdersTable.orderNumber";

	public static final String SQL_SELECT_URGENT_ORDER_BY_ID = "SELECT UrgentOrderTable.id, OrdersTable.orderDate, OrdersTable.orderStatus, "
			+ "OrdersTable.shipmentDate, OrdersTable.employeesId, UrgentOrderTable.priority, "
			+ "UrgentOrderTable.expectedDelivery, UrgentOrderTable.customerId " + "FROM UrgentOrderTable "
			+ "INNER JOIN OrdersTable ON UrgentOrderTable.id = OrdersTable.orderNumber "
			+ "WHERE UrgentOrderTable.id = ?";

	public static final String SQL_INSERT_URGENT_ORDER = "INSERT INTO OrdersTable (orderNumber, orderDate, orderStatus, shipmentDate, employeesId) "
			+ "VALUES (?, ?, ?, ?, ?); " + "INSERT INTO UrgentOrderTable (id, priority, expectedDelivery, customerId) "
			+ "VALUES (?, ?, ?, ?)";

	public static final String SQL_UPDATE_URGENT_ORDER = "UPDATE OrdersTable SET orderDate = ?, orderStatus = ?, shipmentDate = ?, employeesId = ? "
			+ "WHERE orderNumber = ?; "
			+ "UPDATE UrgentOrderTable SET priority = ?, expectedDelivery = ?, customerId = ? " + "WHERE id = ?";

	public static final String SQL_DELETE_URGENT_ORDER = "DELETE FROM UrgentOrderTable WHERE id = ?; "
			+ "DELETE FROM OrdersTable WHERE orderNumber = ?";

	public static final String SQL_INSERT_ORDER = "INSERT INTO OrdersTable (orderNumber, orderDate, orderStatus, shipmentDate, employeesId) VALUES (?, ?, ?, ?, ?)";
	public static final String SQL_UPDATE_ORDER = "UPDATE OrdersTable SET orderDate = ?, orderStatus = ?, shipmentDate = ?, employeesId = ? WHERE orderNumber = ?";
	public static final String SQL_DELETE_ORDER = "DELETE FROM OrdersTable WHERE orderNumber = ?";
	public static final String SQL_SELECT_ORDER_BY_ID = "SELECT * FROM OrdersTable WHERE orderNumber = ?";
	public static final String GET_ALL_REGULAR_ORDERS = """
			SELECT r.id, o.orderDate, o.orderStatus, o.shipmentDate, o.employeesId,
			       c.id AS mainCustomerId, p.name, p.phoneNumber, p.email, c.dayOfFirstContact, c.deliveryAddress
			FROM RegularOrderTable r
			INNER JOIN OrdersTable o ON r.id = o.orderNumber
			INNER JOIN CustomersTable c ON r.mainCustomerId = c.id
			INNER JOIN PersonsTable p ON c.id = p.personId
			""";

	public static final String SQL_SELECT_REGULAR_ORDER_BY_ID = """
			SELECT r.id, o.orderDate, o.orderStatus, o.shipmentDate, o.employeesId,
			       c.id AS mainCustomerId, p.name, p.phoneNumber, p.email, c.dayOfFirstContact, c.deliveryAddress
			FROM RegularOrderTable r
			INNER JOIN OrdersTable o ON r.id = o.orderNumber
			INNER JOIN CustomersTable c ON r.mainCustomerId = c.id
			INNER JOIN PersonsTable p ON c.id = p.personId
			WHERE r.id = ?
			""";

	public static final String SQL_INSERT_REGULAR_ORDER = """
			INSERT INTO OrdersTable (orderNumber, orderDate, orderStatus, shipmentDate, employeesId)
			VALUES (?, ?, ?, ?, ?);
			INSERT INTO RegularOrderTable (id, mainCustomerId)
			VALUES (?, ?)
			""";

	public static final String SQL_UPDATE_REGULAR_ORDER = """
			UPDATE OrdersTable
			SET orderDate = ?, orderStatus = ?, shipmentDate = ?, employeesId = ?
			WHERE orderNumber = ?;
			UPDATE RegularOrderTable
			SET mainCustomerId = ?
			WHERE id = ?
			""";

	public static final String SQL_SELECT_CUSTOMER_BY_ID = """
			SELECT c.id, p.name, p.phoneNumber, p.email, c.dayOfFirstContact, c.deliveryAddress
			FROM CustomersTable c
			INNER JOIN PersonsTable p ON c.id = p.personId
			WHERE c.id = ?
			""";

	public static final String GET_ALL_CUSTOMERS = """
			SELECT c.id, p.name, p.phoneNumber, p.email, c.dayOfFirstContact, c.deliveryAddress
			FROM CustomersTable c
			INNER JOIN PersonsTable p ON c.id = p.personId
			""";
	public static final String SQL_GET_ALL_FOOD_NAMES = 
		    "SELECT dishName FROM foodPairingsTable ORDER BY dishName ASC;";
	public static final String SQL_GET_ALL_OCCASIONS = 
	        "SELECT occasionName, description, season, location FROM OccasionTable";
	// Select all occasions
	public static final String GET_ALL_OCCASIONS = "SELECT * FROM OccasionTable";

	// Select a single occasion by name
	public static final String GET_OCCASION_BY_NAME = "SELECT * FROM OccasionTable WHERE occasionName = ?";

	// Insert a new occasion
	public static final String INSERT_OCCASION = "INSERT INTO OccasionTable (occasionName, description, season, location) VALUES (?, ?, ?, ?)";

	// Update an occasion
	public static final String UPDATE_OCCASION = "UPDATE OccasionTable SET description = ?, season = ?, location = ? WHERE occasionName = ?";

	// Delete an occasion
	public static final String DELETE_OCCASION = "DELETE FROM OccasionTable WHERE occasionName = ?";

	public enum Manipulation {
		UPDATE, INSERT, DELETE;
	}

	public static final String CONN_STR = "jdbc:ucanaccess://" + DB_FILEPATH + ";COLUMNORDER=DISPLAY";

	private static String getDBPath() {
		try {
			String path = Consts.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decoded = URLDecoder.decode(path, "UTF-8");

			// Check if running from a JAR file or an IDE
			if (decoded.contains(".jar")) {
				decoded = decoded.substring(0, decoded.lastIndexOf('/'));
				return decoded + "/database/ex3_324991892_212015739_one.accdb";
			} else {
				decoded = decoded.substring(0, decoded.lastIndexOf("bin/"));
				String dbPath = decoded + "src/entity/ex3_324991892_212015739_one.accdb";

				// Validate database path
				File dbFile = new File(dbPath);
				if (dbFile.exists()) {
					return dbFile.getAbsolutePath();
				} else {
					System.err.println("Database file not found at: " + dbPath);
					return "C:/path/to/default/database/ex3_324991892_212015739_one.accdb";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}