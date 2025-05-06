package entity;

import java.util.ArrayList;
import java.util.Objects;

public class WineType {
	private String serialNumber;
	private String name;
	
	public WineType(String serialNumber, String name) {
		super();
		this.serialNumber = serialNumber;
		this.name = name;
		
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, serialNumber);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WineType other = (WineType) obj;
		return Objects.equals(name, other.name) && Objects.equals(serialNumber, other.serialNumber)
				;
	}
	

}
