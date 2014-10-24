package com.toptal.travelplanner.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "parse_ids")
public class ParseId {

    public static final String FIELD_OBJECT_ID = "ObjectId";
    public static final String FIELD_TRIP = "Trip";

    @DatabaseField(id = true, columnName = FIELD_OBJECT_ID)
    private String id;

    @DatabaseField(foreign = true, columnName = FIELD_TRIP)
    private Trip trip;

    public ParseId(String id, Trip trip) {
        this.id = id;
        this.trip = trip;
    }

    public String getId() {
        return id;
    }

    public Trip getTrip() {
        return trip;
    }
}
