package com.medhelp.medhelp.fragments.patient;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.medhelp.medhelp.activities.patient.EditPatientProfileActivity;
import com.medhelp.medhelp.activities.patient.PatientHumanBodyActivity;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientProfileFragment extends Fragment {

    private static final String TAG = "PatientProfileFragment";
    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;
    private Bitmap mProfileImageBitmap;

    private User mUser;
    private TextView nameText;
    private TextView emailText;
    private TextView locationText;
    private TextView phoneText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        mUser = (User) getActivity().getIntent().getSerializableExtra("user");

        if(mUser != null) {
            loadUserFromService();
            loadImageFromService();
        }

        initFields(view);

        populateFields(mUser);

        FloatingActionButton fabEditPatient = (FloatingActionButton) view.findViewById(R.id.fab_edit_patient_patientProfile);
        fabEditPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditPatientProfileActivity.class);
                intent.putExtra("user", mUser);
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

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image_body_patientProfile);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), PatientHumanBodyActivity.class);
                intent.putExtra("patientId", mUser.get_id());
                startActivity(intent);
            }
        });

        return view;
    }

    private void initFields(View view) {
        nameText = (TextView) view.findViewById(R.id.input_name_patientProfile);
        emailText = (TextView) view.findViewById(R.id.input_email_patientProfile);
        locationText = (TextView) view.findViewById(R.id.input_address_patientProfile);
        phoneText = (TextView) view.findViewById(R.id.input_phone_patientProfile);
    }

    private void populateFields(User user) {
        if (user != null) {
            nameText.setText(user.getName());
            emailText.setText(user.getEmail());
            locationText.setText(String.valueOf(user.getFullAddress()));
            phoneText.setText(user.getPhone());
        }
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

    private void loadImageFromService() {
        String imageUrl = URLHelper.GET_PATIENT_IMAGE.replace(":id", mUser.get_id());

        StringRequest request = new StringRequest(Request.Method.GET, imageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProfileImageBitmap = getImage(response);
                mProfileImage.setImageBitmap(mProfileImageBitmap);
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

    private Bitmap getImage(String response) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> errorMessage = null;
        try {
            errorMessage = mapper.readValue(response, new TypeReference<Map<String,String>>() { });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (errorMessage != null && !TextUtils.isEmpty(errorMessage.get("profileImage"))) {
            return ImageHelper.decodeBase64ToImage(errorMessage.get("profileImage"));
        }

        return null;
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
        imageView.setImageBitmap(mProfileImageBitmap);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
}
