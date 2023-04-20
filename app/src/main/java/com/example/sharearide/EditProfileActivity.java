package com.example.sharearide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sharearide.utils.Constants;
import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.gson.JsonObject;

public class EditProfileActivity extends AppCompatActivity implements ServerCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        EditText etemail = findViewById(R.id.email);
        EditText etfirstname = findViewById(R.id.firstname);
        EditText etlastname = findViewById(R.id.lastname);
        EditText etphone = findViewById(R.id.phone);
        EditText etaddress = findViewById(R.id.address);

        Button submitButton = findViewById(R.id.submitbtn);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryServer.updateUserInfo(
                        EditProfileActivity.this,
                        getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).
                                getString(Constants.UID, null),
                        etemail.getText().toString(),
                        etfirstname.getText().toString(),
                        etlastname.getText().toString(),
                        etphone.getText().toString(),
                        etaddress.getText().toString()
                );
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDone(JsonObject response) {
        if (response.get("Message").toString().replaceAll("\"","").equals("Profile updated!")) {
            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
            finish();
        } else {
            Toast.makeText(this, response.get("Message").toString().replaceAll("\"",""), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}