package com.example.mybus.models;

public class Bus {

    String id;
    String driverId;
    String ownerId;

    public Bus(String id, String driverId, String ownerId) {
        this.id = id;
        this.driverId = driverId;
        this.ownerId = ownerId;
    }

    public Bus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
