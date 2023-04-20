package com.example.sharearide;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharearide.adapter.RecyclerviewAdapter;
import com.example.sharearide.utils.Constants;
import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity implements ServerCallback {

    private EditText departure, destination;
    private RecyclerView departure_list, destination_list;
    private String apiKey;

    MaterialButton search;
    EditText dep;
    EditText dest;
    Spinner rating;
    Spinner riders;
    ArrayList<String> aList = new ArrayList<>();
    String startId;
    String finishId;
    RecyclerviewAdapter rcAdapter;
    String fares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_page);

        // set toolbar format
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find your Carpool");

        apiKey = getResources().getString(R.string.apiKey);
        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        departure = (EditText) findViewById(R.id.departure);
        departure_list = (RecyclerView) findViewById(R.id.departure_list);
        LinearLayoutManager layoutManager_1 = new LinearLayoutManager(RequestActivity.this);
        departure_list.setLayoutManager(layoutManager_1);

        destination = (EditText) findViewById(R.id.destination);
        destination_list = (RecyclerView) findViewById(R.id.destination_list);
        LinearLayoutManager layoutManager_2 = new LinearLayoutManager(RequestActivity.this);
        destination_list.setLayoutManager(layoutManager_2);

        departure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                // Create a RectangularBounds object.
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(-33.880490, 151.184363),
                        new LatLng(-33.858754, 151.229596));
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry("CA")
                        .setOrigin(new LatLng(-33.8749937,151.2041382))
                        .setSessionToken(token)
                        .setQuery(editable.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(RequestActivity.this, departure, response.getAutocompletePredictions(), aList);
                    rcAdapter = recyclerviewAdapter;
                    departure_list.setAdapter(recyclerviewAdapter);
                    departure_list.setVisibility(View.VISIBLE);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        departure_list.setVisibility(View.GONE);
                    }
                });
            }
        });


        departure.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Hide the RecyclerView when the EditText loses focus
                    departure_list.setVisibility(View.GONE);
                }
            }
        });


        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                // Create a RectangularBounds object.
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(-33.880490, 151.184363),
                        new LatLng(-33.858754, 151.229596));
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry("CA")
                        .setOrigin(new LatLng(-33.8749937,151.2041382))
                        .setSessionToken(token)
                        .setQuery(editable.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    destination_list.setVisibility(View.VISIBLE);
                    RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(RequestActivity.this, destination, response.getAutocompletePredictions(), aList);
                    rcAdapter = recyclerviewAdapter;
                    destination_list.setAdapter(recyclerviewAdapter);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        destination_list.setVisibility(View.GONE);
                    }
                });
            }
        });

        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Hide the RecyclerView when the EditText loses focus
                    destination_list.setVisibility(View.GONE);
                }
            }
        });

        searched();
    }

    private void searched(){
        String[] arraySpinner = new String[] {
               "0", "1", "2", "3", "4"
        };

        String[] arraySpinner2 = new String[] {
                "1", "2", "3", "4"
        };

        search = findViewById(R.id.searchButt);
        dep = findViewById(R.id.departure);
        dest = findViewById(R.id.destination);

        rating = findViewById(R.id.ratingSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rating.setAdapter(adapter);

        riders = findViewById(R.id.maxRider);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riders.setAdapter(adapter2);

        search.setOnClickListener(view -> {
            startId = rcAdapter.getList().get(0);
            finishId = rcAdapter.getList().get(1);
            Log.d("Start place ID",startId);
            Log.d("End place ID",finishId);
            Log.d("Start Loc",dep.getText().toString());
            Log.d("End Loc",dest.getText().toString());
            Log.d("UID", getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.UID, null));
            requestRide(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.UID, null), dep.getText().toString(), startId, dest.getText().toString(), finishId, rating.getSelectedItem().toString(), riders.getSelectedItem().toString());

            Context context = getApplicationContext();
            Toast.makeText(context, "Found 1 Ride with fare of $40.00", Toast.LENGTH_LONG).show();
        });
    }

    public void setList(ArrayList<String> list){
        fares = list.toString();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestRide(String uID, String startLocation, String startID, String endLocation, String endID, String minRating, String maxRiders){
        QueryServer.requestRide(this, uID, startLocation, startID, endLocation, endID, minRating, maxRiders);
    }

    @Override
    public void onDone(JsonObject response) {

    }

    @Override
    public Context getContext() {
        return this;
    }
}
