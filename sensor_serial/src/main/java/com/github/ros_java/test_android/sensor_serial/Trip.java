package com.github.ros_java.test_android.sensor_serial;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip{
    private String id;

    private String event;

    private Date requestTime;
    private Date carArriveTime;
    private Date startTime;
    private Date endTime;
    private Date cancelTime;
    private Date carArriveFinal;

    private LatLng pickupLocation;
    private List<TripDestination> destinations;

    private String carID;
    private String carFcmToken;
    private String userID;
    private String userFcmToken;
    private String tabletFcmToken;

    public Trip() {
    }

    public Trip(String carID, String userID, LatLng pickupLocation, List<TripDestination> destinations) {
        this.requestTime = new Date();
        this.carID = carID;
        this.userID = userID;
        this.pickupLocation = pickupLocation;
        this.destinations = destinations;
    }

    public String getId() {
        return id;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public LatLng getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(LatLng pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public List<TripDestination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<TripDestination> destinations) {
        this.destinations = destinations;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCarFcmToken() {
        return carFcmToken;
    }

    public void setCarFcmToken(String carFcmToken) {
        this.carFcmToken = carFcmToken;
    }

    public String getUserFcmToken() {
        return userFcmToken;
    }

    public void setUserFcmToken(String userFcmToken) {
        this.userFcmToken = userFcmToken;
    }

    public String getTabletFcmToken() {
        return tabletFcmToken;
    }

    public void setTabletFcmToken(String tabletFcmToken) {
        this.tabletFcmToken = tabletFcmToken;
    }

    public Date getCarArriveTime() {
        return carArriveTime;
    }

    public void setCarArriveTime(Date carArriveTime) {
        this.carArriveTime = carArriveTime;
    }

    public Date getCarArriveFinal() {
        return carArriveFinal;
    }

    public void setCarArriveFinal(Date carArriveFinal) {
        this.carArriveFinal = carArriveFinal;
    }

    public static Trip toTrip(RequestTrip requestTrip){
        Trip trip = new Trip();
        trip.setPickupLocation(requestTrip.getPickupLocation());
        trip.setUserFcmToken(requestTrip.getUserFcmToken());
        trip.setUserID(requestTrip.getUserID());
        trip.setCarID(requestTrip.getCarID());
        List<TripDestination> destinations = new ArrayList<>();
        for(LatLng latLng : requestTrip.getDestinations()){
            destinations.add(new TripDestination(latLng));
        }
        trip.setDestinations(destinations);
        return trip;
    }

    public static RequestTrip toRequestTrip(Trip trip){
        RequestTrip requestTrip = new RequestTrip();
        requestTrip.setPickupLocation(trip.getPickupLocation());
        List<LatLng> destinations = new ArrayList<>();
        for(TripDestination tripDestination : trip.getDestinations()){
            destinations.add(tripDestination.getLocation());
        }
        requestTrip.setDestinations(destinations);
        return requestTrip;
    }

    public String getEvent() {
        return event;
    }
}
