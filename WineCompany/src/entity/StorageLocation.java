package entity;

import java.util.Objects;

public class StorageLocation {
	private String storageNumber ;
	private String name;
	public StorageLocation(String storageNumber, String name) {
		super();
		this.storageNumber = storageNumber;
		this.name = name;
	}
	public String getStorageNumber() {
		return storageNumber;
	}
	public void setStorageNumber(String storageNumber) {
		this.storageNumber = storageNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "StorageLocation [storageNumber=" + storageNumber + ", name=" + name + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(name, storageNumber);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StorageLocation other = (StorageLocation) obj;
		return Objects.equals(name, other.name) && Objects.equals(storageNumber, other.storageNumber);
	}
	

}
