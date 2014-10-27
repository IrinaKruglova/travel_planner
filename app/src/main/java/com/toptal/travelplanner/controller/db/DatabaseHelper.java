package com.toptal.travelplanner.controller.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.toptal.travelplanner.model.Trip;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getCanonicalName();

    private static final String DATABASE_NAME = "travelPlanner.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Trip, Integer> tripDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate");
            TableUtils.createTable(connectionSource, Trip.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        //no upgrade yet
    }

    @Override
    public void close() {
        super.close();
        tripDao = null;
    }

    public RuntimeExceptionDao<Trip, Integer> getTripDao() {
        if (tripDao == null) {
            tripDao = getRuntimeExceptionDao(Trip.class);
        }
        return tripDao;
    }

    /**
     *
     * @param trip new trip
     * @return id of created trip
     */
    public int addTrip(Trip trip) {
        RuntimeExceptionDao<Trip, Integer> dao = getTripDao();
        dao.create(trip);
        HashMap<String, Object> query = new HashMap<>();
        query.put(Trip.FIELD_DESTINATION, trip.getDestination());
        query.put(Trip.FIELD_START_DATE, trip.getStart());
        query.put(Trip.FIELD_END_DATE, trip.getEnd());
        query.put(Trip.FIELD_COMMENT, trip.getComment());
        return dao.queryForFieldValues(query).get(0).getId();
    }

    public void updateTrip(Trip trip) {
        getTripDao().update(trip);
    }

    public void deleteTrip(Trip trip) {
        getTripDao().delete(trip);
    }

    public List<Trip> getTrips() {
        return getTripDao().queryForAll();
    }

}
