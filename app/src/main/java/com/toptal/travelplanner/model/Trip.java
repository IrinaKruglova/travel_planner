package com.toptal.travelplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@DatabaseTable(tableName = "trips")
public class Trip implements Parcelable {

    public static final String FIELD_ID = "id";
    public static final String FIELD_DESTINATION = "destination";
    public static final String FIELD_START_DATE = "start";
    public static final String FIELD_END_DATE = "end";
    public static final String FIELD_COMMENT = "comment";

    @DatabaseField(id = true, columnName = FIELD_ID)
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
        id = createId();
        destination = "";
        Calendar current = Calendar.getInstance();
        start = current.getTime();
        end = current.getTime();
        comment = "";
    }

    public Trip(String destination, Date start, Date end, String comment) {
        id = createId();
        this.destination = destination;
        this.start = start;
        this.end = end;
        this.comment = comment;
    }

    public Trip(int id, String destination, Date start, Date end, String comment) {
        this(destination, start, end, comment);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    private static Random random = new Random();

    private int createId() {
        return (int)((Calendar.getInstance().getTime().getTime() + random.nextInt()) % 1000000007);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip = (Trip) o;

        if (id != trip.id) return false;
        if (comment != null ? !comment.equals(trip.comment) : trip.comment != null) return false;
        if (destination != null ? !destination.equals(trip.destination) : trip.destination != null)
            return false;
        if (end != null ? !end.equals(trip.end) : trip.end != null) return false;
        if (start != null ? !start.equals(trip.start) : trip.start != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}
