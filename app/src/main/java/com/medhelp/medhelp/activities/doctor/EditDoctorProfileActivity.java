package com.medhelp.medhelp.activities.doctor;

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
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.medhelp.AppController;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.helpers.VolleyMultiPartRequest;
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
    private EditText mDoctorTypeText;
    private EditText mCrmText;
    private EditText mCrmStateText;
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
        initFields();

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
                saveDoctor(mNameText.getText().toString(), mDoctorTypeText.getText().toString(),
                        mEmailText.getText().toString(), mCrmText.getText().toString(),
                        mStreetName.getText().toString(), mZipCode.getText().toString(),
                        mCity.getText().toString(), mState.getText().toString(),
                        mCountry.getText().toString(), mPhone.getText().toString(),
                        mCrmStateText.getText().toString());

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

    private void initFields() {
        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_editDoctorProfile);

        mNameText = (EditText) findViewById(R.id.input_name_editDoctorProfile);
        mEmailText = (EditText) findViewById(R.id.input_email_editDoctorProfile);
        mDoctorTypeText = (EditText) findViewById(R.id.input_doctor_type_editDoctorProfile);
        mCrmText = (EditText) findViewById(R.id.input_crm_editDoctorProfile);
        mCrmStateText = (EditText) findViewById(R.id.input_crm_uf_editDoctorProfile);
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
            mDoctorTypeText.setText(doctor.getDoctorType());
            mCrmText.setText(doctor.getCrm());
            mCrmStateText.setText(doctor.getUfCrm());

            mStreetName.setText(doctor.getAddressStreet());
            mCity.setText(doctor.getCity());
            mZipCode.setText(doctor.getZipCode());
            mState.setText(doctor.getState());
            mCountry.setText(doctor.getCountry());

            mPhone.setText(doctor.getPhone());

            if(doctor.getProfileImage() != null) {
                mProfileImage.setImageBitmap(ImageHelper.decodeBase64ToImage(doctor.getProfileImage()));
            }
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

    private void saveDoctor(final String name, final String email, final String doctorType,
                            final String crm, final String streetName, final String zipCode,
                            final String city, final String state, final String country,
                            final String phone, final String crmUf) {
        String doctorUrl = URLHelper.SAVE_DOCTOR_URL + "/" + mUser.get_id();
        StringRequest request = new StringRequest(Request.Method.PUT, doctorUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean success = parseSaveUserResponseJSON(response);

                if (success) {
                    Intent intent = new Intent(getApplicationContext(), MainDoctorActivity.class);
                    intent.putExtra("user", mUser);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(getBaseContext(), "Erro de tempo de resposta", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getBaseContext(), "Falha na conexão com o servidor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Erro ao completar operação", Toast.LENGTH_SHORT).show();
                }
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
                params.put("doctorType", doctorType);
                params.put("crm", crm);
                params.put("ufCrm", crmUf);
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
        String url = URLHelper.SAVE_DOCTOR_IMAGE.replace(":id",mUser.get_id());
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