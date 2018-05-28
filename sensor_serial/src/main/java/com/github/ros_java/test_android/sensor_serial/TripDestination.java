package com.github.ros_java.test_android.sensor_serial;

import com.google.android.gms.maps.model.LatLng;

public class TripDestination {
    private boolean arrived;
    private LatLng location;

    public TripDestination() {
    }

    public TripDestination(LatLng location) {
        this.location = location;
        this.arrived = false;
    }

    public boolean isArrived() {
        return arrived;
    }
    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }
    public LatLng getLocation() {
        return location;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }
}
