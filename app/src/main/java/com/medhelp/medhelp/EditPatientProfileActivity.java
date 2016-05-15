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
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPatientProfileActivity extends Activity {

    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;

    private EditText emailText;
    private EditText nameText;
    private EditText streetName;
    private EditText zipCode;
    private EditText city;
    private EditText state;
    private EditText country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_profile);

        User user = (User) getIntent().getSerializableExtra("user");
        initFields(user);

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
                savePatient(nameText.getText().toString(), emailText.getText().toString(),
                        streetName.getText().toString(), zipCode.getText().toString(),
                        city.getText().toString(), state.getText().toString(), country.getText().toString());
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

    private void savePatient(final String name, final String email, final String streetName,
                             final String zipCode, final String city, final String state, final String country) {
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
                params.put("zipCode", zipCode);
                params.put("state", state);
                params.put("country", country);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private void initFields(User user) {
        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_editPatientProfile);

        nameText = (EditText) findViewById(R.id.input_name_editPatientProfile);
        emailText = (EditText) findViewById(R.id.input_email_editPatientProfile);
        streetName = (EditText) findViewById(R.id.input_streetName_editPatientProfile);
        city = (EditText) findViewById(R.id.input_city_editPatientProfile);
        zipCode = (EditText) findViewById(R.id.input_zipCode_editPatientProfile);
        state = (EditText) findViewById(R.id.input_state_editPatientProfile);
        country = (EditText) findViewById(R.id.input_country_editPatientProfile);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());

        streetName.setText(user.getAddressStreet());
        zipCode.setText(user.getZipCode());
        state.setText(user.getState());
        country.setText(user.getCountry());
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
