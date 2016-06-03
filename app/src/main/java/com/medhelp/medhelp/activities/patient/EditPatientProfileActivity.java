package com.medhelp.medhelp.activities.patient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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
import com.medhelp.medhelp.helpers.VolleyMultiPartRequest;
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

                saveImage();
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
        String patientUrl = URLHelper.SAVE_PATIENT_URL + "/" + mUser.get_id();
        StringRequest request = new StringRequest(Request.Method.PUT, patientUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean success = parseSaveUserResponseJSON(response);

                if (success) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("user", mUser);
                    startActivity(intent);
                    finish();
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

        return message != null && !TextUtils.isEmpty(message.get("sucess")) && message.get("sucess").equals("ok");
    }

    private void saveImage() {
        String url = URLHelper.SAVE_PATIENT_IMAGE.replace(":id",mUser.get_id());
        HashMap<String, String> params = new HashMap<>();
        params.put("x-access-token", ApiKeyHelper.getApiKey());
        VolleyMultiPartRequest multiPartRequest = new VolleyMultiPartRequest(Request.Method.PUT, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, params) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();

                params.put("profileImage", new DataPart("profileImage.jpg", VolleyMultiPartRequest.getFileDataFromDrawable(getBaseContext(), mProfileImage.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(multiPartRequest);
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
