package com.toptal.travelplanner.controller;

import android.os.AsyncTask;

import com.toptal.travelplanner.controller.db.DatabaseHelper;
import com.toptal.travelplanner.controller.rest_api.IApiAware;
import com.toptal.travelplanner.controller.rest_api.ParseApiManager;
import com.toptal.travelplanner.controller.rest_api.IApiManager;
import com.toptal.travelplanner.model.Trip;

import java.util.List;

/**
 * Created by user on 24.10.2014.
 */
public class Controller {

    private static Controller instance;
    private IApiManager apiManager;
    private DatabaseHelper dbHelper;

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

    public synchronized IApiManager getApiManager() {
        return apiManager;
    }

    public synchronized void runLoadTripsTask(final IApiAware<List<Trip>> apiAware) {
        ApiTask<Void, List<Trip>> task = new ApiTask<Void, List<Trip>>(apiAware) {
            @Override
            protected List<Trip> doInBackground(Void... voids) {
                List<Trip> result = getApiManager().loadTrips();
                if (result==null) {
                    result = dbHelper.getTrips();
                }
                return result;
            }
        };
        task.run();
    }

    public synchronized void runAddTripsTask(final Trip trip, final IApiAware<Boolean> apiAware) {
        ApiTask<Void, Boolean> task = new ApiTask<Void, Boolean>(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean result = getApiManager().addTrip(trip);

            }
        };
        task.run();
    }

    public synchronized void runUpdateTripsTask(final Trip trip, final IApiAware<Boolean> apiAware) {
        ApiTask<Void, Boolean> task = new ApiTask<Void, Boolean>(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return getApiManager().updateTrip(trip);
            }
        };
        task.run();
    }

    public synchronized void runDeleteTripsTask(final Trip trip, final IApiAware<Boolean> apiAware) {
        ApiTask<Void, Boolean> task = new ApiTask<Void, Boolean>(apiAware) {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return getApiManager().deleteTrip(trip);
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



    private abstract class ApiTask<Params, Result> extends AsyncTask<Params, Void, Result> {

        private IApiAware<Result> mApiAware;

        public ApiTask(IApiAware<Result> apiAware) {
            mApiAware = apiAware;
        }

        @Override
        protected void onPostExecute(Result result) {
            mApiAware.onGetResponse(result);
        }

        public void run() {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
