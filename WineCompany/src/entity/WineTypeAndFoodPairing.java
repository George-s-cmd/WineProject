package entity;

import java.util.Objects;

public class WineTypeAndFoodPairing {
    private String wineTypeSerialNumber;
    private String dishName;

    // Constructor
    public WineTypeAndFoodPairing(String wineTypeSerialNumber, String dishName) {
        this.wineTypeSerialNumber = wineTypeSerialNumber;
        this.dishName = dishName;
    }

    // Getters and Setters
    public String getWineTypeSerialNumber() {
        return wineTypeSerialNumber;
    }

    public void setWineTypeSerialNumber(String wineTypeSerialNumber) {
        this.wineTypeSerialNumber = wineTypeSerialNumber;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    // Override equals() and hashCode() for object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WineTypeAndFoodPairing that = (WineTypeAndFoodPairing) o;
        return Objects.equals(wineTypeSerialNumber, that.wineTypeSerialNumber) &&
               Objects.equals(dishName, that.dishName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wineTypeSerialNumber, dishName);
    }

    // Override toString() for easy debugging
    @Override
    public String toString() {
        return "WineTypeAndFoodPairing{" +
                "wineTypeSerialNumber='" + wineTypeSerialNumber + '\'' +
                ", dishName='" + dishName + '\'' +
                '}';
    }
}
