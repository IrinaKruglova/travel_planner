package com.toptal.travelplanner.controller.rest_api.parsers;

import android.util.Log;

import com.toptal.travelplanner.model.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("results");
            final int size = array.length();
            List<Trip> trips = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                Trip trip = tripParser.parseResponse(array.get(i).toString());
                if (trip == null) {
                    throw new JSONException("Filed to parse trip " + i);
                }
                trips.add(tripParser.parseResponse(array.get(i).toString()));
            }
            return trips;
        } catch (JSONException e) {
            Log.e(TripParser.class.getCanonicalName(), "Failed to parse trips: " + response);
            e.printStackTrace();
            return null;
        }
    }
}
