package com.toptal.travelplanner.controller.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.toptal.travelplanner.model.ParseId;
import com.toptal.travelplanner.model.Trip;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 21.10.2014.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getCanonicalName();

    private static final String DATABASE_NAME = "travelPlanner.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Trip, Integer> tripDao = null;
    private RuntimeExceptionDao<ParseId, Integer> parseIdDao = null;

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

        // inserting a sample trip
        Calendar current = Calendar.getInstance();
        Date start = current.getTime();
        current.add(Calendar.DAY_OF_YEAR, 1);
        Date end = current.getTime();
        Trip trip = new Trip("USA", start, end, "A sample trip to USA in 24 hours");
        addTrip(trip);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        //no upgrade yet
    }

    @Override
    public void close() {
        super.close();
        tripDao = null;
        parseIdDao = null;
    }

    public RuntimeExceptionDao<Trip, Integer> getTripDao() {
        if (tripDao == null) {
            tripDao = getRuntimeExceptionDao(Trip.class);
        }
        return tripDao;
    }

    public RuntimeExceptionDao<ParseId, Integer> getParseIdDao() {
        if (parseIdDao == null) {
            parseIdDao = getRuntimeExceptionDao(ParseId.class);
        }
        return parseIdDao;
    }

    /**
     * you should call "addParseId" for this trip as soon as you get response from Parse.com
     * @param trip
     */
    public void addTrip(Trip trip) {
        getTripDao().create(trip);
    }

    public void updateTrip(Trip trip) {
        getTripDao().update(trip);
    }

    public void deleteTrip(Trip trip) {
        getParseIdDao().delete(getParseId(trip));
        getTripDao().delete(trip);
    }

    public List<Trip> getTrips() {
        return getTripDao().queryForAll();
    }

    public ParseId getParseId(Trip trip) {
        List<ParseId> ids = getParseIdDao().queryForEq(ParseId.FIELD_TRIP, trip);
        if (ids.size()==0)
            return null;
        return ids.get(0);
    }

    public void addParseId(String id, Trip trip) {
        getParseIdDao().create(new ParseId(id,trip));
    }


}
