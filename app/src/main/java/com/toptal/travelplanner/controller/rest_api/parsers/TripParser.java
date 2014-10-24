package com.toptal.travelplanner.controller.rest_api.parsers;

import android.util.Log;

import com.toptal.travelplanner.model.Trip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by user on 24.10.2014.
 */
public class TripParser implements IParser<Trip> {

    private static final TripParser instance = new TripParser();

    private TripParser() {}

    public static TripParser getInstance() {
        return instance;
    }

    @Override
    public Trip parseResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);

            Trip trip = new Trip(json.getString(Trip.FIELD_DESTINATION),
                    new Date(json.getLong(Trip.FIELD_START_DATE)),
                    new Date(json.getLong(Trip.FIELD_END_DATE)),
                    json.getString(Trip.FIELD_COMMENT));
            return trip;
        } catch (JSONException e) {
            Log.e(TripParser.class.getCanonicalName(),"failed to parse a trip");
            e.printStackTrace();
            return null;
        }
    }
}
