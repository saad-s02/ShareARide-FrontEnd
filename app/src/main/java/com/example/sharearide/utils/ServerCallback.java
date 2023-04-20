package com.example.sharearide.utils;

import android.content.Context;

import org.json.JSONException;
import com.google.gson.JsonObject;

public interface ServerCallback {
    void onDone(JsonObject response);
    Context getContext();
}
