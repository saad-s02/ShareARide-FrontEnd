package com.example.sharearide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sharearide.utils.Constants;
import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;


public class LoginActivity extends AppCompatActivity implements ServerCallback {
    EditText etemail;
    EditText etpassword;
    MaterialButton login;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (preferences.contains(Constants.UID)) {
            loadMainActivity();
        }

        etemail = findViewById(R.id.loginemail);
        etpassword = findViewById(R.id.loginpassword);
        login = findViewById(R.id.loginbtn);

        login.setOnClickListener(view -> {
            if (etemail.getText().toString().isEmpty() && etpassword.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both the values", Toast.LENGTH_SHORT).show();
                return;
            }
            loginPost(etemail.getText().toString(), etpassword.getText().toString());
        });

        configureRegisterButton();
    }

    private void configureRegisterButton() {
        Button loginButton = (Button) findViewById(R.id.switchregister);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

    }

    private void loginPost(String email, String password){
        QueryServer.login(this, email, password);
    }

    @Override
    public void onDone(JsonObject response) {
        if (response.get("Message").toString().replaceAll("\"","").equals("Login successful!")) {
            // Save users UID forever basically.
            preferences
                    .edit()
                    .putString(Constants.UID, response.get("UID").toString().replaceAll("\"",""))
                    .apply();
            loadMainActivity();
        } else {
            Toast.makeText(this, response.get("Message").toString().replaceAll("\"",""), Toast.LENGTH_LONG).show();
        }
    }

    public void loadMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }
}