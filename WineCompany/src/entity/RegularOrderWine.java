package entity;

import java.util.Objects;

public class RegularOrderWine {
	private String regularOrderNumber;
	private String wineId ;
	private int quantity;
	public RegularOrderWine(String regularOrderNumber, String wineId, int quantity) {
		super();
		this.regularOrderNumber = regularOrderNumber;
		this.wineId = wineId;
		this.quantity = quantity;
	}
	public String getRegularOrderNumber() {
		return regularOrderNumber;
	}
	public void setRegularOrderNumber(String regularOrderNumber) {
		this.regularOrderNumber = regularOrderNumber;
	}
	public String getWineId() {
		return wineId;
	}
	public void setWineId(String wineId) {
		this.wineId = wineId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	@Override
	public int hashCode() {
		return Objects.hash(quantity, regularOrderNumber, wineId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegularOrderWine other = (RegularOrderWine) obj;
		return quantity == other.quantity && Objects.equals(regularOrderNumber, other.regularOrderNumber)
				&& Objects.equals(wineId, other.wineId);
	}
	@Override
	public String toString() {
		return "RegularOrderWine [regularOrderNumber=" + regularOrderNumber + ", wineId=" + wineId + ", quantity="
				+ quantity + "]";
	}
	

}
