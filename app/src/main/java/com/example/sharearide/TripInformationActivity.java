package com.example.sharearide;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TripInformationActivity extends AppCompatActivity implements OnMapReadyCallback, ServerCallback {
    private GoogleMap mMap;
    private String apiKey;
    private GeoApiContext mGeoApiContext = null;
    private Button submit;
    private TextView departure, destination, stops, ETA, fare, distance;
    private Place origin, end;
    private String eta_text, fare_text, distance_text;
    private ArrayList<String> placeId = new ArrayList<>();
    private Map<String, Place> placeMap = new LinkedHashMap<>();
    private String rideId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tripinfo_page);

        Intent intent = getIntent();
        rideId = intent.getStringExtra("rideId");

        // set toolbar format
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_close_24);
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Trip Information");
        }

        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripInformationActivity.this, RatingActivity.class);
                intent.putExtra("rideId", rideId);
                startActivity(intent);
            }
        });
//        id.add("ChIJwSr3RKyELIgRHAFDYeoAnjk"); // mcmaster
//        id.add("ChIJHfVL0gw5K4gRvKTcAQhsK6w"); // terminal
//        id.add("ChIJmzrzi9Y0K4gRgXUc3sTY7RU"); // cn tower
        getRideInfo(rideId);
    }

    private void getRideInfo(String rideid) {
        QueryServer.getRideInfo(this, rideid);
    }

    public void onDone(JsonObject response) {
        eta_text = response.get("ETA").getAsString();
        fare_text = response.get("fare").getAsString();
        distance_text = response.get("distance").getAsString() + " km";

        placeId.add(response.get("startlocationid").getAsString());
        JsonArray stringArrayJson = response.getAsJsonArray("stops");
        for (int i = 0; i < stringArrayJson.size(); i++) {
            placeId.add(stringArrayJson.get(i).getAsString());
        }
        placeId.add(response.get("endlocationid").getAsString());
        retrievePlace(placeId);
    }

    private void retrievePlace(ArrayList<String> placeId) {
        apiKey = getResources().getString(R.string.apiKey);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

        for (String id : placeId) {
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(id, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                placeMap.put(id, place);
                Log.d(TAG, "Place found: " + place.getName());

                if (placeMap.size() == placeId.size()) {
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                    if (mGeoApiContext == null) {
                        mGeoApiContext = new GeoApiContext.Builder()
                                .apiKey(apiKey)
                                .build();
                    }
                }
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Place place : placeMap.values()) {
            mMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getName()));
        }

        origin = placeMap.get(placeId.get(0));
        end = placeMap.get(placeId.get(placeId.size() - 1));

        setText();
        setBound();

        for (int i = 0; i < placeId.size() - 1; i++) {
            calculateDirections(placeMap.get(placeId.get(i)).getLatLng(),
                    placeMap.get(placeId.get(i+1)).getLatLng());
        }
    }

    private void setText() {
        departure = (TextView) findViewById(R.id.departure);
        destination = (TextView) findViewById(R.id.destination);
        stops = (TextView) findViewById(R.id.stops);
        ETA = (TextView) findViewById(R.id.ETA);
        fare = (TextView) findViewById(R.id.fare);
        distance = (TextView) findViewById(R.id.distance);

        departure.setText("Departure: " + origin.getName());
        destination.setText("Destination: " + end.getName());
        ETA.setText("ETA: " + eta_text);
        fare.setText("Estimated Fare: $" + fare_text);
        distance.setText("Total Distance: " + distance_text);
        String stops_text = "Stops: ";
        for (int i = 0; i < placeId.size(); i++) {
            if (i == placeId.size() - 1) {
                stops_text += placeMap.get(placeId.get(i)).getName();
                break;
            }
            stops_text += placeMap.get(placeId.get(i)).getName() + " - ";
        }
        stops.setText(stops_text);
    }

    private void setBound() {
        CameraUpdate initialLocation = CameraUpdateFactory.newLatLngZoom(origin.getLatLng(), 10);
        mMap.moveCamera(initialLocation);

        // Calculate the bounds of the route and zoom in to fit the bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin.getLatLng());
        builder.include(end.getLatLng());
        LatLngBounds bounds = builder.build();
        CameraUpdate routeZoom = CameraUpdateFactory.newLatLngBounds(bounds, 120);
        mMap.animateCamera(routeZoom, 600, null);
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                double shortestDistance = Double.MAX_VALUE;
                DirectionsRoute shortestPolyline = null;
                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    double distance = route.legs[0].distance.inMeters;

                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        shortestPolyline = route;
                    }
                }

                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(shortestPolyline.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(TripInformationActivity.this, R.color.red));
                    polyline.setClickable(true);
                }
            }
        });
    }

    private void calculateDirections(LatLng mOrigin, LatLng mDestination){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                mDestination.latitude,
                mDestination.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mOrigin.latitude,
                        mOrigin.longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    public Context getContext() {
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
