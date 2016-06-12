package com.medhelp.medhelp.fragments.doctor;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.medhelp.medhelp.model.FeedItem;
import com.medhelp.medhelp.model.User;
import com.medhelp.medhelp.views.adapters.FeedListAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DoctorSocialFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private User mUser;

    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;

    private EditText mNewPublicationText;
    private Button mNewPublicationButton;

    public DoctorSocialFragment() {
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
        View view = inflater.inflate(R.layout.fragment_patient_social, container, false);

        mUser = (User) getActivity().getIntent().getSerializableExtra("user");

        mNewPublicationText = (EditText) view.findViewById(R.id.text_new_publication);
        mNewPublicationButton = (Button) view.findViewById(R.id.btn_new_publication);
        mNewPublicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPublication(mUser.get_id(), "post", mNewPublicationText.getText().toString());
            }
        });

        listView = (ListView) view.findViewById(R.id.list);

        feedItems = new ArrayList<>();

        loadPostsFromService();

        return view;
    }

    private void loadPostsFromService() {
        StringRequest request = new StringRequest(Request.Method.GET, URLHelper.GET_PUBLICATIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                feedItems = parseResponseJSON(response);
                listAdapter = new FeedListAdapter(getActivity(), feedItems);
                listView.setAdapter(listAdapter);
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

        AppController.getInstance().addToRequestQueue(request);
    }

    private List<FeedItem> parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        FeedItem[] feedItems = new FeedItem[]{};
        try {
            feedItems = objectMapper.readValue(response, FeedItem[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(feedItems);
    }


    private void addNewPublication(final String idUser, final String publicationType, final String publicationText) {
        StringRequest request = new StringRequest(Request.Method.POST, URLHelper.ADD_PUBLICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mNewPublicationText.setText("");
                loadPostsFromService();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(calendar.getTimeInMillis());
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String date = format1.format(calendar.getTime());

                Map<String, String> params = new HashMap<>();
                params.put("idUser", idUser);
                params.put("type", publicationType);
                params.put("text", publicationText);
                params.put("date", date);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
