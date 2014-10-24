package com.toptal.travelplanner.controller.rest_api;

import com.toptal.travelplanner.model.Trip;

import java.util.List;

public interface IApiManager {

    public List<Trip> loadTrips();

    public boolean addTrip(Trip trip);

    public boolean updateTrip(Trip trip);

    public boolean deleteTrip(Trip trip);
}
