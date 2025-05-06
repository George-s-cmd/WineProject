package entity;

import java.sql.Date;
import java.util.Objects;

public class RegularOrder extends Order {
    private String customerId; // Store customer ID instead of full Customer object

    // Constructor with Customer object
    public RegularOrder(String orderNumber, Date orderDate, String orderStatus, Date shipmentDate, String employeeId,
                        Customer mainCustomer) {
        super(orderNumber, orderDate, orderStatus, shipmentDate, employeeId);
        this.customerId = mainCustomer.getPersonId(); // Extract customer ID
    }

    // Constructor with customerId as String
    public RegularOrder(String orderNumber, Date orderDate, String orderStatus, Date shipmentDate, String employeeId,
                        String selectedCustomerId) {
        super(orderNumber, orderDate, orderStatus, shipmentDate, employeeId);
        this.customerId = selectedCustomerId; // Store customer ID directly
    }

    // Getter method for customer ID
    public String getCustomerId() {
        return customerId;
    }

    // Setter method for customer ID
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "RegularOrder [customerId=" + customerId + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegularOrder other = (RegularOrder) obj;
        return Objects.equals(customerId, other.customerId);
    }
}
