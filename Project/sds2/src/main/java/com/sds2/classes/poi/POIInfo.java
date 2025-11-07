package com.sds2.classes.poi;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;

@Embeddable
public class POIInfo {
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    protected POIInfo() {}

    public POIInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
