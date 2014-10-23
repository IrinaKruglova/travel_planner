package com.toptal.travelplanner.controller.rest_api;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.toptal.travelplanner.model.Trip;

import java.util.List;

/**
 * Created by user on 23.10.2014.
 */
public class ApiManager implements IApiManager {

    public interface Callback {
        public void onDone();
        public void onFail(String errorMessage);
    }

    private Callback mCallback;

    public ApiManager(Callback callback) {
        mCallback = callback;
    }

    @Override
    public List<Trip> getTrips() {
        return null;
    }

    @Override
    public void addTrip(Trip trip) {
        tripToParseObject(trip).saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onDone(e);
            }
        });
    }

    @Override
    public void updateTrip(Trip trip) {
        tripToParseObject(trip).saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onDone(e);
            }
        });
    }

    @Override
    public void deleteTrip(Trip trip) {
        tripToParseObject(trip).deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                onDone(e);
            }
        });
    }

    private void onDone(Exception e) {
        if (e==null) {
            mCallback.onDone();
        }
        else {
            mCallback.onFail(e.getMessage());
        }
    }

    private ParseObject tripToParseObject(Trip trip) {
        ParseObject object = new ParseObject("Trip");
        object.put("destination", trip.getDestination());
        object.put("start", trip.getStart());
        object.put("end", trip.getEnd());
        object.put("comment", trip.getComment());
        return object;
    }
}
