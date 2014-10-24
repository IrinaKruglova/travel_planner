package com.toptal.travelplanner.controller.rest_api.parsers;

import android.util.Log;

import com.toptal.travelplanner.model.Trip;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 24.10.2014.
 */
public class TripListParser implements IParser<List<Trip>> {

    private static final TripListParser instance = new TripListParser();

    private TripListParser() {}

    public static TripListParser getInstance() {
        return instance;
    }

    @Override
    public List<Trip> parseResponse(String response) {
        TripParser tripParser = TripParser.getInstance();

        try {
            JSONArray json = new JSONArray(response);
            final int size = json.length();
            List<Trip> trips = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                trips.add(tripParser.parseResponse(json.get(i).toString()));
            }
            return trips;
        } catch (JSONException e) {
            Log.e(TripParser.class.getCanonicalName(), "failed to parse trips");
            e.printStackTrace();
            return null;
        }
    }
}
