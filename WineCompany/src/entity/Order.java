package entity;

import java.sql.Date;
import java.util.Objects;

public  class  Order {
	private String orderNumber;
	private Date orderDate;
	private String orderStatus;
	private Date shipmentDate;
	 private String employeeId;
	public Order(String orderNumber, Date orderDate, String orderStatus, Date shipmentDate, String employeeId) {
		super();
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.shipmentDate = shipmentDate;
		this.employeeId = employeeId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Date getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(Date shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	@Override
	public int hashCode() {
		return Objects.hash(employeeId, orderDate, orderNumber, orderStatus, shipmentDate);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(employeeId, other.employeeId) && Objects.equals(orderDate, other.orderDate)
				&& Objects.equals(orderNumber, other.orderNumber) && Objects.equals(orderStatus, other.orderStatus)
				&& Objects.equals(shipmentDate, other.shipmentDate);
	}
	@Override
	public String toString() {
		return "Order [orderNumber=" + orderNumber + ", orderDate=" + orderDate + ", orderStatus=" + orderStatus
				+ ", shipmentDate=" + shipmentDate + ", employeeId=" + employeeId + "]";
	}
	

}
