package com.sds2.classes.coordinates;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private Double latitude;
    private Double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location that)) return false;
        return Objects.equals(latitude, that.latitude)
            && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
