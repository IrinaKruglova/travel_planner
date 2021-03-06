package com.toptal.travelplanner.ui.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.toptal.travelplanner.R;
import com.toptal.travelplanner.controller.Controller;
import com.toptal.travelplanner.controller.db.DatabaseHelper;
import com.toptal.travelplanner.controller.rest_api.IApiAware;
import com.toptal.travelplanner.model.Trip;
import com.toptal.travelplanner.ui.fragments.MonthPlanFragment;
import com.toptal.travelplanner.ui.fragments.NavigationDrawerFragment;
import com.toptal.travelplanner.ui.fragments.TripListFragment;
import com.toptal.travelplanner.ui.fragments.TripListHolder;


public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper>
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = MainActivity.class.getCanonicalName();
    public final static int REQUEST_CODE_ADD_TRIP = 1;
    public final static int REQUEST_CODE_EDIT_TRIP = 2;
    public final static String RESULT_TRIP = "new_trip";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Controller.getInstance().registerDbHelper(getHelper());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onDestroy() {
        Controller.getInstance().unregisterDbHelper();

        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment newFragment;
        switch (position) {
            case 0 : newFragment = new TripListFragment(); break;
            case 1 : newFragment = new MonthPlanFragment(); break;
            case 2 :
                launchSynchronization();
                return;
            case 3 : logout(); return;
            default: return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, newFragment)
                .commit();
    }

    private void launchSynchronization() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Synchronizing ...");
        progressDialog.setMessage("Synchronizing in progress ...");
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
        Controller.getInstance().runSynchronizeTripsTask(new IApiAware<Boolean>() {
            @Override
            public void onGetResponse(Boolean response) {
                mNavigationDrawerFragment.selectItem(0);
                progressDialog.dismiss();
                if (Boolean.FALSE.equals(response)) {
                    Toast.makeText(MainActivity.this, "Sync failed", Toast.LENGTH_SHORT).show();
                }
            }

            ;
        }, new IApiAware<Integer>() {
            @Override
            public void onGetResponse(Integer response) {
                progressDialog.setProgress(response);
            }
        });
    }


    public void onSectionAttached(String sectionTitle) {
       mTitle = sectionTitle;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intentAddTrip = new Intent(this, EditTripActivity.class);
            this.startActivityForResult(intentAddTrip, REQUEST_CODE_ADD_TRIP);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(this, getString(R.string.trip_save_fail), Toast.LENGTH_SHORT).show();
            return;
        }
        final Trip newTrip = data.getParcelableExtra(RESULT_TRIP);
        if (requestCode == REQUEST_CODE_ADD_TRIP) {
            int id = getHelper().addTrip(newTrip);
            newTrip.setId(id);
        }
        else {
            getHelper().updateTrip(newTrip);
        }
        updateTripsList();

        IApiAware<Boolean> apiAware = new IApiAware<Boolean>() {
            @Override
            public void onGetResponse(Boolean response) {
                if (!response) {
                    Toast.makeText(MainActivity.this, "Failed to save data on server", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (requestCode == REQUEST_CODE_ADD_TRIP) {
            Controller.getInstance().runAddTripTask(newTrip, apiAware);
        }
        else {
            Controller.getInstance().runUpdateTripTask(newTrip, apiAware);
        }
    }

    private void updateTripsList() {

        FragmentManager fm = getFragmentManager();

        TripListHolder tripsFragment = (TripListHolder) fm.findFragmentById(R.id.container);
        if (tripsFragment!=null)
            tripsFragment.updateList();

    }

    private void logout() {
        Controller.getInstance().dropCredentials();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

}
