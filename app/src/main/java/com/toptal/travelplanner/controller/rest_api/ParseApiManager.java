package com.toptal.travelplanner.controller.rest_api;

import android.text.TextUtils;
import android.util.Log;

import com.toptal.travelplanner.controller.Controller;
import com.toptal.travelplanner.controller.rest_api.parsers.TripListParser;
import com.toptal.travelplanner.model.Trip;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

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
    private static final String CONTENT_JSON = "application/json";
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

        Request<List<Trip>> request = new Request<>("GET",
                OBJECTS_PATH + Trip.class.getSimpleName()+"?where=",
                HttpStatus.SC_OK,
                new RequestCallback<List<Trip>>() {
                    @Override
                    public List<Trip> onExecuted(InputStream inputStreamResponse) {
                        String response = streamToString(inputStreamResponse);
                        Log.d(TAG, "trips list response: " + response);
                        return TripListParser.getInstance().parseResponse(response);
                    }
                });

        try {
            request.putString("username", user);
            request.executeWithUrlParams();
            return request.getResult();
        } catch (JSONException|IOException e) {
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

        Request<Boolean> request = new Request<>("POST",
                OBJECTS_PATH + Trip.class.getSimpleName(),
                HttpStatus.SC_CREATED,
                new RequestCallback<Boolean>() {
                    @Override
                    public Boolean onExecuted(InputStream inputStreamResponse) {
                        return true;
                    }
                });

        try {
            request.putInt(Trip.FIELD_ID, trip.getId());
            request.putString(Trip.FIELD_DESTINATION, trip.getDestination());
            request.putLong(Trip.FIELD_START_DATE, trip.getStart().getTime());
            request.putLong(Trip.FIELD_END_DATE, trip.getEnd().getTime());
            request.putString(Trip.FIELD_COMMENT, trip.getComment());
            request.putString("username", user);

            request.executeWithJsonEntity();
            Boolean res = request.getResult();
            return res == null ? false : res;
        } catch (JSONException|IOException e) {
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

        Request<Boolean> request = new Request<>("PUT",
                OBJECTS_PATH + Trip.class.getSimpleName() + "/" + objectId,
                HttpStatus.SC_OK,
                new RequestCallback<Boolean>() {
                    @Override
                    public Boolean onExecuted(InputStream inputStreamResponse) {
                        return true;
                    }
                }
            );

        try {
            request.putString(Trip.FIELD_DESTINATION, trip.getDestination());
            request.putLong(Trip.FIELD_START_DATE, trip.getStart().getTime());
            request.putLong(Trip.FIELD_END_DATE, trip.getEnd().getTime());
            request.putString(Trip.FIELD_COMMENT, trip.getComment());
            request.putString("username", user);

            request.executeWithJsonEntity();
            Boolean res = request.getResult();
            return res == null ? false : res;
        } catch (JSONException|IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to update a trip");
            return false;
        }
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

        Request<Boolean> request = new Request<>("DELETE",
                OBJECTS_PATH + Trip.class.getSimpleName() + "/" + objectId,
                HttpStatus.SC_OK,
                new RequestCallback<Boolean>() {
                    @Override
                    public Boolean onExecuted(InputStream inputStreamResponse) {
                        return true;
                    }
                });

        try {
            request.execute();
            Boolean res = request.getResult();
            return res == null ? false : res;
        } catch (IOException e) {
            Log.e(TAG, "Failed to remove a trip");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean signUp(String user, String password) {
        Request<Boolean> request = new Request<>("POST",
                USERS_PATH,
                HttpStatus.SC_CREATED,
                new RequestCallback<Boolean>() {
                    @Override
                    public Boolean onExecuted(InputStream inputStreamResponse) {
                        return true;
                    }
                });
        try {
            request.putString("username", user);
            request.putString("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        try {
            request.executeWithJsonEntity();
            Boolean res = request.getResult();
            return res==null? false : res;
        } catch (IOException e) {
            Log.e(TAG, "Failed to sign up");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean logIn(String user, String password) {
        Request<Boolean> request = new Request<>("GET",
                LOGIN_PATH + "?username=" + user + "&password=" + password,
                HttpStatus.SC_OK,
                new RequestCallback<Boolean>() {
                    @Override
                    public Boolean onExecuted(InputStream inputStreamResponse) {
                        return true;
                    }
                });
        try {
            request.execute();
            Boolean res = request.getResult();
            return res==null? false : res;
        } catch (IOException e) {
            Log.e(TAG, "Failed to log in");
            e.printStackTrace();
            return false;
        }
    }

    private String findTripParseId(int localId) {
        String user = Controller.getInstance().getUser();
        if (TextUtils.isEmpty(user)) {
            Log.e(TAG, "User is not logged in");
            return null;
        }
        Request<String> request = new Request<>("GET",
                OBJECTS_PATH + Trip.class.getSimpleName()+"?where=",
                HttpStatus.SC_OK,
                new RequestCallback<String>() {
                    @Override
                    public String onExecuted(InputStream is) {
                        return findObjectId(is);
                    }
                });
        try {
            request.putInt(Trip.FIELD_ID, localId);
            request.putString("username", user);
            request.executeWithUrlParams();
            return request.getResult();
        } catch (JSONException|IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String findObjectId(InputStream is) {
        try {
            JSONObject json = new JSONObject(streamToString(is));
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

    private String streamToString(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    private interface RequestCallback<T> {
        public T onExecuted(InputStream inputStreamResponse);
    }

    private class Request<T> {

        HttpRequestBase requestBase;
        JSONObject data;
        String url;
        int expectedCode;
        RequestCallback<T> callback;
        T result;

        Request(String method, String endpoint, int expectedCode, RequestCallback<T> callback) {
            switch (method) {
                case "POST" : requestBase = new HttpPost(); break;
                case "PUT" : requestBase = new HttpPut(); break;
                case "DELETE" : requestBase = new HttpDelete(); break;
                default: requestBase = new HttpGet();
            }
            requestBase.addHeader(HEADER_APP_ID, APP_ID);
            requestBase.addHeader(HEADER_REST_KEY, REST_API_KEY);
            url = SERVER_PATH + endpoint;
            this.expectedCode = expectedCode;
            this.callback = callback;
        }


        public void executeWithJsonEntity() throws IOException {
            requestBase.addHeader(HEADER_CONTENT, CONTENT_JSON);
            if (requestBase instanceof HttpPost) {
                ((HttpPost) requestBase).setEntity(new StringEntity(data.toString(), UTF8_ENCODING));
            }
            else if (requestBase instanceof HttpPut) {
                ((HttpPut) requestBase).setEntity(new StringEntity(data.toString(), UTF8_ENCODING));
            }
            execute();
        }
        public void executeWithUrlParams() throws IOException {
            url = url + URLEncoder.encode(data.toString(), UTF8_ENCODING);
            execute();
        }

        public void execute() throws IOException {
            try {
                requestBase.setURI(new URI(url));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Invalid url " + url);
            }
            final HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
            final HttpClient httpClient = new DefaultHttpClient(httpParameters);
            final HttpResponse response = httpClient.execute(requestBase);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == expectedCode) {
                result = callback.onExecuted( response.getEntity().getContent() );
            }
            else {
                Log.e(TAG, "Invalid code " + responseCode + " for url " + url);
            }
        }

        public T getResult() {
            return result;
        }

        public void putString(String field, String s) throws JSONException {
            if (data == null) {
                data = new JSONObject();
            }
            data.put(field, s);
        }

        public void putInt(String field, int i) throws JSONException {
            if (data == null) {
                data = new JSONObject();
            }
            data.put(field, i);
        }

        public void putLong(String field, long l) throws JSONException {
            if (data == null) {
                data = new JSONObject();
            }
            data.put(field, l);
        }

    }

}
