package com.medhelp.medhelp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.medhelp.AppController;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.activities.patient.PatientHumanBodyActivity;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.Patient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPatientProfileActivity extends Activity {

    private String mPatientId;
    private String mDoctorId;

    private CircleImageView mProfileImage;
    private TextView mNameText;
    private TextView mEmailText;
    private TextView mPhoneText;
    private ImageButton mHumanBodyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_profile);

        mPatientId = (String) getIntent().getSerializableExtra("patientId");
        mDoctorId = (String) getIntent().getSerializableExtra("doctorId");

        initFields();

        loadUserFromService();

        mHumanBodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PatientHumanBodyActivity.class);
                intent.putExtra("patientId", mPatientId);
                startActivity(intent);
            }
        });
    }

    private void initFields() {
        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_viewPatient);
        mNameText = (TextView) findViewById(R.id.name_viewPatient);
        mEmailText = (TextView) findViewById(R.id.email_viewPatient);
        mPhoneText = (TextView) findViewById(R.id.phone_viewPatient);
        mHumanBodyButton = (ImageButton) findViewById(R.id.image_body_patientProfile);
    }

    private void populateFields(Patient patient) {
        if (patient != null) {
            mNameText.setText(patient.getName());
            mEmailText.setText(patient.getEmail());
            mPhoneText.setText(patient.getPhone());

            if (patient.getProfileImage() != null) {
                Bitmap img = ImageHelper.decodeBase64ToImage(patient.getProfileImage());
                if (img != null)
                    mProfileImage.setImageBitmap(Bitmap.createScaledBitmap(img, 100, 100, false));
            }
        }
    }

    private void loadUserFromService() {
        String patientUrl = URLHelper.GET_PATIENT_URL + "/" + mPatientId;
        StringRequest request = new StringRequest(Request.Method.GET, patientUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Patient patient = parsePatientResponseJSON(response);
                populateFields(patient);
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

    private Patient parsePatientResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Patient patient = null;
        try {
            patient = objectMapper.readValue(response, Patient.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return patient;
    }

}
