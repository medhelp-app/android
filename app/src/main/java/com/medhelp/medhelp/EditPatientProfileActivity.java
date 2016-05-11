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

import com.medhelp.medhelp.model.User;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPatientProfileActivity extends Activity {

    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_profile);

        User user = (User) getIntent().getSerializableExtra("user");

        EditText nameText = (EditText) findViewById(R.id.input_name_editPatientProfile);
        EditText emailText = (EditText) findViewById(R.id.input_email_editPatientProfile);
        EditText streetName = (EditText) findViewById(R.id.input_streetName_editPatientProfile);
        EditText zipCode = (EditText) findViewById(R.id.input_zipCode_editPatientProfile);
        EditText state = (EditText) findViewById(R.id.input_state_editPatientProfile);
        EditText country = (EditText) findViewById(R.id.input_country_editPatientProfile);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        streetName.setText(user.getAddress().getStreetName());
        zipCode.setText(user.getAddress().getZipCode());
        state.setText(user.getAddress().getState());
        country.setText(user.getAddress().getCountry());

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
                finish();
            }
        });

        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_editPatientProfile);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
