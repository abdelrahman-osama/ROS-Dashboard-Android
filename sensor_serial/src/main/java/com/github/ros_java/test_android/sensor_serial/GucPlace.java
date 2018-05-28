package com.github.ros_java.test_android.sensor_serial;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ahmed on 27-Mar-18.
 */

public class GucPlace {
    private String name;
    private LatLng latLng;

    public GucPlace(String name, LatLng latLng){
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
