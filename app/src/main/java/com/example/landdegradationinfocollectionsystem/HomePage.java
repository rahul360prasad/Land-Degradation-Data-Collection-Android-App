package com.example.landdegradationinfocollectionsystem;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HomePage extends AppCompatActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    //creating shared preference name and also creating key name
    private static final String SHARED_PRE_NAME = "mypref";
    private static final String homepg_url = "http://10.0.0.145/LDDCapp_db/LDDCrecordsInsertion.php";
    //------------LOCATION CODE------------
    private static final int REQUEST_LOCATION = 1;
    //taking user id from login/register
    SharedPreferences sharedPreferencesId;
    CardView porjRegCard, dataCollectionCard, soilDataReport, aboutApp, logoutCard, soilDataUpdate;
    TextView usernamedisplay;
    //--------------SHARED PREFERENCES----------------
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    ImageView imgView_1, imgView_2;
    String encodeImgStr_1, encodeImgStr_2;
    Bitmap bitmapImg_1, bitmapImg_2;
    Button btnGetLocation;
    LocationManager locationManager;
    TextView lbl_latitude, lbl_longitude;
    //---------holding the images on image view------------
    //------------------------------IMAGE 1--------------------------
    ActivityResultLauncher<Intent> someActivityResultLauncherEst = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent imgOneData = result.getData();
                        Uri imageUri;
                        if (imgOneData != null) {
                            imageUri = imgOneData.getData();
                            InputStream inputStream;
                            try {
                                inputStream = getContentResolver().openInputStream(imageUri);
                                bitmapImg_1 = BitmapFactory.decodeStream(inputStream);
                                imgView_1.setImageBitmap(bitmapImg_1);
                                encodeBitmapImg_first(bitmapImg_1);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
    //-------------------------------------IMAGE 2-----------------------------------
    ActivityResultLauncher<Intent> someActivityResultLauncherWst = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent wstData = result.getData();
                        Uri imageUri;
                        if (wstData != null) {
                            imageUri = wstData.getData();
                            InputStream inputStream;
                            try {
                                inputStream = getContentResolver().openInputStream(imageUri);
                                bitmapImg_2 = BitmapFactory.decodeStream(inputStream);
                                imgView_2.setImageBitmap(bitmapImg_2);
                                encodeBitmapImg_second(bitmapImg_2);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
    private int userid;
    private String username;
    // defining spinner for state, district, tehsil, block, topo250k, topo50k
    private AutoCompleteTextView stateSpinner, districtSpinner, tehsilSpinner;
    private TextInputLayout edtVillagename, edtSlope, edtLandcover, edtCroptype, edtLanddegradationCatg,
            edtSeverity, edtExtent;

    //declaring variable for storing selected state, district, tehsil, block, topo250k, topo50k
    private String selectedState, selectedDistrict, selectedTehsil;

    // creating a strings for storing our values from edittext fields.
    private String villagename, latitude, longitude, slope, landCover, cropType,
            landDegradationCategory, severity, extent;

    //defining and declaring array adapter for state, district, tehsil, block, topo250k, topo50k
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter, tehsilAdapter;

    //----------------------------------POPUP EXIT APP Msg-----------------------------------------------
    @Override
    public void onBackPressed() {
        customExitDialog();
    }

    private void customExitDialog() {
        // creating custom dialog
        final Dialog dialog = new Dialog(HomePage.this);

        // setting content view to dialog
        dialog.setContentView(R.layout.custom_exit_dialog);

        // getting reference of TextView
        TextView dialogButtonYes = (TextView) dialog.findViewById(R.id.textViewYes);
        TextView dialogButtonNo = (TextView) dialog.findViewById(R.id.textViewNo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // click listener for No
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss the dialog
                dialog.dismiss();

            }
        });

        // click listener for Yes
        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //below is the shared preference displaying the login user name
        sharedPreferencesId = getSharedPreferences(SHARED_PRE_NAME, Context.MODE_PRIVATE);
//        userid = Integer.parseInt(sharedPreferencesId.getString(KEY_ID, ""));
        username = sharedPreferencesId.getString(KEY_NAME, "");
        usernamedisplay = findViewById(R.id.userName_dashboard);
        usernamedisplay.setText("Welcome, " + username);

        //--------Location codes-------------------
        //Add permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        lbl_latitude = findViewById(R.id.lbl_latitude);
        lbl_longitude = findViewById(R.id.lbl_longitude);
        btnGetLocation = findViewById(R.id.get_location);

        //---HIDING THE ACTION BAR
        try {
//          this.getSupportActionBar().hide();
            getSupportActionBar().setTitle("");
        } catch (NullPointerException e) {
        }
        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#44E8EAED"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);

        //---------------REFERENCES------------------------------
        //----------spinners-----------
        stateSpinner = (AutoCompleteTextView) findViewById(R.id.spin_select_state);
        districtSpinner = (AutoCompleteTextView) findViewById(R.id.spin_select_district);
        tehsilSpinner = (AutoCompleteTextView) findViewById(R.id.spin_select_tehsil);

        edtVillagename = (TextInputLayout) findViewById(R.id.etVillage_layout);
        edtSlope = (TextInputLayout) findViewById(R.id.etSlope_layout);
        edtLandcover = (TextInputLayout) findViewById(R.id.etLandcover_layout);
        edtCroptype = (TextInputLayout) findViewById(R.id.etCroptype_layout);
        edtLanddegradationCatg = (TextInputLayout) findViewById(R.id.etLandDegradationCat_layout);
        edtSeverity = (TextInputLayout) findViewById(R.id.etSeverity_layout);
        edtExtent = (TextInputLayout) findViewById(R.id.etExtent_layout);
        edtVillagename = (TextInputLayout) findViewById(R.id.etVillage_layout);
        edtVillagename = (TextInputLayout) findViewById(R.id.etVillage_layout);

        //------------ADD PHOTOS-------------------------
        //for choosing img btn
//        east_browse = (Button) findViewById(R.id.image_1);
//        west_browse = (Button) findViewById(R.id.image_2);

        //for img logo
        imgView_1 = (ImageView) findViewById(R.id.image_1);
        imgView_2 = (ImageView) findViewById(R.id.image_2);

        //---------------------------------------------LIST OF STATE AND DISTRICT ON SPINNER-----------------------------------------------------------------
        //populate arrayAdapter using string array and spinner layout that we will define
        stateAdapter = ArrayAdapter.createFromResource(this, R.array.array_indian_state, R.layout.spinner_item);

        //now specifying the layout to use when list of choice is appears
        stateAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        //now simply populate the spinner using the adapter i.e stateAdapter
        stateSpinner.setAdapter(stateAdapter);

        stateSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //for disabling the first option of state spinner
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                    tv.setTextSize(18);
                }
                //storing the selected state to "selectedState" variable
//                selectedState = stateSpinner.getText().toString();
                selectedState = (String) adapterView.getItemAtPosition(position);

                int adapterViewID = adapterView.getId();

                if (adapterViewID != R.id.spin_select_state) {
                    districtSpinner.setText("");
                    Log.d("ok", "ok-----------");
                    switch (selectedState) {
                        case "Andhra Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhra_pradesh_districts, R.layout.spinner_item);
                            break;
                        case "Arunachal Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_arunachal_pradesh_districts, R.layout.spinner_item);
                            break;
                        case "Assam":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_assam_districts, R.layout.spinner_item);
                            break;
                        case "Bihar":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_bihar_districts, R.layout.spinner_item);
                            break;
                        case "Chhattisgarh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_chhattisgarh_districts, R.layout.spinner_item);
                            break;
                        case "Goa":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_goa_districts, R.layout.spinner_item);
                            break;
                        case "Gujarat":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_gujarat_districts, R.layout.spinner_item);
                            break;
                        case "Haryana":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_haryana_districts, R.layout.spinner_item);
                            break;
                        case "Himachal Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_himachal_pradesh_districts, R.layout.spinner_item);
                            break;
                        case "Jharkhand":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_jharkhand_districts, R.layout.spinner_item);
                            break;
                        case "Karnataka":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_karnataka_districts, R.layout.spinner_item);
                            break;
                        case "Kerala":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_kerala_districts, R.layout.spinner_item);
                            break;
                        case "Madhya Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_madhya_pradesh_districts, R.layout.spinner_item);
                            break;
                        case "Maharashtra":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_maharashtra_districts, R.layout.spinner_item);
                            break;
                        case "Manipur":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_manipur_districts, R.layout.spinner_item);
                            break;
                        case "Meghalaya":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_meghalaya_districts, R.layout.spinner_item);
                            break;
                        case "Mizoram":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_mizoram_districts, R.layout.spinner_item);
                            break;
                        case "Nagaland":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_nagaland_districts, R.layout.spinner_item);
                            break;
                        case "Odisha":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_odisha_districts, R.layout.spinner_item);
                            break;
                        case "Punjab":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_punjab_districts, R.layout.spinner_item);
                            break;
                        case "Rajasthan":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_rajasthan_districts, R.layout.spinner_item);
                            break;
                        case "Sikkim":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_sikkim_districts, R.layout.spinner_item);
                            break;
                        case "Tamil Nadu":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_tamil_nadu_districts, R.layout.spinner_item);
                            break;
                        case "Telangana":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_telangana_districts, R.layout.spinner_item);
                            break;
                        case "Tripura":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_tripura_districts, R.layout.spinner_item);
                            break;
                        case "Uttar Pradesh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_uttar_pradesh_districts, R.layout.spinner_item);
                            break;
                        case "Uttarakhand":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_uttarakhand_districts, R.layout.spinner_item);
                            break;
                        case "West Bengal":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_west_bengal_districts, R.layout.spinner_item);
                            break;
                        case "Andaman and Nicobar Islands":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andaman_nicobar_districts, R.layout.spinner_item);
                            break;
                        case "Chandigarh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_chandigarh_districts, R.layout.spinner_item);
                            break;
                        case "Dadra and Nagar Haveli":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_dadra_nagar_haveli_districts, R.layout.spinner_item);
                            break;
                        case "Daman and Diu":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_daman_diu_districts, R.layout.spinner_item);
                            break;
                        case "Delhi":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_delhi_districts, R.layout.spinner_item);
                            break;
                        case "Jammu and Kashmir":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_jammu_kashmir_districts, R.layout.spinner_item);
                            break;
                        case "Lakshadweep":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_lakshadweep_districts, R.layout.spinner_item);
                            break;
                        case "Ladakh":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_ladakh_districts, R.layout.spinner_item);
                            break;
                        case "Puducherry":
                            districtAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_puducherry_districts, R.layout.spinner_item);
                            break;
                        default:
                            break;
                    }
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);     // Specify the layout to use when the list of choices appears
                    districtSpinner.setAdapter(districtAdapter);
                }
            }
        });

        //--------------------district------------------------
        districtSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //storing the selected state to "selectedState" variable
