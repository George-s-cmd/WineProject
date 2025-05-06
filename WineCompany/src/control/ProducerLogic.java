package control;

import entity.Producer;
import entity.Wine;
import entity.Consts;
import entity.Consts.Manipulation;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
public class ProducerLogic {
    private static ProducerLogic _instance;

    private ProducerLogic() {}

    public static ProducerLogic getInstance() {
        if (_instance == null) {
            _instance = new ProducerLogic();
        }
        return _instance;
    }

    /**
     * Retrieves all producers from the database.
     *
     * @return A list of all producers.
     */
    public ArrayList<Producer> getAllProducers() {
        ArrayList<Producer> producers = new ArrayList<>();
        String query = "SELECT * FROM ProducersTable";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Producer producer = new Producer(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("contactPhone"),
                            rs.getString("address"),
                            rs.getString("email")
                    );

                    // Fetch wines for this producer
                    ArrayList<Wine> wines = getWinesForProducer(producer.getId());
                    producer.setWineList(wines);

                    producers.add(producer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return producers;
    }

    /**
     * Retrieves all wines associated with a given producer ID.
     *
     * @param producerId The producer ID.
     * @return A list of wines associated with the producer.
     */
    public ArrayList<Wine> getWinesForProducer(String producerId) {
        ArrayList<Wine> wines = new ArrayList<>();

        String query = """
                SELECT catalogNumber, name, productionYear, pricePerBottle, 
                       sweetnessLevel, description, productImage, wineTypeId
                FROM WineTable
                WHERE producerId = ?
                """;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, producerId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Wine wine = new Wine(
                                rs.getString("catalogNumber"),
                                producerId,
                                rs.getString("name"),
                                rs.getInt("productionYear"),
                                rs.getDouble("pricePerBottle"),
                                rs.getString("sweetnessLevel"),
                                rs.getString("description"),
                                rs.getString("productImage"),
                                rs.getString("wineTypeId")
                        );
                        wines.add(wine);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wines;
    }

    /**
     * Searches for a producer by ID and retrieves associated wines.
     *
     * @param producerId The producer ID.
     * @return A Producer object with all associated wines.
     */
    public Producer searchProducer(String producerId) {
        Producer producer = null;
        String query = "SELECT * FROM ProducersTable WHERE id = ?";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, producerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        producer = new Producer(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("contactPhone"),
                                rs.getString("address"),
                                rs.getString("email")
                        );

                        // Fetch wines for this producer
                        ArrayList<Wine> wines = getWinesForProducer(producerId);
                        producer.setWineList(wines);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return producer;
    }

    /**
     * Adds a new producer to the database.
     *
     * @param producer The producer object.
     * @return True if successful, otherwise false.
     */
    public boolean addProducer(Producer producer) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_INS_PRODUCER)) {

                stmt.setString(1, producer.getId());
                stmt.setString(2, producer.getName());
                stmt.setString(3, producer.getContactPhone());
                stmt.setString(4, producer.getAddress());
                stmt.setString(5, producer.getEmail());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Edits an existing producer in the database.
     *
     * @param producer The producer object with updated values.
     * @return True if successful, otherwise false.
     */
    public boolean editProducer(Producer producer) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_UPD_PRODUCER)) {

                stmt.setString(1, producer.getName());
                stmt.setString(2, producer.getContactPhone());
                stmt.setString(3, producer.getAddress());
                stmt.setString(4, producer.getEmail());
                stmt.setString(5, producer.getId());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a producer from the database.
     *
     * @param producerId The producer ID to delete.
     * @return True if successful, otherwise false.
     */
    public boolean deleteProducer(String producerId) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(Consts.SQL_DEL_PRODUCER)) {

                stmt.setString(1, producerId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Loads all producers and their wines into a map.
     *
     * @return A map with Producer ID as the key and a list of wines as the value.
     */
    public Map<String, List<Wine>> getWinesByProducerId() {
        Map<String, List<Wine>> producerWineMap = new HashMap<>();
        String query = "SELECT * FROM WineTable";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String producerId = rs.getString("producerId");

                    producerWineMap.putIfAbsent(producerId, new ArrayList<>());

                    Wine wine = new Wine(
                            rs.getString("catalogNumber"),
                            producerId,
                            rs.getString("name"),
                            rs.getInt("productionYear"),
                            rs.getDouble("pricePerBottle"),
                            rs.getString("sweetnessLevel"),
                            rs.getString("description"),
                            rs.getString("productImage"),
                            rs.getString("wineTypeId")
                    );

                    producerWineMap.get(producerId).add(wine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return producerWineMap;
    }
    public void importProducersFromXML(String path) {
        try {
            // Load and parse XML
            Document doc = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder().parse(new File(path));
            doc.getDocumentElement().normalize();

            // Get all <producer> nodes
            NodeList nl = doc.getElementsByTagName("producer");

            int errors = 0;

            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nl.item(i);

                    // Construct a Producer object
                    Producer p = new Producer(
                        el.getElementsByTagName("id").item(0).getTextContent(),
                        el.getElementsByTagName("name").item(0).getTextContent(),
                        el.getElementsByTagName("contactPhone").item(0).getTextContent(),
                        el.getElementsByTagName("address").item(0).getTextContent(),
                        el.getElementsByTagName("email").item(0).getTextContent()
                    );

                    // Try to insert; if it fails, try update
                    if (!manipulateProducer(p, Manipulation.INSERT) 
                        && !manipulateProducer(p, Manipulation.UPDATE)) {
                        errors++;
                    }
                }
            }

            System.out.println((errors == 0)
                ? "Producer data imported successfully!"
                : String.format("Producer data imported with %d errors!", errors));

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    public boolean manipulateProducer(Producer p, Manipulation manipulation) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement stmt = conn.prepareCall(
                     (manipulation.equals(Manipulation.UPDATE)) 
                         ? Consts.SQL_UPD_PRODUCER 
                         : (manipulation.equals(Manipulation.INSERT)) 
                             ? Consts.SQL_INS_PRODUCER 
                             : Consts.SQL_DEL_PRODUCER)) {

                allocateProducerParams(stmt, p, manipulation);
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
    private void allocateProducerParams(CallableStatement stmt, Producer p, Manipulation manipulation) throws SQLException {
        int i = 1;

        if (!manipulation.equals(Manipulation.UPDATE)) {
            stmt.setString(i++, p.getId());

            if (manipulation.equals(Manipulation.DELETE))
                return;
        }

        stmt.setString(i++, p.getName());
        stmt.setString(i++, p.getContactPhone());
        stmt.setString(i++, p.getAddress());
        stmt.setString(i++, p.getEmail());

        if (manipulation.equals(Manipulation.UPDATE))
            stmt.setString(i, p.getId());
    }

    
public boolean producerExists(String producerId) {
    boolean exists = false;

    try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
         PreparedStatement stmt = conn.prepareStatement(Consts.CHECK_PRODUCER_EXISTS)) {

        stmt.setString(1, producerId);
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
