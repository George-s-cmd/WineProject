package entity;

import java.util.Objects;

public class Occasion {
	private String occasionName;
	private String description;
	private String season;
	private String location;
	public Occasion(String occasionName, String description, String season, String location) {
		super();
		this.occasionName = occasionName;
		this.description = description;
		this.season = season;
		this.location = location;
	}
	public String getOccasionName() {
		return occasionName;
	}
	public void setOccasionName(String occasionName) {
		this.occasionName = occasionName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public int hashCode() {
		return Objects.hash(description, location, occasionName, season);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Occasion other = (Occasion) obj;
		return Objects.equals(description, other.description) && Objects.equals(location, other.location)
				&& Objects.equals(occasionName, other.occasionName) && Objects.equals(season, other.season);
	}
	@Override
	public String toString() {
		return "Occasion [occasionName=" + occasionName + ", description=" + description + ", season=" + season
				+ ", location=" + location + "]";
	}
	

}
