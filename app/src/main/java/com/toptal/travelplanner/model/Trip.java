package com.toptal.travelplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 20.10.2014.
 */
@DatabaseTable(tableName = "trips")
public class Trip implements Parcelable {

    public static final String FIELD_DESTINATION = "destination";
    public static final String FIELD_START_DATE = "start";
    public static final String FIELD_END_DATE = "end";
    public static final String FIELD_COMMENT = "comment";

    @DatabaseField(generatedId = true, columnName = "id")
    private int id;

    @DatabaseField(columnName = FIELD_DESTINATION, canBeNull = false)
    private String destination;

    @DatabaseField(columnName = FIELD_START_DATE, canBeNull = false)
    private Date start;

    @DatabaseField(columnName = FIELD_END_DATE, canBeNull = false)
    private Date end;

    @DatabaseField(columnName = FIELD_COMMENT)
    private String comment;

    public Trip() {
        destination = "";
        Calendar current = Calendar.getInstance();
        start = current.getTime();
        end = current.getTime();
        comment = "";
    }

    public Trip(String destination, Date start, Date end, String comment) {
        this.destination = destination;
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(destination);
        parcel.writeLong(start.getTime());
        parcel.writeLong(end.getTime());
        parcel.writeString(comment);
    }

    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    private Trip(Parcel in) {
        id = in.readInt();
        destination = in.readString();
        start = new Date(in.readLong());
        end = new Date(in.readLong());
        comment = in.readString();
    }
}
