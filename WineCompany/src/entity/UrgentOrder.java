package entity;

import java.sql.Date;
import java.util.Objects;

import javax.swing.JOptionPane;

public class UrgentOrder extends Order {
    private int priority;
    private Date expectedDelivery;
    private String customerId;

    public UrgentOrder(String orderNumber, Date orderDate, String orderStatus, Date shipmentDate, 
                       String employeeId, int priority, Date expectedDelivery, String customerId) {
        super(orderNumber, orderDate, orderStatus, shipmentDate, employeeId);
        this.priority = priority;
        this.expectedDelivery = expectedDelivery;
        this.customerId = customerId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority < 1 || priority > 5) {
            JOptionPane.showMessageDialog(null, 
                    "Priority must be between 1 and 5!", 
                    "Invalid Priority", 
                    JOptionPane.WARNING_MESSAGE);
            return; // Prevents updating if the value is invalid
        }
        this.priority = priority;
    }


    public Date getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(Date expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), priority, expectedDelivery, customerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        UrgentOrder other = (UrgentOrder) obj;
        return priority == other.priority &&
               Objects.equals(expectedDelivery, other.expectedDelivery) &&
               Objects.equals(customerId, other.customerId);
    }

    @Override
    public String toString() {
        return "UrgentOrder [priority=" + priority + ", expectedDelivery=" + expectedDelivery +
                ", customerId=" + customerId + "]";
    }
}
