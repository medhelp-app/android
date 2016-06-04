package com.medhelp.medhelp.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.medhelp.medhelp.AppController;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.Doctor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewDoctorProfileActivity extends FragmentActivity {

    private String mDoctorId;

    private CircleImageView mProfileImage;
    private TextView mNameText;
    private TextView mEmailText;
    private TextView mPhoneText;

    private AppCompatRatingBar mRatingGeneral;
    private AppCompatRatingBar mRatingPunctuality;
    private AppCompatRatingBar mRatingAttention;
    private AppCompatRatingBar mRatingLocation;

    private GoogleMap mMap;
    private double mLongitude;
    private double mLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor_profile);

        mDoctorId = (String) getIntent().getSerializableExtra("doctorId");

        initFields();

        loadUserFromService();
    }

    private void initFields() {
        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_doctorProfile);
        mNameText = (TextView) findViewById(R.id.name_viewDoctor);
        mEmailText = (TextView) findViewById(R.id.email_viewDoctor);
        mPhoneText = (TextView) findViewById(R.id.phone_viewDoctor);

        mRatingGeneral = (AppCompatRatingBar) findViewById(R.id.ratingGeneral_viewDoctor);
        mRatingPunctuality = (AppCompatRatingBar) findViewById(R.id.ratingPunctuality_viewDoctor);
        mRatingAttention = (AppCompatRatingBar) findViewById(R.id.ratingAttention_viewDoctor);
        mRatingLocation = (AppCompatRatingBar) findViewById(R.id.ratingLocation_viewDoctor);
    }

    private void populateFields(Doctor doctor) {
        if (doctor != null) {
            mNameText.setText(doctor.getName());
            mEmailText.setText(doctor.getEmail());
            mPhoneText.setText(doctor.getPhone());

            if (doctor.getProfileImage() != null) {
                mProfileImage.setImageBitmap(ImageHelper.decodeBase64ToImage(doctor.getProfileImage()));
            }
        }
    }

    private void loadUserFromService() {
        String patientUrl = URLHelper.GET_DOCTOR_URL + "/" + mDoctorId;
        StringRequest request = new StringRequest(Request.Method.GET, patientUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final Doctor doctor = parseResponseJSON(response);
                populateFields(doctor);

                setLatLng(doctor.getFullAddress());
                if (mLatitude != 0 && mLongitude !=0) {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;

                            LatLng address = new LatLng(mLatitude, mLongitude);
                            mMap.addMarker(new MarkerOptions().position(address).title(doctor.getAddressStreet()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(address));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", ApiKeyHelper.getApiKey());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private Doctor parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Doctor doctor = null;
        try {
            doctor = objectMapper.readValue(response, Doctor.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doctor;
    }

    private void setLatLng(String address) {
        Geocoder geocoder = new Geocoder(this);

        try {
            ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocationName
                    (address, 1);
            if (addresses.size() > 0) {
                mLongitude = addresses.get(0).getLongitude();
                mLatitude = addresses.get(0).getLatitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
