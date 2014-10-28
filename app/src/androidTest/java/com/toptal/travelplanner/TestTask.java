package com.toptal.travelplanner;

import android.os.AsyncTask;
import android.util.Log;

import com.toptal.travelplanner.controller.rest_api.IApiAware;
import com.toptal.travelplanner.controller.rest_api.IApiManager;
import com.toptal.travelplanner.controller.rest_api.ParseApiManager;
import com.toptal.travelplanner.model.Trip;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestTask extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            IApiManager apiManager = new ParseApiManager("test_user");

            boolean oldUser = apiManager.logIn("test_user", "123");
            boolean newUser = apiManager.signUp("test_user", "123");

            check(oldUser&&!newUser || newUser&&!oldUser);

            Date now = Calendar.getInstance().getTime();
            final Trip trip1 = new Trip("Russia", now, now, "comment");

            apiManager.addTrip(trip1);
            List<Trip> trips = apiManager.loadTrips();
            check(trips.contains(trip1));

            trip1.setComment("new comment");

            apiManager.updateTrip(trip1);
            trips  = apiManager.loadTrips();
            int index = trips.indexOf(trip1);
            check("new comment".equals(trips.get(index).getComment()));

            Trip trip2 = new Trip("USA", now, now, "trip to US");
            apiManager.addTrip(trip2);
            trips = apiManager.loadTrips();
            check(trips.size()==2);
            check(trips.contains(trip1));
            check(trips.contains(trip2));

            apiManager.deleteTrip(trip1);

            trips = apiManager.loadTrips();
            check(!trips.contains(trip1));
            check(trips.contains(trip2));

            apiManager.deleteTrip(trip2);
            check(apiManager.loadTrips().size()==0);

            ApplicationTest.taskResult = true;
        }
        catch (Exception e) {

        }
        finally {
            ApplicationTest.taskCompleted = true;
        }

        return null;
    }

    private void check(boolean expression) throws Exception {
        if (!expression) throw new Exception();
    }

}
