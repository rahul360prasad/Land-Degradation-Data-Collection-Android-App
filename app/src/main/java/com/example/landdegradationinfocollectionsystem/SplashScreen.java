package com.example.landdegradationinfocollectionsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.landdegradationinfocollectionsystem.service.Networkservice;

public class SplashScreen extends AppCompatActivity {

    public static final String BROADCAST = "checkinternet";
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST)) {
                if (intent.getStringExtra("online_status").equals("true")) {
                    Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                    Log.d("data", "true");
                } else {
                    Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
                    Log.d("data", "false");
                }
            }
        }
    };
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST);
        Intent serviceIntent = new Intent(this, Networkservice.class);
        startService(serviceIntent);

        if (Networkservice.isOnline(getApplicationContext())) {

            //splash screen image
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, 2500);
            Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "Internet is OFF", Toast.LENGTH_SHORT).show();
            customNetworkCheckingDailogbox();
        }

        //below code is for hiding the title bar of app
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
    }

    private void customNetworkCheckingDailogbox() {
        // creating custom dialog
        final Dialog dialog = new Dialog(SplashScreen.this);

        // setting content view to dialog
        dialog.setContentView(R.layout.no_internet_connection_layout);

        // getting reference of TextView
        TextView dialogButtonTryAgain = (TextView) dialog.findViewById(R.id.btnTryAgain);
        TextView dialogButtonExit = (TextView) dialog.findViewById(R.id.textViewExit);

        // click listener on Try Again button
        dialogButtonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //this will always start your activity as a new task
                startActivity(intent);
                //dismiss the dialog
                dialog.dismiss();

            }
        });

        // click listener on Exit button
        dialogButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                // and exit the exit
                dialog.dismiss();
                finishAffinity();
            }
        });
        // show the exit dialog
        dialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

}