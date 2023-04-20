package com.example.sharearide;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharearide.adapter.NotificationAdapter;
import com.example.sharearide.adapter.RecyclerviewAdapter;
import com.example.sharearide.utils.Constants;
import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements ServerCallback {

    private RecyclerView recyclerView;
    private Button submit_btn;
    private String rideId = "6jIo8cQf0C3wm569PvOj";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_page);

        // set toolbar format
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_close_24);
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Request Notifications");
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager_1 = new LinearLayoutManager(NotificationActivity.this);
        recyclerView.setLayoutManager(layoutManager_1);
        String[] myDataset = {"item 1", "item 2", "item 3", "item 4", "item 5"};
        List<String> myList = Arrays.asList(myDataset);
        NotificationAdapter recyclerviewAdapter = new NotificationAdapter(NotificationActivity.this, myList);
        recyclerView.setAdapter(recyclerviewAdapter);

        submit_btn = (Button) findViewById(R.id.submit);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.UID, null);
//                String id = "EebNJYoGbNR7GdFUxvRsipmTaIl1";
                startRide(rideId, id);
                Intent intent = new Intent(NotificationActivity.this, TripInformationActivity.class);
                intent.putExtra("rideId", rideId);
                startActivity(intent);
            }
        });

    }

    private void startRide(String rideid, String cuid) {
        QueryServer.startRide(this, rideid, cuid);
    }

    public void onDone(JsonObject response) {
        Log.d(TAG, response.get("Message").getAsString());
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
