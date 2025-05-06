package entity;

public class WineTypeOccasion {
    private String wineTypeSerialNumber;
    private String occasionName;

    // Constructor
    public WineTypeOccasion(String wineTypeSerialNumber, String occasionName) {
        this.wineTypeSerialNumber = wineTypeSerialNumber;
        this.occasionName = occasionName;
    }

    // Getter for wineTypeSerialNumber
    public String getWineTypeSerialNumber() {
        return wineTypeSerialNumber;
    }

    // Getter for occasionName
    public String getOccasionName() {
        return occasionName;
    }

    // Setter for wineTypeSerialNumber
    public void setWineTypeSerialNumber(String wineTypeSerialNumber) {
        this.wineTypeSerialNumber = wineTypeSerialNumber;
    }

    // Setter for occasionName
    public void setOccasionName(String occasionName) {
        this.occasionName = occasionName;
    }
}


