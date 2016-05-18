package com.medhelp.medhelp.activities.doctor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.medhelp.AppController;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.Doctor;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditDoctorProfileActivity extends Activity {

    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;

    private EditText mNameText;
    private EditText mEmailText;
    private EditText mCrmText;
    private EditText mStreetName;
    private EditText mZipCode;
    private EditText mCity;
    private EditText mState;
    private EditText mCountry;
    private EditText mPhone;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);

        mUser = (User) getIntent().getSerializableExtra("user");
        initFields(mUser);

        loadUserFromService();

        Button cancelButton = (Button) findViewById(R.id.button_cancel_editDoctorProfile);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button saveButton = (Button) findViewById(R.id.button_save_editDoctorProfile);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDoctor(mNameText.getText().toString(), mEmailText.getText().toString(),
                        mCrmText.getText().toString(), mStreetName.getText().toString(),
                        mZipCode.getText().toString(), mCity.getText().toString(),
                        mState.getText().toString(), mCountry.getText().toString(),
                        mPhone.getText().toString());
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void initFields(User user) {
        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_editDoctorProfile);

        mNameText = (EditText) findViewById(R.id.input_name_editDoctorProfile);
        mEmailText = (EditText) findViewById(R.id.input_email_editDoctorProfile);
        mCrmText = (EditText) findViewById(R.id.input_crm_editDoctorProfile);
        mStreetName = (EditText) findViewById(R.id.input_streetName_editDoctorProfile);
        mCity = (EditText) findViewById(R.id.input_city_editDoctorProfile);
        mZipCode = (EditText) findViewById(R.id.input_zipCode_editDoctorProfile);
        mState = (EditText) findViewById(R.id.input_state_editDoctorProfile);
        mCountry = (EditText) findViewById(R.id.input_country_editDoctorProfile);

        mPhone = (EditText) findViewById(R.id.input_phone_editDoctorProfile);
    }

    private void populateFields(Doctor doctor) {
        if (doctor != null) {
            mNameText.setText(doctor.getName());
            mEmailText.setText(doctor.getEmail());
            mCrmText.setText(doctor.getCrm());

            mStreetName.setText(doctor.getAddressStreet());
            mCity.setText(doctor.getCity());
            mZipCode.setText(doctor.getZipCode());
            mState.setText(doctor.getState());
            mCountry.setText(doctor.getCountry());

            mPhone.setText(doctor.getPhone());
        }
    }

    private void loadUserFromService() {
        String doctorUrl = URLHelper.GET_DOCTOR_URL + "/" + mUser.get_id();
        StringRequest request = new StringRequest(Request.Method.GET, doctorUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Doctor doctor = parseResponseJSON(response);
                populateFields(doctor);
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

    private void saveDoctor(final String name, final String email, final String crm,
                            final String streetName, final String zipCode, final String city,
                            final String state, final String country, final String phone) {
        String doctorUrl = URLHelper.SAVE_DOCTOR_URL + "/" + mUser.get_id();
        StringRequest request = new StringRequest(Request.Method.PUT, doctorUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean success = parseSaveUserResponseJSON(response);

                if (success) {
                    Intent intent = new Intent(getApplicationContext(), MainDoctorActivity.class);
                    intent.putExtra("user", mUser);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", ApiKeyHelper.getApiKey());

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("crm", crm);
                params.put("addressStreet", streetName);
                params.put("city", city);
                params.put("zipCode", zipCode);
                params.put("state", state);
                params.put("country", country);
                params.put("phone", phone);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private boolean parseSaveUserResponseJSON(String response) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> message = null;
        try {
            message = mapper.readValue(response, new TypeReference<Map<String,String>>() { });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message.get("sucess").equals("ok");
    }

}