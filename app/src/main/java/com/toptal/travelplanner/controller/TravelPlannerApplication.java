package com.toptal.travelplanner.controller;

import android.app.Application;

public class TravelPlannerApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Controller.getInstance().setApplicationContext(getApplicationContext());
    }
}