//                selectedDistrict = districtSpinner.getText().toString();
                selectedDistrict = (String) adapterView.getItemAtPosition(position);

                int adapterViewID = adapterView.getId();

                if (adapterViewID != R.id.spin_select_district) {
                    tehsilSpinner.setText("");
                    Log.d("ok", "ok-----------");
                    switch (selectedDistrict) {
                        // -------Andaman Nicobar---------------
                        case "Nicobar":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_nicobarDistrict_tehsil, R.layout.spinner_item);
                            break;
                        case "North and Middle Andaman":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_northmiddleAndamanDistrict_tehsil, R.layout.spinner_item);
                            break;
                        case "South Andaman":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_southAndamanDistrict_tehsil, R.layout.spinner_item);
                            break;

                        // -------Andhra Pradesh---------------
                        case "Anantapur":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Anantapur, R.layout.spinner_item);
                            break;
                        case "Chittoor":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Chittoor, R.layout.spinner_item);
                            break;
                        case "East Godavari":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_EastGodavari, R.layout.spinner_item);
                            break;
                        case "Cuddapah":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Cuddapah, R.layout.spinner_item);
                            break;
                        case "Guntur":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Guntur, R.layout.spinner_item);
                            break;
                        case "Krishna":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Krishna, R.layout.spinner_item);
                            break;
                        case "Kurnool":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Kurnool, R.layout.spinner_item);
                            break;
                        case "Nellore":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Nellore, R.layout.spinner_item);
                            break;
                        case "Prakasam":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Prakasam, R.layout.spinner_item);
                            break;
                        case "Srikakulam":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Srikakulam, R.layout.spinner_item);
                            break;
                        case "Visakhapatnam":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Visakhapatnam, R.layout.spinner_item);
                            break;
                        case "Vizianagaram":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_Vizianagaram, R.layout.spinner_item);
                            break;
                        case "West Godavari":
                            tehsilAdapter = ArrayAdapter.createFromResource(HomePage.this,
                                    R.array.array_andhraPradesh_tehsil_WestGodavari, R.layout.spinner_item);
                            break;
                        default:
                            break;
                    }
                    tehsilAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);     // Specify the layout to use when the list of choices appears
                    tehsilSpinner.setAdapter(tehsilAdapter);
                }
            }
        });

        //--------------------tehsil------------------------
        tehsilSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //for disabling the first option of state spinner
                selectedTehsil = (String) adapterView.getItemAtPosition(position);

            }
        });

        //---------------------------------------ADD PHOTOS---------------------------------------
        //--------------------------BUTTON OPERATIONS-------------------------------------------------
