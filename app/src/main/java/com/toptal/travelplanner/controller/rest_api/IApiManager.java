package com.toptal.travelplanner.controller.rest_api;

import com.toptal.travelplanner.model.Trip;

import java.util.List;

/**
 * Created by user on 23.10.2014.
 */
public interface IApiManager {

    public static final int CONNECTION_TIMEOUT = 9000;     // ms
    public static final int SOCKET_TIMEOUT = 15000;     // ms

    public List<Trip> getTrips();
    public void addTrip(Trip trip);
    public void updateTrip(Trip trip);
    public void deleteTrip(Trip trip);
}
