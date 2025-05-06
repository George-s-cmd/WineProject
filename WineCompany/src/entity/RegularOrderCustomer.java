package entity;

public class RegularOrderCustomer {
    private String regularOrderId;
    private String customerId;

    // Constructor
    public RegularOrderCustomer(String regularOrderId, String customerId) {
        this.regularOrderId = regularOrderId;
        this.customerId = customerId;
    }

    // Getters
    public String getRegularOrderId() {
        return regularOrderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    // Setters
    public void setRegularOrderId(String regularOrderId) {
        this.regularOrderId = regularOrderId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // toString method for debugging/logging
    @Override
    public String toString() {
        return "RegularOrderCustomer{" +
                "regularOrderId='" + regularOrderId + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
