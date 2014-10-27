package com.toptal.travelplanner.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.toptal.travelplanner.controller.db.DatabaseHelper;
import com.toptal.travelplanner.controller.rest_api.IApiAware;
import com.toptal.travelplanner.controller.rest_api.ParseApiManager;
import com.toptal.travelplanner.controller.rest_api.IApiManager;
import com.toptal.travelplanner.model.Trip;

import java.util.List;

public class Controller {

    private static final String PREFERENCE_USER = "user";

    private static Controller instance;

    private IApiManager apiManager;
    private DatabaseHelper dbHelper;
    private Context appContext;

    private Controller() {
        apiManager = new ParseApiManager();
    }

    public static Controller getInstance() {
        if (instance == null) {
            synchronized (Controller.class) {
                if (instance == null) {
                    instance = new Controller();
                }
            }
        }
        return instance;
    }

    public void setApplicationContext(Context applicationContext) {
        appContext = applicationContext;
    }

    public String getUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return preferences.getString(PREFERENCE_USER, null);
    }

    public void setUser(String user) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        preferences.edit().putString(PREFERENCE_USER, user).commit();
    }

    public void dropCredentials() {
        setUser("");
    }

    public synchronized IApiManager getApiManager() {
        return apiManager;
    }

    public synchronized void runLoadTripsTask(final IApiAware<List<Trip>> apiAware) {
        AsyncTask<Void, Void, List<Trip>> task = new AsyncTask<Void, Void, List<Trip>>() {
            @Override
            protected List<Trip> doInBackground(Void... voids) {
                List<Trip> result = getApiManager().loadTrips();
                if (result==null) {
                    result = dbHelper.getTrips();
                }
                return result;
            }
            @Override
            protected void onPostExecute(List<Trip> result) {
                apiAware.onGetResponse(result);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized void runAddTripTask(final Trip trip, final IApiAware<Boolean> apiAware) {
        ApiTask task = new ApiTask(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return getApiManager().addTrip(trip);
            }
        };
        task.run();
    }

    public synchronized void runUpdateTripTask(final Trip trip, final IApiAware<Boolean> apiAware) {
        ApiTask task = new ApiTask(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return getApiManager().updateTrip(trip);
            }
        };
        task.run();
    }

    public synchronized void runDeleteTripTask(final Trip trip, final IApiAware<Boolean> apiAware) {
        ApiTask task = new ApiTask(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return getApiManager().deleteTrip(trip);
            }
        };
        task.run();
    }

    public synchronized void runSignupTask(final String user, final String password, final IApiAware<Boolean> apiAware) {
        Controller.getInstance().setUser(user);
        ApiTask task = new ApiTask(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Controller.getInstance().setUser(user);
                return getApiManager().signUp(user, password);
            }
        };
        task.run();
    }

    public synchronized void runLoginTask(final String user, final String password, final IApiAware<Boolean> apiAware) {
        Controller.getInstance().setUser(user);
        ApiTask task = new ApiTask(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return getApiManager().logIn(user, password);
            }
        };
        task.run();
    }



    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * call this in Activity.onCreate
     */
    public void registerDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * callthis in Activity.inDestroy
     */
    public void unregisterDbHelper() {
        this.dbHelper = null;
    }

    private abstract class ApiTask extends AsyncTask<Void, Void, Boolean> {

        private IApiAware<Boolean> mApiAware;

        ApiTask(IApiAware<Boolean> apiAware) {
            mApiAware = apiAware;
        }

        void run() {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mApiAware.onGetResponse(result);
        }

    }

}
