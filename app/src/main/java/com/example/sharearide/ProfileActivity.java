package com.example.sharearide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sharearide.utils.Constants;
import com.example.sharearide.utils.DiscordService;
import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.gson.JsonObject;

public class ProfileActivity extends AppCompatActivity implements ServerCallback {
    private String JS_SNIPPET = "javascript:(function()%7Bvar%20i%3Ddocument.createElement('iframe')%3Bdocument.body.appendChild(i)%3Balert(i.contentWindow.localStorage.token.slice(1,-1))%7D)()";

    private SharedPreferences preferences;

    RelativeLayout mainLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        QueryServer.getUserInfo(this, preferences.getString(Constants.UID, null));

        mainLayout = findViewById(R.id.rl_main);
        Button discordLogin = findViewById(R.id.btn_discord_login);
        Button discordLogout = findViewById(R.id.btn_discord_logout);
        LinearLayout linearLayoutDiscordTest = findViewById(R.id.ll_discord_test);

        if (preferences.contains(Constants.DISCORD_TOKEN)) {
            discordLogin.setVisibility(View.GONE);
            discordLogout.setVisibility(View.VISIBLE);
            linearLayoutDiscordTest.setVisibility(View.VISIBLE);
        }


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Settings");

        WebView webView = findViewById(R.id.discord_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.stopLoading();

                if (request.getUrl().toString().endsWith("/app")) {
                    view.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    view.loadUrl(JS_SNIPPET);
                    view.getSettings().setJavaScriptEnabled(false);

                    view.clearCache(true);
                    view.clearHistory();
                    view.clearFormData();
                    view.clearSslPreferences();

                    WebStorage.getInstance().deleteAllData();
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();

                    discordLogin.setVisibility(View.GONE);
                    discordLogout.setVisibility(View.VISIBLE);
                    linearLayoutDiscordTest.setVisibility(View.VISIBLE);

                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                preferences
                        .edit()
                        .putString(Constants.DISCORD_TOKEN, message)
                        .apply();
                return true;
            }
        });

        discordLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl("https://discord.com/login");
            }
        });

        Button connect = findViewById(R.id.btn_discord_connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DiscordService.class);
                intent.putExtra("Token", preferences.getString(Constants.DISCORD_TOKEN, null));
                intent.setAction("START_ACTIVITY_ACTION");
                startService(intent);
            }
        });

        Button disconnect = findViewById(R.id.btn_discord_disconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(v.getContext(), DiscordService.class));
            }
        });

        Button edit = findViewById(R.id.editbtn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        discordLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().remove(Constants.DISCORD_TOKEN).apply();
                finish();
            }
        });

        Button logout = findViewById(R.id.btn_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().remove(Constants.UID).apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDone(JsonObject response) {
        TextView email = findViewById(R.id.email);
        TextView firstname = findViewById(R.id.firstname);
        TextView lastname = findViewById(R.id.lastname);
        TextView phone = findViewById(R.id.phone);
        TextView address = findViewById(R.id.address);
        TextView dob = findViewById(R.id.dob);

        email.setText(response.get("email").toString().replaceAll("\"", ""));
        firstname.setText(response.get("firstName").toString().replaceAll("\"", ""));
        lastname.setText(response.get("lastName").toString().replaceAll("\"", ""));
        phone.setText(response.get("phoneNumber").toString().replaceAll("\"", ""));
        address.setText(response.get("address").toString().replaceAll("\"", ""));
        dob.setText(response.get("DOB").toString().replaceAll("\"", ""));

        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
