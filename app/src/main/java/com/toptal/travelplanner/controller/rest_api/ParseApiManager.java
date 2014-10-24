package com.toptal.travelplanner.controller.rest_api;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.toptal.travelplanner.controller.Controller;
import com.toptal.travelplanner.controller.rest_api.parsers.ActionSuccessParser;
import com.toptal.travelplanner.controller.rest_api.parsers.IParser;
import com.toptal.travelplanner.controller.rest_api.parsers.TripListParser;
import com.toptal.travelplanner.controller.rest_api.parsers.TripParser;
import com.toptal.travelplanner.model.Trip;
import com.toptal.travelplanner.ui.fragments.TripListFragment;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ParseApiManager implements IApiManager {

    private static final String TAG = ParseApiManager.class.getCanonicalName();

    private static final int CONNECTION_TIMEOUT = 5000;     // ms
    private static final String USER_AGENT = System.getProperty("http.agent");
    public static final String UTF8_ENCODING = "UTF-8";
    public static final String CP1251_ENCODING = "windows-1251";

    private static final String APP_ID = "Y7G2qM5MgI7JO25LXJz1bKTTy2y4ueA9Z5txJHPs";
    private static final String REST_IP_KEY = "3rlLHq1rN8P9yFrP6i7vBwq44EDaidiBeb2HBeaB";
    private static final String SERVER_PATH = "https://api.parse.com/1/";//1 is Parse.com API version
    private static final String OBJECTS_PATH = "classes/";

    public ParseApiManager() {

    }

    @Override
    public List<Trip> loadTrips() {
        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getCanonicalName();
        try {
            final HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
            httpParameters.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
            final HttpClient httpClient = new DefaultHttpClient(httpParameters);
            final HttpGet request = new HttpGet(url);
            request.addHeader("X-Parse-Application-Id", APP_ID);
            request.addHeader("X-Parse-REST-API-Key", REST_IP_KEY);

            final HttpResponse response = httpClient.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_UNAUTHORIZED:
                    final InputStream inputStream = response.getEntity().getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    final String res = br.readLine();
                    List<Trip> result = TripListParser.getInstance().parseResponse(res);
                    return result;
                default:
                    Log.e(TAG, "Invalid response code: " + statusCode + "\n  url = " + url);
                    return null;

            }
        } catch (IOException|IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean addTrip(Trip trip) {
        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getCanonicalName();
        final ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(Trip.FIELD_DESTINATION, trip.getDestination()));
        params.add(new BasicNameValuePair(Trip.FIELD_START_DATE, Long.toString(trip.getStart().getTime())));
        params.add(new BasicNameValuePair(Trip.FIELD_END_DATE, Long.toString(trip.getEnd().getTime())));
        params.add(new BasicNameValuePair(Trip.FIELD_COMMENT, trip.getComment()));

        try {
            final HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
            httpParameters.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
            final HttpClient httpClient = new DefaultHttpClient(httpParameters);
            final HttpPost request = new HttpPost(url);
            request.addHeader("X-Parse-Application-Id", APP_ID);
            request.addHeader("X-Parse-REST-API-Key", REST_IP_KEY);
            request.addHeader("content-type", "application/json");
            request.setEntity(new UrlEncodedFormEntity(params, UTF8_ENCODING));

            final HttpResponse response = httpClient.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_CREATED:
                case HttpStatus.SC_ACCEPTED:
                case HttpStatus.SC_UNAUTHORIZED:
                    return true;
                default:
                    Log.e(TAG, "Invalid response code: " + statusCode + "\n  url = " + url);
                    return false;
            }
        } catch (IOException|IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateTrip(Trip trip) {
        String objectId = Controller.getInstance().getDbHelper().getParseId(trip).getId();
        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getCanonicalName() + "/" + objectId;
        final ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(Trip.FIELD_DESTINATION, trip.getDestination()));
        params.add(new BasicNameValuePair(Trip.FIELD_START_DATE, Long.toString(trip.getStart().getTime())));
        params.add(new BasicNameValuePair(Trip.FIELD_END_DATE, Long.toString(trip.getEnd().getTime())));
        params.add(new BasicNameValuePair(Trip.FIELD_COMMENT, trip.getComment()));

        try {
            final HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
            httpParameters.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
            final HttpClient httpClient = new DefaultHttpClient(httpParameters);
            final HttpPut request = new HttpPut(url);
            request.addHeader("X-Parse-Application-Id", APP_ID);
            request.addHeader("X-Parse-REST-API-Key", REST_IP_KEY);
            request.addHeader("content-type", "application/json");
            request.setEntity(new UrlEncodedFormEntity(params, UTF8_ENCODING));

            final HttpResponse response = httpClient.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_CREATED:
                case HttpStatus.SC_ACCEPTED:
                case HttpStatus.SC_UNAUTHORIZED:
                    return true;
                default:
                    Log.e(TAG, "Invalid response code: " + statusCode + "\n  url = " + url);
                    return false;

            }
        } catch (IOException|IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteTrip(Trip trip) {
        String objectId = Controller.getInstance().getDbHelper().getParseId(trip).getId();
        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getCanonicalName() + "/" + objectId;

        try {
            final HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
            httpParameters.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
            final HttpClient httpClient = new DefaultHttpClient(httpParameters);
            final HttpDelete request = new HttpDelete(url);
            request.addHeader("X-Parse-Application-Id", APP_ID);
            request.addHeader("X-Parse-REST-API-Key", REST_IP_KEY);

            final HttpResponse response = httpClient.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_CREATED:
                case HttpStatus.SC_UNAUTHORIZED:
                    return true;
                default:
                    Log.e(TAG, "Invalid response code: " + statusCode + "\n  url = " + url);
                    return false;

            }
        } catch (IOException|IllegalStateException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }
}
