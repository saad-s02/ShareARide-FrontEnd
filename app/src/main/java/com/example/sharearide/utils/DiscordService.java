package com.example.sharearide.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.my.kizzyrpc.KizzyRPC;
import com.my.kizzyrpc.model.Activity;
import com.my.kizzyrpc.model.Assets;
import com.my.kizzyrpc.model.Metadata;
import com.my.kizzyrpc.model.Timestamps;

import java.util.Arrays;

public class DiscordService extends Service {
    private KizzyRPC kizzyRPC;
    private String token;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        token = intent.getStringExtra("Token");
        kizzyRPC = new KizzyRPC(token);
        if (intent.getAction().equals("START_ACTIVITY_ACTION"))
        {
            kizzyRPC.setActivity(
                    new Activity(
                            "ShareARide",
                            "Offering a Ride",
                            "Carpooling",
                            0,
                            new Timestamps(null, System.currentTimeMillis()),
                            new Assets(
                                    "mp:attachments/966488590550986772/1091897337082818560/1024x1024.jpg",
                                    null,
                                    "Share a Ride",
                                    null
                            ),
                            Arrays.asList("Get the App!", "Join!"),
                            new Metadata(Arrays.asList(
                                    "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                                    "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                            )),
                            "962990036020756480"
                    ), "online", System.currentTimeMillis());
        }
        notification();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        kizzyRPC.closeRPC();
        super.onDestroy();
    }

    private void notification() {
        NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(new NotificationChannel("Discord RPC", "Background Service", NotificationManager.IMPORTANCE_LOW));
        Notification.Builder builder = new Notification.Builder(this, "Discord RPC");
        //builder.setSmallIcon(1500001);
        builder.setContentText("Rpc Running");
        builder.setUsesChronometer(true);
        this.startForeground(11234, builder.build());
    }
}
