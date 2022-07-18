package com.example.landdegradationinfocollectionsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    ProgressDialog progressDialog;
    private EditText etName, etEmail, etPassword, etReenterPassword;
    private String name, email, password, reenterPassword;
        private String url = "http://10.0.0.145/LDDCapp_db/LDDCapp.php";
//    private String url = "http://14.139.123.73:9090/web/NBSS/php/mysql.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //below code is for hiding the title bar of app
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        //input box
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etReenterPassword = findViewById(R.id.etReenterPassword);

        name = email = password = reenterPassword = "";
    }

    //----------Register Button method-----------------------
    public void Register(View view) {

        // below is for progress dialog box
        //Initialinzing the progress Dialog
        progressDialog = new ProgressDialog(RegisterPage.this);
        //show Dialog
        progressDialog.show();
        //set Content View
        progressDialog.setContentView(R.layout.progress_dialog);
        //set transparent background
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        name = etName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        reenterPassword = etReenterPassword.getText().toString().trim();

        //-----------validations for empty input box--------------
        if (TextUtils.isEmpty(name)) {
            progressDialog.dismiss();
            etName.setError("Please enter username");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            progressDialog.dismiss();
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            progressDialog.dismiss();
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            progressDialog.dismiss();
            etPassword.setError("Enter a password");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(reenterPassword)) {
            Toast.makeText(this, "Password Mismatch", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!name.equals("") && !email.equals("") && !password.equals("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "?TYPE=USER_REGISTER&name=" + name + "&email=" + email + "&password=" + password, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if (TextUtils.equals(response, "1")) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterPage.this, "Registered Successfull", Toast.LENGTH_SHORT).show();
                            // on below line we are displaying a success toast message.
                            Intent intent = new Intent(RegisterPage.this, RegisterPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterPage.this, "Failed to register!!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Fields can not be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    //----------Go to Login page Button-----------------------
    public void moveToLogin(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}