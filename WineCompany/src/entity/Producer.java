package entity;

import java.util.ArrayList;
import java.util.Objects;

public class Producer {
    private String id;
    private String name;
    private String contactPhone;
    private String address;
    private String email;
    private ArrayList<Wine> wineList;
	public Producer(String id, String name, String contactPhone, String address, String email) {
		super();
		this.id = id;
		this.name = name;
		this.contactPhone = contactPhone;
		this.address = address;
		this.email = email;
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public ArrayList<Wine> getWineList() {
		return wineList;
	}
	public void setWineList(ArrayList<Wine> wineList) {
		this.wineList = wineList;
	}
	@Override
	public int hashCode() {
		return Objects.hash(address, contactPhone, email, id, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Producer other = (Producer) obj;
		return Objects.equals(address, other.address) && Objects.equals(contactPhone, other.contactPhone)
				&& Objects.equals(email, other.email) && Objects.equals(id, other.id)
				 && Objects.equals(name, other.name);
	}
	@Override
	public String toString() {
		return "Producer [id=" + id + ", name=" + name + ", contactPhone=" + contactPhone + ", address=" + address
				+ ", email=" + email +  "]";
	}

    
}
