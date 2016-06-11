package com.medhelp.medhelp.fragments.doctor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.helpers.WeekDayHelper;
import com.medhelp.medhelp.model.Availability;
import com.medhelp.medhelp.model.EWeekDays;
import com.medhelp.medhelp.model.User;
import com.medhelp.medhelp.views.adapters.AvailabilityListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorPrescriptionFragment extends Fragment {

    private User mUser;

    private FloatingActionButton mAddAvailabilityFab;
    private Spinner mDialogWeekdaySpinner;
    private EditText mDialogStartHourText;
    private EditText mDialogEndHourText;

    private HashMap<String, List<Availability>> mAvailabilityMap;

    public DoctorPrescriptionFragment() {
        // Required empty public constructor
    }

    public static DoctorPrescriptionFragment newInstance(String param1, String param2) {
        DoctorPrescriptionFragment fragment = new DoctorPrescriptionFragment();
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
        View view = inflater.inflate(R.layout.fragment_doctor_prescription, container, false);

        mUser = (User) getActivity().getIntent().getSerializableExtra("user");

        initFields(view);
        getAvailabilityFromService();

        mAddAvailabilityFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddAvailabilityDialog();
            }
        });

        return view;
    }

    private void initFields(View view) {
        mAddAvailabilityFab = (FloatingActionButton) view.findViewById(R.id.fab_add_availability_consult);
    }

    private void getAvailabilityFromService() {
        String url = URLHelper.GET_DOCTOR_AVAILABILITY.replace(":id", mUser.get_id());
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Availability[] availabilities = parseResponseJSON(response);

                mAvailabilityMap = new HashMap<>();
                for (Availability availability : availabilities) {
                    if (mAvailabilityMap.get(availability.getWeekday()) != null) {
                        mAvailabilityMap.get(availability.getWeekday()).add(availability);
                    } else {
                        mAvailabilityMap.put(availability.getWeekday(), new ArrayList<>(Arrays.asList(availability)));
                    }
                }

                if (availabilities != null) {
                    AvailabilityListAdapter list = new AvailabilityListAdapter(getActivity(), mAvailabilityMap);
                    ListView lv = (ListView) getView().findViewById(R.id.list_availabilities_consult);
                    lv.setAdapter(list);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", ApiKeyHelper.getApiKey());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private Availability[] parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Availability[] availabilities = null;
        try {
            availabilities = objectMapper.readValue(response, Availability[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return availabilities;
    }

    private void createAddAvailabilityDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_doctor_availability, null);
        alertDialog.setView(dialogView);

        mDialogWeekdaySpinner = (Spinner) dialogView.findViewById(R.id.spinner_doctor_availability);
        String[] weekdays = EWeekDays.getWeekdays();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, weekdays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDialogWeekdaySpinner.setAdapter(adapter);

        mDialogStartHourText = (EditText) dialogView.findViewById(R.id.input_start_hour_addAvailability);
        mDialogEndHourText = (EditText) dialogView.findViewById(R.id.input_end_hour_addAvailability);

        alertDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), mDialogStartHourText.getText().toString(), Toast.LENGTH_LONG).show();
                String day = String.valueOf(WeekDayHelper.mWeekDaysMap.get(mDialogWeekdaySpinner.getSelectedItem().toString()));
                String startHour = mDialogStartHourText.getText().toString();
                String endHour = mDialogEndHourText.getText().toString();

                addAvailabilityToService(day, startHour, endHour);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, null);

        alertDialog.create();
        alertDialog.show();
    }

    private void addAvailabilityToService(final String day, final String startHour, final String endHour) {
        String url = URLHelper.ADD_DOCTOR_AVAILABILITY.replace(":id", mUser.get_id());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (parseAddAvailabilityResponse(response) == true) {
                    Snackbar snackbar = Snackbar.make(getView().findViewById(R.id.fab_add_availability_consult), "Horário adicionado", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    getAvailabilityFromService();
                } else {
                    Snackbar snackbar = Snackbar.make(getView().findViewById(R.id.fab_add_availability_consult), "Ocorreu um problema ao adicionar esta data", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", ApiKeyHelper.getApiKey());

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("weekday", day);
                params.put("startHour", startHour);
                params.put("endHour", endHour);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private boolean parseAddAvailabilityResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> message = null;
        try {
            message = mapper.readValue(response, new TypeReference<Map<String,String>>() { });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message != null && !TextUtils.isEmpty(message.get("success")) && message.get("success").equals("true");
    }

}
