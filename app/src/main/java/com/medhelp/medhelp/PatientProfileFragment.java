package com.medhelp.medhelp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.medhelp.medhelp.model.User;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientProfileFragment extends Fragment {

    private static final String TAG = "PatientProfileFragment";
    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;
    private EditText mNameText;
    private EditText mEmailText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        User user = (User) getActivity().getIntent().getSerializableExtra("user");

        mNameText = (EditText) view.findViewById(R.id.input_name_patientProfile);
        mEmailText = (EditText) view.findViewById(R.id.input_email_patientProfile);

        mNameText.setText(user.name);
        mEmailText.setText(user.email);

        mProfileImage = (CircleImageView) view.findViewById(R.id.image_profile_patientProfile);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                mProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getActivity().getBaseContext(), "Não foi possível carregar a imagem", Toast.LENGTH_LONG).show();
            }
        }
    }
}
