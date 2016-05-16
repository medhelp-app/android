package com.medhelp.medhelp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPatientProfileActivity extends Activity {

    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;

    private EditText mEmailText;
    private EditText mNameText;
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
        setContentView(R.layout.activity_edit_patient_profile);

        mUser = (User) getIntent().getSerializableExtra("user");
        initFields(mUser);

        loadUserFromService();

        Button cancelButton = (Button) findViewById(R.id.button_cancel_editPatientProfile);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button saveButton = (Button) findViewById(R.id.button_save_editPatientProfile);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePatient(mNameText.getText().toString(), mEmailText.getText().toString(),
                        mStreetName.getText().toString(), mZipCode.getText().toString(),
                        mCity.getText().toString(), mState.getText().toString(),
                        mCountry.getText().toString(), mPhone.getText().toString());
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

    private void loadUserFromService() {
        String patientUrl = URLHelper.GET_PATIENT_URL + "/" + mUser.get_id();
        StringRequest request = new StringRequest(Request.Method.GET, patientUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user = parseResponseJSON(response);
                populateFields(user);
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

    private User parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        User user = null;
        try {
            user = objectMapper.readValue(response, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void savePatient(final String name, final String email, final String streetName,
                             final String zipCode, final String city, final String state,
                             final String country, final String phone) {
        StringRequest request = new StringRequest(Request.Method.POST, URLHelper.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("streetName", streetName);
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

    private void initFields(User user) {
        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_editPatientProfile);

        mNameText = (EditText) findViewById(R.id.input_name_editPatientProfile);
        mEmailText = (EditText) findViewById(R.id.input_email_editPatientProfile);
        mStreetName = (EditText) findViewById(R.id.input_streetName_editPatientProfile);
        mCity = (EditText) findViewById(R.id.input_city_editPatientProfile);
        mZipCode = (EditText) findViewById(R.id.input_zipCode_editPatientProfile);
        mState = (EditText) findViewById(R.id.input_state_editPatientProfile);
        mCountry = (EditText) findViewById(R.id.input_country_editPatientProfile);

        mPhone = (EditText) findViewById(R.id.input_phone_editPatientProfile);

        populateFields(user);
    }

    private void populateFields(User user) {
        if (user != null) {
            mNameText.setText(user.getName());
            mEmailText.setText(user.getEmail());

            mStreetName.setText(user.getAddressStreet());
            mCity.setText(user.getCity());
            mZipCode.setText(user.getZipCode());
            mState.setText(user.getState());
            mCountry.setText(user.getCountry());

            mPhone.setText(user.getPhone());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                mProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Não foi possível carregar a imagem", Toast.LENGTH_LONG).show();
            }
        }
    }
}
