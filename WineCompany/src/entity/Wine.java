package entity;

import java.time.Year;
import java.util.Objects;

public class Wine {

    private String catalogNumber;
    private String producerId;
    private String name;
    private int productionYear;
    private double pricePerBottle;
    private String sweetnessLevel;
    private String description;
    private String productImage;
    private String wineTypeId;
	public Wine(String catalogNumber, String producerId, String name, int productionYear, double pricePerBottle,
			String sweetnessLevel, String description, String productImage, String wineTypeId) {
		super();
		this.catalogNumber = catalogNumber;
		this.producerId = producerId;
		this.name = name;
		this.productionYear = productionYear;
		this.pricePerBottle = pricePerBottle;
		this.sweetnessLevel = sweetnessLevel;
		this.description = description;
		this.productImage = productImage;
		this.wineTypeId = wineTypeId;
	}
	
	public String getCatalogNum() {
		return catalogNumber;
	}
	public void setCatalogNum(String catalogNum) {
		this.catalogNumber = catalogNum;
	}
	public String getProducerId() {
		return producerId;
	}
	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getProductionYear() {
		return productionYear;
	}
	

	public void setProductionYear(int productionYear) {
	    int currentYear = Year.now().getValue(); // Get the current year
	    if (productionYear > currentYear) {
	        throw new IllegalArgumentException("Production year cannot be in the future!");
	    }
	    if (productionYear < 1900) { // Adjust this limit based on realistic wine production years
	        throw new IllegalArgumentException("Production year is too old to be valid!");
	    }
	    this.productionYear = productionYear;
	}

	public double getPricePerBottle() {
		return pricePerBottle;
	}
	public void setPricePerBottle(double pricePerBottle) {
		this.pricePerBottle = pricePerBottle;
	}
	public String getSweetnessLevel() {
		return sweetnessLevel;
	}
	public void setSweetnessLevel(String sweetnessLevel) {
		this.sweetnessLevel = sweetnessLevel;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getProductImage() {
		return productImage;
	}
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	public String getWineTypeId() {
		return wineTypeId;
	}
	public void setWineTypeId(String wineTypeId) {
		this.wineTypeId = wineTypeId;
	}
	@Override
	public int hashCode() {
		return Objects.hash(catalogNumber, description, name, pricePerBottle, producerId, productImage, productionYear,
				sweetnessLevel, wineTypeId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wine other = (Wine) obj;
		return Objects.equals(catalogNumber, other.catalogNumber) && Objects.equals(description, other.description)
				&& Objects.equals(name, other.name)
				&& Double.doubleToLongBits(pricePerBottle) == Double.doubleToLongBits(other.pricePerBottle)
				&& Objects.equals(producerId, other.producerId) && Objects.equals(productImage, other.productImage)
				&& productionYear == other.productionYear && Objects.equals(sweetnessLevel, other.sweetnessLevel)
				&& Objects.equals(wineTypeId, other.wineTypeId);
	}
	@Override
	public String toString() {
	    return "Wine [catalogNumber=" + catalogNumber + 
	           ", producerId=" + producerId + 
	           ", name=" + name + 
	           ", productionYear=" + productionYear + 
	           ", pricePerBottle=" + pricePerBottle + 
	           ", sweetnessLevel=" + sweetnessLevel + 
	           ", description=" + description + 
	           ", productImage=" + productImage + 
	           ", wineTypeId=" + wineTypeId + "]";
	}

    
}
