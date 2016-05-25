package com.medhelp.medhelp.fragments.patient;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.GoogleMap;
import com.medhelp.medhelp.AppController;
import com.medhelp.medhelp.activities.patient.MainActivity;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.Doctor;
import com.medhelp.medhelp.views.adapters.DoctorListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientDoctorFragment extends Fragment {

    private GoogleMap mMap;
    private ListView mListView;
    private DoctorListAdapter mDoctorListAdapter;
    private List<Doctor> mDoctors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_doctor, container, false);

        setHasOptionsMenu(true);

        mListView = (ListView) view.findViewById(R.id.list_doctors_patientDoctor);
        mDoctors = new ArrayList<>();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getString(R.string.hint_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = URLHelper.DOCTOR_FIND_URL + "/" + query.replace(" ", "%20");
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mDoctors = parseResponseJSON(response);
                        mDoctorListAdapter = new DoctorListAdapter(getActivity(), mDoctors);
                        mListView.setAdapter(mDoctorListAdapter);

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

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                int indexSuggestion = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
                String suggestion = cursor.getString(indexSuggestion);

                searchView.setQuery(suggestion, true);

                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getActivity(), MainActivity.class)));
        searchView.setIconifiedByDefault(false);
    }

    private List<Doctor> parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Doctor[] doctors = new Doctor[]{};
        try {
             doctors = objectMapper.readValue(response, Doctor[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(doctors);
    }
}
