package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.POI;

public class POISResponse {
    
    private List<POI> data;

    public List<POI> getData() { 
        return data; 
    }

    public void setData(List<POI> data) { 
        this.data = data; 
    }
}

