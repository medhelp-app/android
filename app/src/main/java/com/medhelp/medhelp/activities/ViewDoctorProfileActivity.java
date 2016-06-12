package com.medhelp.medhelp.activities;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.medhelp.medhelp.AppController;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ApiKeyHelper;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.helpers.WeekDayHelper;
import com.medhelp.medhelp.model.Availability;
import com.medhelp.medhelp.model.Doctor;
import com.medhelp.medhelp.model.Opinion;
import com.medhelp.medhelp.model.OpinionSummary;
import com.medhelp.medhelp.views.adapters.OpinionsListAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewDoctorProfileActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    private String mPatientId;
    private String mDoctorId;

    private CircleImageView mProfileImage;
    private TextView mNameText;
    private TextView mEmailText;
    private TextView mPhoneText;

    private Button mScheduleAppointmentButton;
    private TextView mAppointmentDate;
    private Spinner mDialogWeekdaySpinner;
    private HashMap<String, List<Availability>> mAvailabilityMap;
    private List<Availability> mAvailabilityHours;
    private String mAvailabilityDate;

    private AppCompatRatingBar mRatingGeneral;
    private TextView mOptionsCount;
    private AppCompatRatingBar mRatingPunctuality;
    private AppCompatRatingBar mRatingAttention;
    private AppCompatRatingBar mRatingLocation;

    private AppCompatButton mEvaluateButton;
    private AppCompatButton mEvaluateListButton;

    private GoogleMap mMap;
    private double mLongitude;
    private double mLatitude;

    private AppCompatRatingBar mLocationRating;
    private AppCompatRatingBar mAttentionRating;
    private AppCompatRatingBar mPunctualityRating;
    private EditText mEvaluationDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor_profile);

        mDoctorId = (String) getIntent().getSerializableExtra("doctorId");
        mPatientId = (String) getIntent().getSerializableExtra("patientId");

        initFields();

        loadUserFromService();
        loadRatingsFromService();

        mScheduleAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createScheduleAppointmentDialog();
            }
        });

        mEvaluateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvaluationDialog();
            }
        });

        mEvaluateListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvaluationListDialog();
            }
        });
    }

    private void initFields() {
        mProfileImage = (CircleImageView) findViewById(R.id.image_profile_doctorProfile);
        mNameText = (TextView) findViewById(R.id.name_viewDoctor);
        mEmailText = (TextView) findViewById(R.id.email_viewDoctor);
        mPhoneText = (TextView) findViewById(R.id.phone_viewDoctor);

        mScheduleAppointmentButton = (Button) findViewById(R.id.btn_schedule_appointment_viewDoctor);

        mRatingGeneral = (AppCompatRatingBar) findViewById(R.id.ratingGeneral_viewDoctor);
        mOptionsCount = (TextView) findViewById(R.id.opinions_count_viewDoctor);
        mRatingPunctuality = (AppCompatRatingBar) findViewById(R.id.ratingPunctuality_viewDoctor);
        mRatingAttention = (AppCompatRatingBar) findViewById(R.id.ratingAttention_viewDoctor);
        mRatingLocation = (AppCompatRatingBar) findViewById(R.id.ratingLocation_viewDoctor);

        mEvaluateButton = (AppCompatButton) findViewById(R.id.btn_evaluate_viewDoctor);
        mEvaluateListButton = (AppCompatButton) findViewById(R.id.btn_evaluateList_viewDoctor);
    }

    private void populateFields(Doctor doctor) {
        if (doctor != null) {
            mNameText.setText(doctor.getName());
            mEmailText.setText(doctor.getEmail());
            mPhoneText.setText(doctor.getPhone());

            if (doctor.getProfileImage() != null) {
                mProfileImage.setImageBitmap(ImageHelper.decodeBase64ToImage(doctor.getProfileImage()));
            }
        }
    }

    private void loadUserFromService() {
        String patientUrl = URLHelper.GET_DOCTOR_URL + "/" + mDoctorId;
        StringRequest request = new StringRequest(Request.Method.GET, patientUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final Doctor doctor = parseDoctorResponseJSON(response);
                populateFields(doctor);

                setLatLng(doctor.getFullAddress());
                if (mLatitude != 0 && mLongitude !=0) {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;

                            LatLng address = new LatLng(mLatitude, mLongitude);
                            mMap.addMarker(new MarkerOptions().position(address).title(doctor.getAddressStreet()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(address));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                        }
                    });
                }
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

    private Doctor parseDoctorResponseJSON(String response) {
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

    private void setLatLng(String address) {
        Geocoder geocoder = new Geocoder(this);

        try {
            ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocationName
                    (address, 1);
            if (addresses.size() > 0) {
                mLongitude = addresses.get(0).getLongitude();
                mLatitude = addresses.get(0).getLatitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRatingsFromService() {
        String patientUrl = URLHelper.GET_DOCTOR_OPINIONS_SUMMARY.replace(":id", mDoctorId);
        StringRequest request = new StringRequest(Request.Method.GET, patientUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseRatingsResponseJSON(response);
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

    private void parseRatingsResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OpinionSummary summary = null;
        try {
            summary = objectMapper.readValue(response, OpinionSummary.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (summary != null) {
            if (summary.getGeneralRating() != null)
                mRatingGeneral.setRating(Float.parseFloat(summary.getGeneralRating()));
            if (summary.getNumberOfEvaluations() != null)
                mOptionsCount.setText("Baseado em " + summary.getNumberOfEvaluations() + " opiniões");
            if (summary.getPunctualityRating() != null)
                mRatingPunctuality.setRating(Float.parseFloat(summary.getPunctualityRating()));
            if (summary.getAttentionRating() != null)
                mRatingAttention.setRating(Float.parseFloat(summary.getAttentionRating()));
            if (summary.getInstallationRating() != null)
                mRatingLocation.setRating(Float.parseFloat(summary.getInstallationRating()));
        }
    }

    private void  createScheduleAppointmentDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewDoctorProfileActivity.this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_schedule_appointment, null);
        alertDialog.setTitle(R.string.schedule_appointment);
        alertDialog.setView(dialogView);

        mAppointmentDate = (TextView) dialogView.findViewById(R.id.text_date_appointment);
        mDialogWeekdaySpinner = (Spinner) dialogView.findViewById(R.id.spinner_schedule_time);

        ImageView btnDatePicker = (ImageView) dialogView.findViewById(R.id.btn_show_date_picker_appointment);
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAvailabilityFromService();

            }
        });

        alertDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                makeAppointment(mDoctorId, mPatientId, mAvailabilityHours.get(mDialogWeekdaySpinner.getSelectedItemPosition()).get_id(), mAvailabilityDate);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, null);

        alertDialog.show();
    }

    private void getAvailabilityFromService() {
        String url = URLHelper.GET_DOCTOR_AVAILABILITY.replace(":id", mDoctorId);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Availability[] availabilities = parseDoctorAvailabilityJSON(response);

                mAvailabilityMap = new HashMap<>();
                for (Availability availability : availabilities) {
                    if (mAvailabilityMap.get(availability.getWeekday()) != null) {
                        mAvailabilityMap.get(availability.getWeekday()).add(availability);
                    } else {
                        mAvailabilityMap.put(availability.getWeekday(), new ArrayList<>(Arrays.asList(availability)));
                    }
                }

                if (mAvailabilityMap.size() > 0) {
                    createCalendar(mAvailabilityMap);
                }
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

    private void createCalendar(HashMap<String, List<Availability>> availabilityMap) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                ViewDoctorProfileActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        Calendar[] daysArray = calculateDaysAvailable(availabilityMap);

        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.setSelectableDays(daysArray);
        datePickerDialog.vibrate(false);

        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }

    private Calendar[] calculateDaysAvailable(HashMap<String, List<Availability>> availabilityMap) {
        List<Calendar> daysList = new LinkedList<>();
        Calendar[] daysArray;
        Calendar calendar = Calendar.getInstance();
        Calendar cMax = Calendar.getInstance();
        cMax.set(calendar.get(Calendar.YEAR)+2, 11, 31);

        ArrayList<Integer> days = new ArrayList<>();
        for (String day : availabilityMap.keySet()) {
            days.add(WeekDayHelper.mWeekDaysCalendarMap.get(day));
        }

        while (calendar.getTimeInMillis() <= cMax.getTimeInMillis()) {
            if (days.contains(calendar.get(Calendar.DAY_OF_WEEK))) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(calendar.getTimeInMillis());

                daysList.add(c);
            }
            calendar.setTimeInMillis(calendar.getTimeInMillis() + (24 * 60 * 60 * 1000));
        }
        daysArray = new Calendar[daysList.size()];
        for (int i = 0; i < daysArray.length; i++) {
            daysArray[i] = daysList.get(i);
        }
        return daysArray;
    }

    private Availability[] parseDoctorAvailabilityJSON(String response) {
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

    private void makeAppointment(final String doctorId, final String patientId, final String availabilityId, final String date) {
        String url = URLHelper.ADD_DOCTOR_APPOINTMENT.replace(":id", mDoctorId);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (parseResponseJSON(response)) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Consulta marcada", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    loadRatingsFromService();
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Ocorreu um erro ao marcar sua consulta", Snackbar.LENGTH_LONG);
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
                params.put("doctorId", doctorId);
                params.put("patientId", patientId);
                params.put("availabilityId", availabilityId);
                params.put("date", date);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private void createEvaluationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewDoctorProfileActivity.this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_evaluate_doctor, null);
        alertDialog.setTitle(R.string.evaluate);
        alertDialog.setView(dialogView);
        mPunctualityRating = (AppCompatRatingBar) dialogView.findViewById(R.id.ratingPunctuality_evaluate);
        mAttentionRating = (AppCompatRatingBar) dialogView.findViewById(R.id.ratingAttention_evaluate);
        mLocationRating = (AppCompatRatingBar) dialogView.findViewById(R.id.ratingLocation_evaluate);
        mEvaluationDescription = (EditText) dialogView.findViewById(R.id.input_description_evaluate);

        alertDialog.setPositiveButton(R.string.evaluate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String punctualityRating = String.valueOf(mPunctualityRating.getRating());
                String attentionRating = String.valueOf(mAttentionRating.getRating());
                String installationRating = String.valueOf(mLocationRating.getRating());
                String description = mEvaluationDescription.getText().toString();

                saveEvaluation(punctualityRating, attentionRating, installationRating, description);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, null);

        alertDialog.show();
    }

    private void saveEvaluation(final String punctualityRating, final String attentionRating,
                                final String installationRating, final String description) {
        String url = URLHelper.SAVE_DOCTOR_OPINIONS.replace(":id", mDoctorId);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (parseResponseJSON(response)) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Opinião adicionada", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    loadRatingsFromService();
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Ocorreu um erro ao adicionar sua opinião", Snackbar.LENGTH_LONG);
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
                params.put("punctualityRating", punctualityRating);
                params.put("attentionRating", attentionRating);
                params.put("installationRating", installationRating);
                params.put("comment", description);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private void createEvaluationListDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewDoctorProfileActivity.this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.body_part_problems_list, null);
        alertDialog.setTitle(R.string.evaluations);
        alertDialog.setView(dialogView);

        getOpinionsFromService(dialogView);

        alertDialog.setPositiveButton(R.string.go_back, null);

        alertDialog.show();

    }

    private boolean parseResponseJSON(String response) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> message = null;
        try {
            message = mapper.readValue(response, new TypeReference<Map<String,String>>() { });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message != null && !TextUtils.isEmpty(message.get("success")) && message.get("success").equals("true");
    }

    private void getOpinionsFromService(final View dialogView) {
        String url = URLHelper.GET_DOCTOR_OPINIONS.replace(":id", mDoctorId);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                Opinion[] opinions = null;
                try {
                    opinions = objectMapper.readValue(response, Opinion[].class);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (opinions != null) {
                    OpinionsListAdapter list = new OpinionsListAdapter(ViewDoctorProfileActivity.this, Arrays.asList(opinions));
                    ListView lv = (ListView) dialogView.findViewById(R.id.list_body_part_problems);
                    lv.setAdapter(list);
                }
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mAvailabilityDate = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        mAppointmentDate.setText(date);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        String weekDay = WeekDayHelper.mWeekCalendarDaysMap.get(calendar.get(Calendar.DAY_OF_WEEK));
        mAvailabilityHours = mAvailabilityMap.get(weekDay);

        ArrayAdapter<Availability> adapter = new ArrayAdapter<>(ViewDoctorProfileActivity.this, android.R.layout.simple_spinner_item, mAvailabilityHours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDialogWeekdaySpinner.setAdapter(adapter);
    }
}
