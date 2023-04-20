package com.example.sharearide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sharearide.utils.Constants;
import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements ServerCallback {

    EditText etemail;
    EditText etpassword;
    EditText etrepassword;
    EditText etfirstname;
    EditText etlastname;
    EditText etphone;
    EditText etaddress;
    EditText etdob;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etemail = findViewById(R.id.email);
        etpassword = findViewById(R.id.password);
        etrepassword = findViewById(R.id.repassword);
        etfirstname = findViewById(R.id.firstname);
        etlastname = findViewById(R.id.lastname);
        etphone = findViewById(R.id.phone);
        etaddress = findViewById(R.id.address);
        etdob = findViewById(R.id.dob);
        register = findViewById(R.id.signupbtn);
        register.setOnClickListener(view -> {
            if (!etpassword.getText().toString().equals(etrepassword.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "Please ensure passwords match!", Toast.LENGTH_LONG).show();
                etpassword.setText("");
                etrepassword.setText("");
                return;
            }
            else if (etpassword.getText().toString().length() == 0){
                Toast.makeText(RegisterActivity.this, "Please write a password!", Toast.LENGTH_LONG).show();
                return;
            }
            registerPost();
        });

        configureButton();
    }

    private void configureButton(){
        Button registerButton = (Button) findViewById(R.id.regloginbtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerPost(){
        QueryServer.register(this, etemail.getText().toString(), etpassword.getText().toString(),
                etfirstname.getText().toString(), etlastname.getText().toString(), etphone.getText().toString(),
                etaddress.getText().toString(), etdob.getText().toString());
    }

    @Override
    public void onDone(JsonObject response) {
        if (response.get("Message").toString().replaceAll("\"","").equals("Registration successful!")) {
            // Save users UID forever basically.
            getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putString(Constants.UID, response.get("UID").toString().replaceAll("\"",""))
                    .apply();
            loadMainActivity();
        } else {
            Toast.makeText(this, response.get("Message").toString().replaceAll("\"",""), Toast.LENGTH_LONG).show();
        }
    }

    public void loadMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }
}