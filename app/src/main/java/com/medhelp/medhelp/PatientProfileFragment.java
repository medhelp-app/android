package com.medhelp.medhelp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medhelp.medhelp.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientProfileFragment extends Fragment {

    private static final String TAG = "PatientProfileFragment";
    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        final User user = (User) getActivity().getIntent().getSerializableExtra("user");

        TextView nameText = (TextView) view.findViewById(R.id.input_name_patientProfile);
        TextView emailText = (TextView) view.findViewById(R.id.input_email_patientProfile);
        TextView locationText = (TextView) view.findViewById(R.id.input_address_patientProfile);
        TextView phoneText = (TextView) view.findViewById(R.id.input_phone_patientProfile);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        locationText.setText(user.getAddress().toString());
        phoneText.setText(user.getPhone());

        FloatingActionButton fabEditPatient = (FloatingActionButton) view.findViewById(R.id.fab_edit_patient_patientProfile);
        fabEditPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditPatientProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        mProfileImage = (CircleImageView) view.findViewById(R.id.image_profile_patientProfile);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showDialogImage();
            }
        });

        return view;
    }

    private void showDialogImage() {
        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageURI(Uri.parse("android.resource://com.medhelp.medhelp/drawable/blank_profile"));
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
}
