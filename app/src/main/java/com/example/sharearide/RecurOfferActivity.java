package com.example.sharearide;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharearide.adapter.RecyclerviewAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RecurOfferActivity extends AppCompatActivity {

    private Button submit_btn;
    private TextView date_time_button;
    private EditText departure, destination;
    private RecyclerView departure_list, destination_list;
    ArrayList<String> aList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recur_offer_page);

        // set toolbar format
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        String apiKey = "AIzaSyCvOEcPKVyfbtE0WOA9ZD1R0X13gK9PNLc";
        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        departure = (EditText) findViewById(R.id.departure);
        departure_list = (RecyclerView) findViewById(R.id.departure_list);
        LinearLayoutManager layoutManager_1 = new LinearLayoutManager(RecurOfferActivity.this);
        departure_list.setLayoutManager(layoutManager_1);

        destination = (EditText) findViewById(R.id.destination);
        destination_list = (RecyclerView) findViewById(R.id.destination_list);
        LinearLayoutManager layoutManager_2 = new LinearLayoutManager(RecurOfferActivity.this);
        destination_list.setLayoutManager(layoutManager_2);

        departure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

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
                        .setOrigin(new LatLng(-33.8749937,151.2041382))
                        .setSessionToken(token)
                        .setQuery(editable.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(RecurOfferActivity.this, departure, response.getAutocompletePredictions(), aList);
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

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
                        .setOrigin(new LatLng(-33.8749937,151.2041382))
                        .setSessionToken(token)
                        .setQuery(editable.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    destination_list.setVisibility(View.VISIBLE);
                    RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(RecurOfferActivity.this, destination, response.getAutocompletePredictions(), aList);
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


        TextView date_time_button = findViewById(R.id.date_time_button);
        date_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current date and time
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Create date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(RecurOfferActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Set selected date to calendar object
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);

                        // Create time picker dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(RecurOfferActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                // Set selected time to calendar object
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);

                                // Format date and time
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm a", Locale.getDefault());
                                String dateTimeString = dateFormat.format(calendar.getTime());

                                // Set date and time on button and textview
                                date_time_button.setText(dateTimeString.toLowerCase());
                            }
                        }, hour, minute, false);
                        timePickerDialog.show();
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        AutoCompleteTextView exposedDropdown_1 = findViewById(R.id.frequency);
        String[] items_1 = new String[]{"Daily", "Weekly", "Monthly", "Every Weekday"};
        ArrayAdapter<String> adapter_1 = new ArrayAdapter<>(this, R.layout.dropdown_item, items_1);
        exposedDropdown_1.setAdapter(adapter_1);

        AutoCompleteTextView exposedDropdown_2 = findViewById(R.id.num_of_carpool);
        String[] items_2 = new String[]{"1", "2", "3", "4"};
        ArrayAdapter<String> adapter_2 = new ArrayAdapter<>(this, R.layout.dropdown_item, items_2);
        exposedDropdown_2.setAdapter(adapter_2);

        submit_btn = (Button) findViewById(R.id.submit);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecurOfferActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

//    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
////            mSelectedYear = year;
////            mSelectedMonth = monthOfYear;
////            mSelectedDay = dayOfMonth;
//        }
//    };
//
//    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
////            mSelectedHour = hourOfDay;
////            mSelectedMinute = minute;
//        }
//    };


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