//-----------------------------------------------EAST IMG-----------------------------------------------------------
        imgView_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(HomePage.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
//                                startActivityForResult(Intent.createChooser(intent, "Browse East Image"), 1);
                                someActivityResultLauncherEst.launch(Intent.createChooser(intent, "Browse East Image"));
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Toast.makeText(HomePage.this, "Permission Required!!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        //-----------------------------------------------WEST IMG-----------------------------------------------------------
        imgView_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(HomePage.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                someActivityResultLauncherWst.launch(Intent.createChooser(intent, "Browse West Image"));
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Toast.makeText(HomePage.this, "Permission Required!!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

//--------------BUTTON METHOD FOR GETTING CURRENT LOCATION DETAILS---------------
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //Check gps is enable or not

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Write Function To enable gps

                    OnGPS();
                } else {
                    //GPS is already On then

                    getLocation();
                }
            }
        });

    }

    private void getLocation() {
        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomePage.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps != null) {
                double lat = LocationGps.getLatitude();
                double longi = LocationGps.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                lbl_latitude.setText(latitude);
                lbl_longitude.setText(longitude);
            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi = LocationNetwork.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                lbl_latitude.setText(latitude);
                lbl_longitude.setText(longitude);
            } else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi = LocationPassive.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                lbl_latitude.setText(latitude);
                lbl_longitude.setText(longitude);
            } else {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

        }

    }

    //-----------hardware back button----------------
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(ProjectRegistrationForm.this, HomePage.class);
//        startActivity(intent);
//    }
    //-----------HOME ICON on action bar-------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_logo:
                // below is for progress dialog box
                //Initialinzing the progress Dialog
                progressDialog = new ProgressDialog(HomePage.this);
                //show Dialog
                progressDialog.show();
                //set Content View
                progressDialog.setContentView(R.layout.progress_dialog);
                //set transparent background
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                sharedPreferences = getSharedPreferences(SHARED_PRE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                setLoginState(true);
                Toast.makeText(HomePage.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLoginState(boolean status) {
        SharedPreferences sp = getSharedPreferences("LoginState", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("setLoggingOut", status);
        ed.commit();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //----------------------EAST-------------------
    private void encodeBitmapImg_first(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImgStr_1 = Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }

    //-------------------------WEST-----------------------
    private void encodeBitmapImg_second(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImgStr_2 = Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }

    //-------------------------BUTTON FUNCTION-----------------------
    public void DataSubmitBtn(View view) {
        Log.d("state", "state--------------" + selectedState);
        Log.d("district", "district--------------" + selectedDistrict);
        Log.d("tehsil", "tehsil--------------" + selectedTehsil);

        // below is for progress dialog box
        //Initialinzing the progress Dialog
        progressDialog = new ProgressDialog(HomePage.this);
        //show Dialog
        progressDialog.show();
        //set Content View
        progressDialog.setContentView(R.layout.progress_dialog);
        //set transparent background
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // for project id
//        sharedPreferencesId = getSharedPreferences(SHARED_PRE_NAME, Context.MODE_PRIVATE);
//        projectID = sharedPreferencesId.getString(KEY_PROJECT_ID, "");

        villagename = edtVillagename.getEditText().getText().toString().trim();
        slope = edtSlope.getEditText().getText().toString().trim();
        landCover = edtLandcover.getEditText().getText().toString().trim();
        cropType = edtCroptype.getEditText().getText().toString().trim();
        landDegradationCategory = edtLanddegradationCatg.getEditText().getText().toString().trim();
        severity = edtSeverity.getEditText().getText().toString().trim();
        extent = edtExtent.getEditText().getText().toString().trim();

        //----------validating the text fields if empty or not.-------------------//commenting only for next page codig
        if (TextUtils.isEmpty(villagename)) {
            progressDialog.dismiss();
            edtVillagename.setError("Please enter village name");
            Toast.makeText(this, "village name required...", Toast.LENGTH_LONG).show();
            return;
        } else {
            // calling method to add data
            StringRequest stringRequest = new StringRequest(Request.Method.POST, homepg_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //reseting all data back to empty after sending to database
                    //---------add photos------------
                    imgView_1.setImageResource(R.drawable.image_gallery);
                    imgView_2.setImageResource(R.drawable.image_gallery);

                    //below code is for live server
                    try {
                        if (TextUtils.equals(response, "1")) {
                            progressDialog.dismiss();
                            Toast.makeText(HomePage.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(HomePage.this, "Data stored Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(HomePage.this, HomePage.class));
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.d("error", "error-------" + error);
                    Toast toast = Toast.makeText(getApplicationContext(), "Images required..." + error, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 20);
                    toast.show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("userid", String.valueOf(userid));
                    data.put("username", username);
                    data.put("state", selectedState);
                    data.put("district", selectedDistrict);
                    data.put("tehsil", selectedTehsil);
                    data.put("villagename", villagename);
                    data.put("latitude", latitude);
                    data.put("longitude", longitude);
                    data.put("slope", slope);
                    data.put("landcover", landCover);
                    data.put("croptype", cropType);
                    data.put("landdegradationcategory", landDegradationCategory);
                    data.put("severity", severity);
                    data.put("extent", extent);
                    data.put("first_image", encodeImgStr_1);
                    data.put("second_image", encodeImgStr_2);

                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}

