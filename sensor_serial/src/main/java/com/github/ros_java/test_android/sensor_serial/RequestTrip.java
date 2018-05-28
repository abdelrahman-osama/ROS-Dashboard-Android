package com.github.ros_java.test_android.sensor_serial;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;


public class RequestTrip {
    private String id;

    private Date requestTime;
    private Date carArriveTime;
    private Date startTime;
    private Date endTime;
    private Date cancelTime;

    private LatLng pickupLocation;
    private List<LatLng> destinations;

    private String carID;
    private String carFcmToken;
    private String userID;
    private String userFcmToken;
    private String tabletFcmToken;

    public RequestTrip() {
    }

    public RequestTrip(String carID, String userID, LatLng pickupLocation, List<LatLng> destinations) {
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

    public List<LatLng> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<LatLng> destinations) {
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
}
