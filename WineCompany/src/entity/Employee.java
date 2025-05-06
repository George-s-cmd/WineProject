package entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;

public class Employee extends Person {
    private String officeAddress;
    private Date employmentStartDate;
   
	public Employee(String personId, String name, String phoneNumber, String email, String officeAddress,
			Date employmentStartDate) {
		super(personId, name, phoneNumber, email);
		this.officeAddress = officeAddress;
		this.employmentStartDate = employmentStartDate;
		
	}
	public String getOfficeAddress() {
		return officeAddress;
	}
	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}
	public Date getEmploymentStartDate() {
		return employmentStartDate;
	}
	public void setEmploymentStartDate(Date employmentStartDate) {
		this.employmentStartDate = employmentStartDate;
	}
	
	@Override
	public String toString() {
		return "Employee [officeAddress=" + officeAddress + ", employmentStartDate=" + employmentStartDate
				 + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash( employmentStartDate, officeAddress);
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
		Employee other = (Employee) obj;
		return Objects.equals(employmentStartDate, other.employmentStartDate)
				&& Objects.equals(officeAddress, other.officeAddress);
	}

}
