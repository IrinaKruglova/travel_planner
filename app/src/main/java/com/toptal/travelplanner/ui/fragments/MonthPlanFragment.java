package com.toptal.travelplanner.ui.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.toptal.travelplanner.R;
import com.toptal.travelplanner.controller.Controller;
import com.toptal.travelplanner.controller.Util;
import com.toptal.travelplanner.controller.rest_api.IApiAware;
import com.toptal.travelplanner.model.Trip;
import com.toptal.travelplanner.ui.activities.EditTripActivity;
import com.toptal.travelplanner.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MonthPlanFragment extends TripListHolder {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getString(R.string.title_section2));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips, container, false);
        rootView.findViewById(R.id.filter).setVisibility(View.GONE);
        mTrips = new ArrayList<>();
        setupAdapter();
        if (savedInstanceState==null) {
            Controller.getInstance().getTripsFromDB(mTripsLoadedApiAware);
        }

        return rootView;
    }

    @Override
    protected void setupAdapter() {
        List<HashMap<String,String>> dayMaps = new ArrayList<>(30);
        Calendar calendar = getStartOfDay();
        for (int i = 0; i<30; i++) {
            HashMap<String, String> map = new HashMap<>();
            Date d1 = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date d2 = calendar.getTime();

            map.put("date", Util.formatDate(d1));

            String trips = "";
            for (Trip trip : mTrips) {
                if (areIntersecting(trip.getStart().getTime(), trip.getEnd().getTime(),
                        d1.getTime(), d2.getTime())) {
                    if (!TextUtils.isEmpty(trips))
                        trips += ", ";
                    trips += trip.getDestination();
                }
            }
            map.put("trip", trips);

            dayMaps.add(map);
        }
        mAdapter = new SimpleAdapter(getActivity(),
                dayMaps,
                R.layout.calendar_item,
                new String[] { "date", "trip"},
                new int[] { android.R.id.text1, android.R.id.text2});
        setListAdapter(mAdapter);
    }

    private boolean areIntersecting(long x, long y, long a, long b) {
        return (a>=x && a<=y || b>=x && b<=y || x>=a && x<=b || y>=a && y<=b);
    }

    private Calendar getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar;
    }
}
