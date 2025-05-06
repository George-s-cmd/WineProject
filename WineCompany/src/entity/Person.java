package entity;

import java.util.Objects;

public abstract class Person {
	private String personId;
	private String name;
	private String phoneNumber;
	private String email;
	public Person(String personId, String name, String phoneNumber, String email) {
		super();
		this.personId = personId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public int hashCode() {
		return Objects.hash(email, name, personId, phoneNumber);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		return Objects.equals(email, other.email) && Objects.equals(name, other.name)
				&& Objects.equals(personId, other.personId) && Objects.equals(phoneNumber, other.phoneNumber);
	}
	@Override
	public String toString() {
		return "Person [personId=" + personId + ", name=" + name + ", phoneNumber=" + phoneNumber + ", email=" + email
				+ "]";
	}
	
	

}
