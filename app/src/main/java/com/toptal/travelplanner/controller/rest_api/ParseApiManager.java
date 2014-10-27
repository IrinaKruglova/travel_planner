package com.toptal.travelplanner.controller.rest_api;

import android.text.TextUtils;
import android.util.Log;

import com.toptal.travelplanner.controller.Controller;
import com.toptal.travelplanner.controller.rest_api.parsers.IParser;
import com.toptal.travelplanner.controller.rest_api.parsers.TripListParser;
import com.toptal.travelplanner.model.Trip;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ParseApiManager implements IApiManager {

    private static final String TAG = ParseApiManager.class.getCanonicalName();

    private static final int CONNECTION_TIMEOUT = 5000;     // ms
    //private static final String USER_AGENT = System.getProperty("http.agent");
    public static final String UTF8_ENCODING = "UTF-8";
    public static final String CP1251_ENCODING = "windows-1251";

    private static final String HEADER_APP_ID = "X-Parse-Application-Id";
    private static final String HEADER_REST_KEY = "X-Parse-REST-API-Key";
    private static final String HEADER_CONTENT = "Content-Type";
    private static final String APP_ID = "Y7G2qM5MgI7JO25LXJz1bKTTy2y4ueA9Z5txJHPs";
    private static final String REST_API_KEY = "3rlLHq1rN8P9yFrP6i7vBwq44EDaidiBeb2HBeaB";
    private static final String CONTENT_TYPE = "application/json";
    private static final String SERVER_PATH = "https://api.parse.com/1/";//1 is Parse.com API version
    private static final String OBJECTS_PATH = "classes/";
    private static final String USERS_PATH = "users";
    private static final String LOGIN_PATH = "login/";

    public ParseApiManager() {

    }

    @Override
    public List<Trip> loadTrips() {
        String user = Controller.getInstance().getUser();
        if (TextUtils.isEmpty(user)) {
            Log.e(TAG, "User is not logged in");
            return null;
        }

        JSONObject urlParams = new JSONObject();
        try {
            urlParams.put("username", user);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        String url;
        try {
            url = SERVER_PATH + OBJECTS_PATH + Trip.class.getSimpleName()+"?where=" +
                URLEncoder.encode(urlParams.toString(), UTF8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader(HEADER_APP_ID, APP_ID);
        httpRequest.addHeader(HEADER_REST_KEY, REST_API_KEY);

        final HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        final HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            final HttpResponse response = httpClient.execute(httpRequest);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                br.close();
                Log.d(TAG, "trips list response: " + sb.toString());
                return TripListParser.getInstance().parseResponse(sb.toString());
            }
            else {
                Log.e(TAG, "Invalid response code: " + responseCode + "\n  url = " + url);
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to load trips");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addTrip(Trip trip) {
        String user = Controller.getInstance().getUser();
        if (TextUtils.isEmpty(user)) {
            Log.e(TAG, "User is not logged in");
            return false;
        }

        JSONObject urlParams = new JSONObject();
        try {
            urlParams.put(Trip.FIELD_ID, trip.getId());
            urlParams.put(Trip.FIELD_DESTINATION, trip.getDestination());
            urlParams.put(Trip.FIELD_START_DATE, trip.getStart().getTime());
            urlParams.put(Trip.FIELD_END_DATE, trip.getEnd().getTime());
            urlParams.put(Trip.FIELD_COMMENT, trip.getComment());
            urlParams.put("username", user);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getSimpleName();

        HttpPost httpRequest = new HttpPost(url);
        httpRequest.addHeader(HEADER_APP_ID, APP_ID);
        httpRequest.addHeader(HEADER_REST_KEY, REST_API_KEY);

        Log.e(TAG, urlParams.toString());
        try {
            httpRequest.setEntity(new StringEntity(urlParams.toString(), UTF8_ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        final HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        final HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            final HttpResponse response = httpClient.execute(httpRequest);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_CREATED) {
                return true;
            }
            else {
                Log.e(TAG, "Invalid response code: " + responseCode + "\n  url = " + url);
                return false;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to add a trip");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTrip(Trip trip) {
        String objectId = findTripParseId(trip.getId());
        if (TextUtils.isEmpty(objectId)) {
            Log.e(TAG, "parse.com objectId not found, id="+trip.getId());
            return false;
        }
        String user = Controller.getInstance().getUser();
        if (TextUtils.isEmpty(user)) {
            Log.e(TAG, "User is not logged in");
            return false;
        }

        JSONObject urlParams = new JSONObject();
        try {
            urlParams.put(Trip.FIELD_DESTINATION, trip.getDestination());
            urlParams.put(Trip.FIELD_START_DATE, trip.getStart().getTime());
            urlParams.put(Trip.FIELD_END_DATE, trip.getEnd().getTime());
            urlParams.put(Trip.FIELD_COMMENT, trip.getComment());
            urlParams.put("username", user);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getSimpleName() + "/" + objectId;

        HttpPut httpRequest = new HttpPut(url);
        httpRequest.addHeader(HEADER_APP_ID, APP_ID);
        httpRequest.addHeader(HEADER_REST_KEY, REST_API_KEY);

        Log.e(TAG, urlParams.toString());
        try {
            httpRequest.setEntity(new StringEntity(urlParams.toString(), UTF8_ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        final HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        final HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            final HttpResponse response = httpClient.execute(httpRequest);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                return true;
            }
            else {
                Log.e(TAG, "Invalid response code: " + responseCode + "\n  url = " + url);
                return false;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to update a trip");
            e.printStackTrace();
            return false;
        }
        /*String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getSimpleName() + "/" + objectId;

        try {
            JSONObject tripJson = new JSONObject();
            tripJson.put(Trip.FIELD_DESTINATION, trip.getDestination());
            tripJson.put(Trip.FIELD_START_DATE, Long.toString(trip.getStart().getTime()));
            tripJson.put(Trip.FIELD_END_DATE, Long.toString(trip.getEnd().getTime()));
            tripJson.put(Trip.FIELD_COMMENT, trip.getComment());
            return request(url, "PUT", tripJson, HttpsURLConnection.HTTP_OK);
        } catch (IOException|JSONException e) {
            Log.e(TAG, "Failed to update trip");
            e.printStackTrace();
            return false;
        }*/
    }

    @Override
    public boolean deleteTrip(Trip trip) {
        String objectId = findTripParseId(trip.getId());
        if (TextUtils.isEmpty(objectId)) {
            Log.e(TAG, "parse.com objectId not found, id="+trip.getId());
            return false;
        }
        String user = Controller.getInstance().getUser();
        if (TextUtils.isEmpty(user)) {
            Log.e(TAG, "User is not logged in");
            return false;
        }

        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getSimpleName() + "/" + objectId;

        HttpDelete httpRequest = new HttpDelete(url);
        httpRequest.addHeader(HEADER_APP_ID, APP_ID);
        httpRequest.addHeader(HEADER_REST_KEY, REST_API_KEY);

        final HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        final HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            final HttpResponse response = httpClient.execute(httpRequest);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                return true;
            }
            else {
                Log.e(TAG, "Invalid response code: " + responseCode + "\n  url = " + url);
                return false;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to remove a trip");
            e.printStackTrace();
            return false;
        }

        /*String objectId = findTripParseId(trip.getId());
        String url = SERVER_PATH + OBJECTS_PATH + Trip.class.getSimpleName() + "/" + objectId;

        try {
            return request(url, "DELETE", null, HttpsURLConnection.HTTP_OK);
        } catch (IOException|JSONException e) {
            Log.e(TAG, "Failed to delete trip");
            e.printStackTrace();
            return false;
        }*/
    }

    @Override
    public boolean signUp(String user, String password) {
        try {
            JSONObject cred = new JSONObject();
            cred.put("username", user);
            cred.put("password", password);

            return request(SERVER_PATH + USERS_PATH, "POST", cred, HttpsURLConnection.HTTP_CREATED);
        } catch (IOException|JSONException e) {
            Log.e(TAG, "Failed to sign up");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean logIn(String user, String password) {
        String url = SERVER_PATH + LOGIN_PATH + "?username=" + user + "&password=" + password;
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader(HEADER_APP_ID, APP_ID);
        httpRequest.addHeader(HEADER_REST_KEY, REST_API_KEY);

        final HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        final HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            final HttpResponse response = httpClient.execute(httpRequest);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                return true;
            }
            else {
                Log.e(TAG, "Invalid response code: " + responseCode + "\n  url = " + url);
                return false;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to log in");
            e.printStackTrace();
            return false;
        }

//        String url;
//        try {
//            url = SERVER_PATH + LOGIN_PATH +
//                    "?username=" + URLEncoder.encode(user, UTF8_ENCODING) +
//                    "&password=" + URLEncoder.encode(password, UTF8_ENCODING);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        try {
//            return request(url, "GET", null, HttpsURLConnection.HTTP_OK);
//        } catch (IOException|JSONException e) {
//            Log.e(TAG, "Failed to log in");
//            e.printStackTrace();
//            return false;
//        }
    }

    /**
     *
     * @param url request url
     * @param requestMethod POST, GET, etc
     * @param json JSONObject with request params
     * @param responseCode expected response code
     * @throws IOException
     * @throws JSONException
     */
    private boolean request(String url, String requestMethod, JSONObject json, int responseCode) throws IOException, JSONException{
        URL urlObject = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) urlObject.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestProperty(HEADER_CONTENT, CONTENT_TYPE);
        con.setRequestProperty(HEADER_APP_ID, APP_ID);
        con.setRequestProperty(HEADER_REST_KEY, REST_API_KEY);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod(requestMethod);

        if (json != null) {
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(json.toString());
            wr.flush();
        }

        StringBuilder sb = new StringBuilder();
        int httpResult = con.getResponseCode();
        if(httpResult == responseCode){
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF8_ENCODING));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            br.close();
            Log.d(TAG, "response: " + sb.toString());
            return true;

        } else {
            Log.e(TAG, "Invalid response code: " + httpResult + "\n  url = " + url);
            Log.d(TAG, "response: " + con.getResponseMessage());
            return false;
        }
    }

    private String findTripParseId(int localId) {
        String user = Controller.getInstance().getUser();
        if (TextUtils.isEmpty(user)) {
            Log.e(TAG, "User is not logged in");
            return null;
        }
        JSONObject urlParams = new JSONObject();
        try {
            urlParams.put(Trip.FIELD_ID, localId);
            urlParams.put("username", user);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        String url;
        try {
            url = SERVER_PATH + OBJECTS_PATH + Trip.class.getSimpleName()+"?where=" +
                    URLEncoder.encode(urlParams.toString(), UTF8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.addHeader(HEADER_APP_ID, APP_ID);
        httpRequest.addHeader(HEADER_REST_KEY, REST_API_KEY);

        final HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        final HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            final HttpResponse response = httpClient.execute(httpRequest);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                br.close();
                try {
                    JSONObject json = new JSONObject(sb.toString());
                    JSONArray array = json.getJSONArray("results");
                    final int size = array.length();
                    if (size != 1)
                        return null;
                    JSONObject tripJson = new JSONObject(array.get(0).toString());
                    return tripJson.getString("objectId");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

            }
            else {
                Log.e(TAG, "Invalid response code: " + responseCode + "\n  url = " + url);
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to load trips");
            e.printStackTrace();
            return null;
        }
    }

}
