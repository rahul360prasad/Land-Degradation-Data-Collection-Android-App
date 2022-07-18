package com.example.landdegradationinfocollectionsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AboutApp extends AppCompatActivity {
    //-----------hardware back button----------------
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AboutApp.this, HomePage.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);


        //------------------HIDING THE ACTION BAR----------------------
        try {
            getSupportActionBar().setTitle("");
        } catch (NullPointerException e) {
        }
    }

    //-----------HOME ICON on action bar-------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //---------onclicking go to home dashboard-------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_logo:
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}