package com.medhelp.medhelp.fragments.doctor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.medhelp.medhelp.activities.doctor.EditDoctorProfileActivity;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.Doctor;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileFragment extends Fragment {

    private int PICK_IMAGE_REQUEST = 1;

    private CircleImageView mProfileImage;
    private Bitmap mProfileImageBitmap;

    private User mUser;
    private TextView mNameText;
    private TextView mEmailText;
    private TextView mCrmText;
    private TextView mCrmUfText;
    private TextView mLocationText;
    private TextView mPhoneText;

    public DoctorProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        mUser = (User) getActivity().getIntent().getSerializableExtra("user");

        loadUserFromService();
        loadImageFromService();

        initFields(view);

        FloatingActionButton fabEditPatient = (FloatingActionButton) view.findViewById(R.id.fab_edit_doctor_doctorProfile);
        fabEditPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditDoctorProfileActivity.class);
                intent.putExtra("user", mUser);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initFields(View view) {
        mProfileImage = (CircleImageView) view.findViewById(R.id.image_profile_doctorProfile);
        mNameText = (TextView) view.findViewById(R.id.input_name_doctorProfile);
        mEmailText = (TextView) view.findViewById(R.id.input_email_doctorProfile);
        mCrmText = (TextView) view.findViewById(R.id.input_crm_doctorProfile);
        mCrmUfText = (TextView) view.findViewById(R.id.input_crm_uf_doctorProfile);
        mLocationText = (TextView) view.findViewById(R.id.input_address_doctorProfile);
        mPhoneText = (TextView) view.findViewById(R.id.input_phone_doctorProfile);
    }

    private void populateFields(Doctor doctor) {
        if (doctor != null) {
            mNameText.setText(doctor.getName());
            mEmailText.setText(doctor.getEmail());
            mCrmText.setText(doctor.getCrm());
            mCrmUfText.setText(doctor.getUfCrm());
            mLocationText.setText(String.valueOf(doctor.getFullAddress()));
            mPhoneText.setText(doctor.getPhone());
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

    private void loadImageFromService() {
        String imageUrl = URLHelper.GET_DOCTOR_IMAGE.replace(":id", mUser.get_id());

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
        Map<String, String> responseMap = null;
        try {
            responseMap = mapper.readValue(response, new TypeReference<Map<String,String>>() { });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responseMap != null && !TextUtils.isEmpty(responseMap.get("profileImage"))) {
            return ImageHelper.decodeBase64ToImage(responseMap.get("profileImage"));
        }

        return null;
    }
}
