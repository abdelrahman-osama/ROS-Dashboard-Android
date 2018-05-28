package com.github.ros_java.test_android.sensor_serial;

import com.google.android.gms.maps.model.LatLng;

public class Car {

    private String id;
	
	private String carID;
	private String tabletFcmToken;
	
	private LatLng latLng;
    
	private boolean available;
	
	private Trip currentTrip;
	
	public Car() {
		
	}
	
    public Car(String carID, String tabletFcmToken, LatLng latlng) {
    	this.carID = carID;
    	this.latLng = latlng;
    	this.available = true;
	}
    
    public Car(String carID, String tabletFcmToken, LatLng latlng, boolean available) {
    	this.carID = carID;
    	this.latLng = latlng;
    	this.available = available;
    	this.tabletFcmToken = tabletFcmToken;
	}
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getcarID() {
		return carID;
	}
	public void setcarID(String carID) {
		this.carID = carID;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Trip getCurrentTrip() {
		return currentTrip;
	}

	public void setCurrentTrip(Trip currentTrip) {
		this.currentTrip = currentTrip;
	}

	public String getTabletFcmToken() {
		return tabletFcmToken;
	}

	public void setTabletFcmToken(String tabletFcmToken) {
		this.tabletFcmToken = tabletFcmToken;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
	
	
}
