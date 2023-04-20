package com.example.sharearide.utils;

import android.content.SharedPreferences;

public class Constants {
    private static final String LOCAL_URL = "http://10.0.2.2:5050/";
    private static final String PUBLIC_URL = "https://sharearide-backend-production.up.railway.app/";

    public final static String URL = LOCAL_URL;
    public final static String LOGIN = "login/";
    public final static String REGISTER = "registeraccount/";
    public final static String SCANQRCODE = "scanqrcode/";
    public final static String GETRIDEINFO = "getrideinfo/";
    public final static String GET_USER_INFO = "getuserinfo/";
    public final static String EDIT_PROFILE = "editprofile/";
    public final static String START_RIDE = "startride/";
    public final static String FINISH_RIDE = "finishride/";
    public final static String RATE_USER = "rateuser/";
    public final static String REQUEST_RIDE = "requestcarpool/";

    public final static String PREFERENCES = "preferences";
    public final static String DISCORD_TOKEN = "token";
    public final static String UID = "uid";
}
