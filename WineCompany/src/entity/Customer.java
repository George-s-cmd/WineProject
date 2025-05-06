package entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;

public class Customer extends Person {

	private Date dayOfFirstContact;
	private String deliveryAddress;
	
	public Customer(String personId, String name, String phoneNumber, String email, Date dayOfFirstContact,
			String deliveryAddress) {
		super(personId, name, phoneNumber, email);
		this.dayOfFirstContact = dayOfFirstContact;
		this.deliveryAddress = deliveryAddress;
		
	}
	public Date getDayOfFirstContact() {
		return dayOfFirstContact;
	}
	public void setDayOfFirstContact(Date dayOfFirstContact) {
		this.dayOfFirstContact = dayOfFirstContact;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash( dayOfFirstContact, deliveryAddress);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		return  Objects.equals(dayOfFirstContact, other.dayOfFirstContact)
				&& Objects.equals(deliveryAddress, other.deliveryAddress);
	}
	@Override
	public String toString() {
		return "Customer [dayOfFirstContact=" + dayOfFirstContact + ", deliveryAddress=" + deliveryAddress
				
				+ "]";
	}
	
	
	
}
