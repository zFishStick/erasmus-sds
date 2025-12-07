package com.sds2.dto;

import java.util.Objects;

public record CityDTO(String name, String country, double latitude, double longitude) {
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CityDTO(String otherName, String otherCountry, double otherLatitude, double otherLongitude))) return false;
		return Objects.equals(name, otherName)
				&& Objects.equals(country, otherCountry)
				&& Double.compare(latitude, otherLatitude) == 0
				&& Double.compare(longitude, otherLongitude) == 0;
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(name, country);
		result = 31 * result + Double.hashCode(latitude);
		result = 31 * result + Double.hashCode(longitude);
		return result;
	}

	@Override
	public String toString() {
		return "CityDTO[name=" + name + ", country=" + country + ", coordinates=(" + latitude + ", " + longitude + ")]";
	}
}