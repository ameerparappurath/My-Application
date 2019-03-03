package com.ameer.myapplication.models;

public class LocationDataModel {
    private String locationName, locationId;

    public LocationDataModel(String locationName, String locationId) {

        this.locationName = locationName;
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

}
