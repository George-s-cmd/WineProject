package entity;

import java.util.Objects;

public class UrgentOrderWine {
	private String urgentOrderNumber;
	private String wineId;
	private int quantitiy;
	public UrgentOrderWine(String urgentOrderNumber, String wineId, int quantitiy) {
		super();
		this.urgentOrderNumber = urgentOrderNumber;
		this.wineId = wineId;
		this.quantitiy = quantitiy;
	}
	public String getUrgentOrderNumber() {
		return urgentOrderNumber;
	}
	public void setUrgentOrderNumber(String urgentOrderNumber) {
		this.urgentOrderNumber = urgentOrderNumber;
	}
	public String getWineId() {
		return wineId;
	}
	public void setWineId(String wineId) {
		this.wineId = wineId;
	}
	public int getQuantitiy() {
		return quantitiy;
	}
	public void setQuantitiy(int quantitiy) {
		this.quantitiy = quantitiy;
	}
	@Override
	public int hashCode() {
		return Objects.hash(quantitiy, urgentOrderNumber, wineId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UrgentOrderWine other = (UrgentOrderWine) obj;
		return quantitiy == other.quantitiy && Objects.equals(urgentOrderNumber, other.urgentOrderNumber)
				&& Objects.equals(wineId, other.wineId);
	}
	@Override
	public String toString() {
		return "UrgentOrderWine [urgentOrderNumber=" + urgentOrderNumber + ", wineId=" + wineId + ", quantitiy="
				+ quantitiy + "]";
	}
	
	

}
