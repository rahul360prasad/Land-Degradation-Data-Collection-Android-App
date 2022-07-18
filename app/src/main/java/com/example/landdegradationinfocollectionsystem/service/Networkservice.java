package com.example.landdegradationinfocollectionsystem.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.example.landdegradationinfocollectionsystem.MainActivity;

public class Networkservice extends Service {

    Handler handler = new Handler();
    private Runnable period = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(period, 1 * 1000000 - SystemClock.elapsedRealtime() % 1000000);

            Intent intent = new Intent();
            intent.setAction(MainActivity.BROADCAST);
            intent.putExtra("online_status", "" + isOnline(Networkservice.this));
            sendBroadcast(intent);
        }
    };

    //check the wifi and mobile data for internet
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cm.getActiveNetworkInfo();
        if (nf != null && nf.isConnectedOrConnecting()
                && (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null
                && cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null
                && cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(period);
        return START_STICKY;
    }
}
