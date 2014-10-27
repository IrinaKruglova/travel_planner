package com.toptal.travelplanner.ui.fragments;

import android.app.ListFragment;
import android.widget.SimpleAdapter;

import com.toptal.travelplanner.controller.Controller;
import com.toptal.travelplanner.controller.rest_api.IApiAware;
import com.toptal.travelplanner.model.Trip;

import java.util.List;

public abstract class TripListHolder extends ListFragment {

    protected SimpleAdapter mAdapter;
    protected List<Trip> mTrips;

    protected final IApiAware<List<Trip>> mTripsLoadedApiAware = new IApiAware<List<Trip>>() {
        @Override
        public void onGetResponse(List<Trip> response) {
            mTrips = response;
            if (getActivity()!=null)
                setupAdapter();
        }
    };

    public void updateList() {
        Controller.getInstance().getTripsFromDB(mTripsLoadedApiAware);
    }

    protected abstract void setupAdapter();
}
