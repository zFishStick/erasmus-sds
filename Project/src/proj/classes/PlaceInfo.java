package proj.classes;

public class PlaceInfo {
	private String name;
	private String description;
	private String website;
	
	public PlaceInfo() {
		this.name = "Unknown Place";
		this.description = "No description available.";
		this.website = "N/A";
	}
	
	public PlaceInfo(String name, String description, String website) {
		this.name = name;
		this.description = description;
		this.website = website;
	}
}
