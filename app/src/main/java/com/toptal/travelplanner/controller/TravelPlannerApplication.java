package com.toptal.travelplanner.controller;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by user on 23.10.2014.
 */
public class TravelPlannerApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this,
                "Y7G2qM5MgI7JO25LXJz1bKTTy2y4ueA9Z5txJHPs",
                "plb1pwhCEIk7JDqltpieEi5LQrgX3Gx1Un0BFbR3");
    }
}
