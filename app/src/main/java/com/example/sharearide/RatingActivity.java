package com.example.sharearide;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharearide.adapter.RatingAdapter;
import com.example.sharearide.utils.Constants;
import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.RatingCallback;
import com.example.sharearide.utils.ServerCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RatingActivity extends AppCompatActivity implements ServerCallback, RatingCallback {

    private TextView textview, fare_text;
    private String rideId;
    private RecyclerView rating;
    ArrayList<String> user_name = new ArrayList<>();
    private Map<String, String> users = new HashMap<>();
    private Map<String, Float> rating_result = new HashMap<>();
    private Button submit;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_page);

        Intent intent = getIntent();
        rideId = intent.getStringExtra("rideId");

        // set toolbar format
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_close_24);
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Rating");
        }

//        String id = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.UID, null);
        String id = "EebNJYoGbNR7GdFUxvRsipmTaIl1";
        Log.d(TAG, rideId);
        finishRide(rideId, id);

        submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<Map.Entry<String, Float>> it = rating_result.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Float> entry = it.next();
                    updateUserRating(entry.getKey(), entry.getValue().toString());
                    Log.d(TAG, "Sucessfully updated!");
                }
                Intent intent = new Intent(RatingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUserRating(String cuid, String rating) {
        QueryServer.rateUser(this, cuid, rating);
    }

    private void getUserInfo(String cuid) {
        QueryServer.getUserInfo(this, cuid);
    }

    private void finishRide(String rideid, String cuid) {
        QueryServer.finishRide(this, rideid, cuid);
    }

    public void onDone(JsonObject response) {
        if (response.has("Message")) {
            String message = response.get("Message").getAsString();
            String fare = response.get("Fare").getAsString();
            textview = (TextView) findViewById(R.id.textView);
            fare_text = (TextView) findViewById(R.id.fare_text);
            textview.setText(message);
            fare_text.setText("Your final fare is $ " + fare);

            JsonArray stringArrayJson = response.getAsJsonArray("OtherRiders");
            for (int i = 0; i < stringArrayJson.size(); i++) {
                getUserInfo(stringArrayJson.get(i).getAsString());
            }
        } else if (response.has("firstName")) {
            String username = response.get("firstName").getAsString() + " " + response.get("lastName").getAsString();
            users.put(username, response.get("cuid").getAsString());
            user_name.add(username);
        }
        rating = findViewById(R.id.rating_list);
        LinearLayoutManager layoutManager_1 = new LinearLayoutManager(RatingActivity.this);
        rating.setLayoutManager(layoutManager_1);
        RatingAdapter adapter = new RatingAdapter(user_name, this);
        rating.setAdapter(adapter);
    }

    @Override
    public void onRatingChanged(int position, float rating) {
        String userId = users.get(position);
        rating_result.put(users.get(userId), rating);
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
