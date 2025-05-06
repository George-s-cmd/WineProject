package entity;

import java.util.Objects;

public class StockedWine {
    private String catalogNumber;  // ✅ Updated (previously wineId)
    private String producerId;  
    private String storageLocationId;
    private int quantity; 

    public StockedWine(String catalogNumber, String producerId, String storageLocationId, int quantity) {
        this.catalogNumber = catalogNumber;
        this.producerId = producerId;
        this.storageLocationId = storageLocationId;
        this.quantity = quantity;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public String getProducerId() {
        return producerId;
    }

    public String getStorageLocationId() {
        return storageLocationId;
    }

    public int getQuantity() {
        return quantity;
    }

	@Override
	public int hashCode() {
		return Objects.hash(catalogNumber, producerId, quantity, storageLocationId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockedWine other = (StockedWine) obj;
		return Objects.equals(catalogNumber, other.catalogNumber) && Objects.equals(producerId, other.producerId)
				&& quantity == other.quantity && Objects.equals(storageLocationId, other.storageLocationId);
	}

	@Override
	public String toString() {
		return "StockedWine [catalogNumber=" + catalogNumber + ", producerId=" + producerId + ", storageLocationId="
				+ storageLocationId + ", quantity=" + quantity + "]";
	}
}

	

   
