package com.medhelp.medhelp.fragments.doctor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.medhelp.medhelp.model.Appointment;
import com.medhelp.medhelp.model.User;
import com.medhelp.medhelp.views.adapters.PatientListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorPatientsFragment extends Fragment {

    private User mUser;

    private ListView mListView;
    private PatientListAdapter mPatientListAdapter;
    private List<Appointment> mAppointments;

    public DoctorPatientsFragment() {
        // Required empty public constructor
    }
    public static DoctorPatientsFragment newInstance(String param1, String param2) {
        DoctorPatientsFragment fragment = new DoctorPatientsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_patients, container, false);
        mUser = (User) getActivity().getIntent().getSerializableExtra("user");

        setHasOptionsMenu(true);

        mListView = (ListView) view.findViewById(R.id.list_patients_doctorPatients);
        mAppointments = new ArrayList<>();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        getPatientsFromService();

        return view;
    }

    private void getPatientsFromService() {
        String url = URLHelper.GET_DOCTOR_APPOINTMENT.replace(":id", mUser.get_id());
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mAppointments = parseResponseJSON(response);
                mPatientListAdapter = new PatientListAdapter(getActivity(), mAppointments);
                mListView.setAdapter(mPatientListAdapter);
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

    private List<Appointment> parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Appointment[] appointments = new Appointment[]{};
        try {
            appointments = objectMapper.readValue(response, Appointment[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(appointments);
    }
}
