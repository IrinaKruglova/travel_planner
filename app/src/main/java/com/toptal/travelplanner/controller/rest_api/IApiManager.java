package com.toptal.travelplanner.controller.rest_api;

import com.toptal.travelplanner.model.Trip;

import java.util.List;

public interface IApiManager {

    /**
     *
     * @return true if success
     */
    public boolean signUp(String user, String password);

    /**
     *
     * @return true if success
     */
    public boolean logIn(String user, String password);


    /**
     *
     * @return list of trips if success, else null
     */
    public List<Trip> loadTrips();

    /**
     * @return true if success
     */
    public boolean addTrip(Trip trip);

    /**
     * @return true if success
     */
    public boolean updateTrip(Trip trip);

    /**
     * @return true if success
     */
    public boolean deleteTrip(Trip trip);
}
