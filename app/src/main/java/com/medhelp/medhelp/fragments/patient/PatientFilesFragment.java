package com.medhelp.medhelp.fragments.patient;

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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhelp.medhelp.AppController;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.helpers.VolleyMultiPartRequest;
import com.medhelp.medhelp.model.Archive;
import com.medhelp.medhelp.model.User;
import com.medhelp.medhelp.views.adapters.ArchiveListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientFilesFragment extends Fragment {

    private int PICK_IMAGE_REQUEST = 1;

    private User mUser;

    private ListView mListView;
    private ArchiveListAdapter mArchiveListAdapter;
    private List<Archive> mArchives;


    public PatientFilesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_patient_files, container, false);

        mUser = (User) getActivity().getIntent().getSerializableExtra("user");

        setHasOptionsMenu(true);

        mListView = (ListView) view.findViewById(R.id.list_archives_patient);
        mArchives = new ArrayList<>();

        getArchivesFromService();

//        FloatingActionButton addArchive = (FloatingActionButton) view.findViewById(R.id.fab_add_archive);
//        addArchive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
//            }
//        });

        return view;
    }

    private void getArchivesFromService() {
        String url = URLHelper.GET_ARCHIVES.replace(":id", mUser.get_id());
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mArchives = parseResponseJSON(response);
                mArchiveListAdapter = new ArchiveListAdapter(getActivity(), mArchives);
                mListView.setAdapter(mArchiveListAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Tente novamente", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", ApiKeyHelper.getApiKey());

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(request);
    }

    private List<Archive> parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Archive[] archives = new Archive[]{};
        try {
            archives = objectMapper.readValue(response, Archive[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(archives);
    }

    private void saveImage(final Bitmap bitmap) {
        String url = URLHelper.SAVE_ARCHIVE.replace(":id",mUser.get_id());
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
                String response = error.toString();
            }
        }, params) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();

                params.put("profileImage", new DataPart("profileImage.jpg", VolleyMultiPartRequest.getFileDataFromDrawable(bitmap)));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(multiPartRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
//                String selectedImageUri = data.toURI();
//                Bitmap mBitmap = MediaStore.getBitmap(this.getContentResolver(), selectedImageUri);
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    saveImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }
}
