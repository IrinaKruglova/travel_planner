package com.toptal.travelplanner.ui.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.HashMap;
import java.util.List;

public class MonthPlanFragment extends ListFragment {

    final static String DAYS_LEFT = "days_left";
    final static String DATES = "dates";
    final static String INSTANCE_STATE_KEY = "trips";

    private EditText mFilter;
    SimpleAdapter mAdapter;
    private ActionMode.Callback mActionModeCallback;
    private ActionMode mActionMode;
    private List<Trip> mTrips;
    private int mSelectedTripPosition;

    private final IApiAware<List<Trip>> mTripsLoadedApiAware = new IApiAware<List<Trip>>() {
        @Override
        public void onGetResponse(List<Trip> response) {
            mTrips = response;
            setupAdapter();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getString(R.string.title_section2));

        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.trip, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Trip trip = mTrips.get(mSelectedTripPosition);
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        editTrip(trip);
                        mode.finish();
                        return true;
                    case R.id.action_delete:
                        deleteTrip(trip);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Trip[] tripArray = new Trip[mTrips.size()];
        state.putParcelableArray(INSTANCE_STATE_KEY, mTrips.toArray(tripArray));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips, container, false);
        mFilter = (EditText)rootView.findViewById(R.id.filter);
        mTrips = new ArrayList<>();
        if (savedInstanceState!=null) {
            for (Parcelable parcelable : savedInstanceState.getParcelableArray(INSTANCE_STATE_KEY)) {
                mTrips.add((Trip) parcelable);
            }
        }
        setupAdapter();
        if (savedInstanceState==null) {
            Controller.getInstance().runLoadTripsTask(mTripsLoadedApiAware);
        }

        mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return rootView;
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        if (mActionMode != null) {
            return;
        }
        mSelectedTripPosition = position;
        mActionMode = getActivity().startActionMode(mActionModeCallback);
        view.setSelected(true);
    }

    public void updateList() {
        Controller.getInstance().runLoadTripsTask(mTripsLoadedApiAware);
    }

    private void setupAdapter() {
        List<HashMap<String,String>> tripMaps = new ArrayList<>();
        for (Trip trip : mTrips) {
            tripMaps.add(createDisplayedData(trip));
        }
        mAdapter = new SimpleAdapter(getActivity(),
                tripMaps,
                R.layout.trip_list_item,
                new String[] { Trip.FIELD_DESTINATION, Trip.FIELD_COMMENT,
                        DATES, DAYS_LEFT},
                new int[] { android.R.id.text1, android.R.id.text2, R.id.dates, R.id.days_left});
        setListAdapter(mAdapter);
    }

    private HashMap<String, String> createDisplayedData(Trip trip) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Trip.FIELD_DESTINATION, trip.getDestination());
        map.put(Trip.FIELD_COMMENT, trip.getComment());
        StringBuilder sbDates = new StringBuilder();
        sbDates.append(Util.formatDate(trip.getStart()));
        sbDates.append(" - ");
        sbDates.append(Util.formatDate(trip.getEnd()));
        map.put(DATES, sbDates.toString());
        long days = Util.getDaysLeft(trip.getStart());
        if (days<0) {
            map.put(DAYS_LEFT, "");
        }
        else {
            StringBuilder sbDays = new StringBuilder();
            sbDays.append(days).append(days == 1 ? " day left" : " days left");
            map.put(DAYS_LEFT, sbDays.toString());
        }
        return map;
    }

    private void editTrip(Trip trip) {
        Intent intentEditTrip = new Intent(getActivity(), EditTripActivity.class);
        intentEditTrip.putExtra(EditTripActivity.EXTRA_TRIP, trip);
        getActivity().startActivityForResult(intentEditTrip, MainActivity.REQUEST_CODE_EDIT_TRIP);
    }

    private void deleteTrip(final Trip trip) {
        ((MainActivity)getActivity()).getHelper().deleteTrip(trip);
        Controller.getInstance().runDeleteTripTask(trip, new IApiAware<Boolean>() {
            @Override
            public void onGetResponse(Boolean response) {
                if (response) {
                    mTrips.remove(trip);
                    setupAdapter();
                    Toast.makeText(getActivity(), getString(R.string.trip_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to remove trip", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
