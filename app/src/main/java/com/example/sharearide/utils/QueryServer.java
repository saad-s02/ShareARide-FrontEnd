package com.example.sharearide.utils;

import static com.google.maps.android.Context.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sharearide.RequestActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class QueryServer {
    private static final String BASE_URL = Constants.URL;
    public static ArrayList<String> fares = new ArrayList<>();
    private QueryServer() {}

    public static void login(ServerCallback serverCallback, String email, String password) {
        String url = BASE_URL + Constants.LOGIN;
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }

    public static void register(ServerCallback serverCallback, String email, String password, String firstname,
                                String lastname, String phonenumber, String address, String DOB) {
        String url = BASE_URL + Constants.REGISTER;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("firstname", firstname);
            jsonBody.put("lastname", lastname);
            jsonBody.put("phonenumber", phonenumber);
            jsonBody.put("address", address);
            jsonBody.put("DOB", DOB);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }

    public static void getUserInfo(ServerCallback serverCallback, String UID) {
        String url = BASE_URL + Constants.GET_USER_INFO;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UID", UID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }

    public static void updateUserInfo(ServerCallback serverCallback, String UID, String email, String firstname,
                                String lastname, String phonenumber, String address) {
        String url = BASE_URL + Constants.EDIT_PROFILE;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UID", UID);
            jsonBody.put("firstName", firstname);
            jsonBody.put("lastName", lastname);
            jsonBody.put("phoneNumber", phonenumber);
            jsonBody.put("address", address);
            jsonBody.put("DiscordAuthToken", "");
            jsonBody.put("email", email);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }

    private static void connectToServer(ServerCallback serverCallback, String requestBody, String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(serverCallback.getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);

                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

                serverCallback.onDone(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return super.parseNetworkResponse(response);
            }
        };

        requestQueue.add(stringRequest);

    }

    private static void connectServerJSONArray(ServerCallback serverCallback, String requestBody, String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(serverCallback.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);

                JSONArray jsonarray = null;
                try {
                    jsonarray = new JSONArray(response);
                    if (jsonarray.length() == 0){
                        Log.i("VOLLEY", "No valid rides");
                    }
                    else{
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            String fare = jsonobject.getString("fare");
                            fares.add(fare);
                            Log.d("Fare", fare);

                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                RequestActivity rc = new RequestActivity();
                rc.setList(fares);
                Log.d("Fare", fares.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return super.parseNetworkResponse(response);
            }
        };

        requestQueue.add(stringRequest);

    }


    public static void getRideInfo(ServerCallback serverCallback, String rideId) {
        String url = BASE_URL + Constants.GETRIDEINFO;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("RideID", rideId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }


    public static void scanQRCode(ServerCallback serverCallback, String qrcode) {
        String url = BASE_URL + Constants.SCANQRCODE;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("qrcode", qrcode);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }

    public static void requestRide(ServerCallback serverCallback, String uID, String startLocation, String startID, String endLocation, String endID, String minRating, String maxRiders){
        String url = BASE_URL + Constants.REQUEST_RIDE;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("requester", uID);

            jsonBody.put("start_location", startLocation);
            jsonBody.put("start_location_id", startID);

            jsonBody.put("end_location", endLocation);
            jsonBody.put("end_location_id", endID);

            jsonBody.put("min_rating", minRating);
            jsonBody.put("max_riders", maxRiders);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectServerJSONArray(serverCallback, jsonBody.toString(), url);
    }

    public static void startRide(ServerCallback serverCallback, String rideId, String cuid) {
        String url = BASE_URL + Constants.START_RIDE;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("RideID", rideId);
            jsonBody.put("CUID", cuid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }

    public static void finishRide(ServerCallback serverCallback, String rideId, String cuid) {
        String url = BASE_URL + Constants.FINISH_RIDE;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("RideID", rideId);
            jsonBody.put("CUID", cuid);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }

    public static void rateUser(ServerCallback serverCallback, String cuid, String rating) {
        String url = BASE_URL + Constants.RATE_USER;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UID", cuid);
            jsonBody.put("rating", rating);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        connectToServer(serverCallback, jsonBody.toString(), url);
    }
}
