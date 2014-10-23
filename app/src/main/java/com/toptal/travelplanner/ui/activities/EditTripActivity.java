package com.toptal.travelplanner.ui.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.toptal.travelplanner.R;
import com.toptal.travelplanner.model.Trip;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 21.10.2014.
 */
public class EditTripActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_TRIP="trip";

    private Trip mTrip;
    private EditText mDestinationView, mCommentView;
    private Button mStartView, mEndView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        mTrip = getIntent().getParcelableExtra(EXTRA_TRIP);
        mDestinationView = (EditText)findViewById(R.id.trip_destination);
        mCommentView = (EditText)findViewById(R.id.trip_comment);
        mStartView = (Button)findViewById(R.id.trip_start_btn);
        mEndView = (Button)findViewById(R.id.trip_end_btn);

        mStartView.setOnClickListener(this);
        mEndView.setOnClickListener(this);

        if (mTrip == null) {
            Date currentDate = Calendar.getInstance().getTime();
            mTrip = new Trip();
            mTrip.setStart(currentDate);
            mTrip.setEnd(currentDate);
        }
        else {
            mDestinationView.setText(mTrip.getDestination());
            mCommentView.setText(mTrip.getComment());
        }
    }

    @Override
    public void onClick(final View view) {
        Calendar initialCalendar = Calendar.getInstance();
        Date initialDate = view.getId() == mStartView.getId() ? mTrip.getStart() : mTrip.getEnd();
        initialCalendar.setTimeInMillis(initialDate.getTime());

        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(y, m, d);
                        if (view.getId() == mStartView.getId()) {
                            mTrip.setStart(calendar.getTime());
                        }
                        else {
                            mTrip.setEnd(calendar.getTime());
                        }
                    }
                },
                initialCalendar.get(Calendar.YEAR),
                initialCalendar.get(Calendar.MONTH),
                initialCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_save) {
            mTrip.setDestination(mDestinationView.getText().toString());
            mTrip.setComment(mCommentView.getText().toString());
            Intent data = new Intent();
            data.putExtra(MainActivity.RESULT_TRIP, mTrip);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
