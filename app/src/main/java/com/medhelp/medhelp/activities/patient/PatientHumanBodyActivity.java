package com.medhelp.medhelp.activities.patient;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
import com.medhelp.medhelp.helpers.BodyPartHelper;
import com.medhelp.medhelp.helpers.URLHelper;
import com.medhelp.medhelp.model.BodyPart;
import com.medhelp.medhelp.model.EBodyType;
import com.medhelp.medhelp.model.ESeverityProblem;
import com.medhelp.medhelp.model.Problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientHumanBodyActivity extends Activity {

    private String mPatientId;
    private HashMap<String, BodyPart> mBodyParts = new HashMap<>();

    private ImageButton mBodyImage;
    private ImageButton mHeadImage;
    private ImageButton mChestImage;
    private ImageButton mAbsImage;
    private ImageButton mRightArmImage;
    private ImageButton mLeftArmImage;
    private ImageButton mRightLegImage;
    private ImageButton mLeftLegImage;

    private EditText mDialogProblemText;
    private EditText mDialogDescriptionText;
    private RadioGroup mDialogSeverityRadio;
    private RadioButton mDialogLowButton;
    private RadioButton mDialogMediumButton;
    private RadioButton mDialogHighButton;
    private Spinner mDialogBodyParts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_human_body);

        mPatientId = (String) getIntent().getSerializableExtra("patientId");

        initFields();

        getBodyPartsFromService();

        mLeftLegImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        getBodyPartTouch(x, y);
                        break;
                }

                return false;
            }
        });

        FloatingActionButton fabAddProblem = (FloatingActionButton) findViewById(R.id.fab_add_problem_humanBody);
        fabAddProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddProblemDialog();
            }
        });
    }

    private void initFields() {
        mBodyImage = (ImageButton) findViewById(R.id.humanBody);
        mHeadImage = (ImageButton) findViewById(R.id.head_humanBody);
        mChestImage = (ImageButton) findViewById(R.id.chest_humanBody);
        mAbsImage = (ImageButton) findViewById(R.id.abs_humanBody);
        mRightArmImage = (ImageButton) findViewById(R.id.right_arm_humanBody);
        mLeftArmImage = (ImageButton) findViewById(R.id.left_arm_humanBody);
        mRightLegImage = (ImageButton) findViewById(R.id.right_leg_humanBody);
        mLeftLegImage = (ImageButton) findViewById(R.id.left_leg_humanBody);
    }

    private void getBodyPartTouch(float x, float y) {
        //Head
        if (x > 230 && y > 30 && x < 430 && y < 180) {
            List<Problem> problems = mBodyParts.get(EBodyType.Head.getValue()).getProblems();
            createProblemsListDialog(problems);
        }

        //Chest
        else if (x > 200 && y > 180 && x < 450 && y < 350) {
            List<Problem> problems = mBodyParts.get(EBodyType.Chest.getValue()).getProblems();
            createProblemsListDialog(problems);
        }

        //Abs
        else if (x > 200 && y > 180 && x < 450 && y < 520) {
            List<Problem> problems = mBodyParts.get(EBodyType.Abs.getValue()).getProblems();
            createProblemsListDialog(problems);
        }

        // RightArm
        else if (x > 30 && y > 180 && x < 150 && y < 620) {
            List<Problem> problems = mBodyParts.get(EBodyType.RightArm.getValue()).getProblems();
            createProblemsListDialog(problems);
        }//LeftArm
        else if (x > 460 && y > 180 && x < 630 && y < 620) {
            List<Problem> problems = mBodyParts.get(EBodyType.LeftArm.getValue()).getProblems();
            createProblemsListDialog(problems);
        }

        //RightLeg
        else if (x > 200 && y > 520 && x < 300 && y < 1000) {
            List<Problem> problems = mBodyParts.get(EBodyType.RightLeg.getValue()).getProblems();
            createProblemsListDialog(problems);
        }//LeftLeg
        else if (x > 300 && y > 520 && x < 460 && y < 1000) {
            List<Problem> problems = mBodyParts.get(EBodyType.LeftLeg.getValue()).getProblems();
            createProblemsListDialog(problems);
        }
    }

    private void createProblemsListDialog(List<Problem> problems) {
        List<String> problemsText = new ArrayList<>();
        for (Problem problem : problems) {
            problemsText.add(problem.getProblem());
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PatientHumanBodyActivity.this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        View convertView = inflater.inflate(R.layout.body_part_problems_list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Problemas");

        ListView lv = (ListView) convertView.findViewById(R.id.list_body_part_problems);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PatientHumanBodyActivity.this, android.R.layout.simple_list_item_1, problemsText);
        lv.setAdapter(adapter);
        alertDialog.show();
    }

    private void getBodyPartsFromService() {
        String url = URLHelper.GET_PATIENT_BODY_PARTS_URL.replace(":id", mPatientId);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseResponseJSON(response);
                setBodyImages();
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

    private BodyPart[] parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        BodyPart[] parts = null;
        try {
            parts = objectMapper.readValue(response, BodyPart[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (parts != null) {
            for (BodyPart part : parts)
                mBodyParts.put(part.getPart(), part);
        }

        return parts;
    }

    private void setBodyImages() {
        if (mBodyParts.get(EBodyType.Head.getValue()).getProblems().size() > 0) {
            colorBodyPart(mBodyParts.get(EBodyType.Head.getValue()), mHeadImage,
                    R.drawable.body_head_red, R.drawable.body_head_yellow);
        }

        if (mBodyParts.get(EBodyType.Chest.getValue()).getProblems().size() > 0) {
            colorBodyPart(mBodyParts.get(EBodyType.Chest.getValue()), mChestImage,
                    R.drawable.body_chest_red, R.drawable.body_chest_yellow);
        }

        if (mBodyParts.get(EBodyType.Abs.getValue()).getProblems().size() > 0) {
            colorBodyPart(mBodyParts.get(EBodyType.Abs.getValue()), mAbsImage,
                    R.drawable.body_abs_red, R.drawable.body_abs_yellow);
        }

        if (mBodyParts.get(EBodyType.RightArm.getValue()).getProblems().size() > 0) {
            colorBodyPart(mBodyParts.get(EBodyType.RightArm.getValue()), mRightArmImage,
                    R.drawable.body_right_arm_red, R.drawable.body_right_arm_yellow);
        }

        if (mBodyParts.get(EBodyType.LeftArm.getValue()).getProblems().size() > 0) {
            colorBodyPart(mBodyParts.get(EBodyType.LeftArm.getValue()), mLeftArmImage,
                    R.drawable.body_left_arm_red, R.drawable.body_left_arm_yellow);
        }

        if (mBodyParts.get(EBodyType.RightLeg.getValue()).getProblems().size() > 0) {
            colorBodyPart(mBodyParts.get(EBodyType.RightLeg.getValue()), mRightLegImage,
                    R.drawable.body_right_leg_red, R.drawable.body_right_leg_yellow);
        }

        if (mBodyParts.get(EBodyType.LeftLeg.getValue()).getProblems().size() > 0) {
            colorBodyPart(mBodyParts.get(EBodyType.LeftLeg.getValue()), mLeftLegImage,
                    R.drawable.body_left_leg_red, R.drawable.body_left_leg_yellow);
        }
    }

    private void colorBodyPart(BodyPart bodyPart, ImageButton bodyPartImage,
                               int redDrawable, int yellowDrawable) {
        ESeverityProblem severityProblem = getBodyColorFromSeverity(bodyPart.getProblems());
        if (severityProblem == ESeverityProblem.High) {
            bodyPartImage.setImageResource(redDrawable);
        } else if (severityProblem == ESeverityProblem.Medium) {
            bodyPartImage.setImageResource(yellowDrawable);
        }
    }

    private ESeverityProblem getBodyColorFromSeverity(List<Problem> problems) {
        ESeverityProblem severityProblem = ESeverityProblem.Low;
        for (Problem problem : problems) {
            if (ESeverityProblem.High.getValue().equals(problem.getSeverity())) {
                return ESeverityProblem.High;
            } else if(ESeverityProblem.Medium.getValue().equals(problem.getSeverity())) {
                severityProblem = ESeverityProblem.Medium;
            }
        }
        return severityProblem;
    }

    private void createAddProblemDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PatientHumanBodyActivity.this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_body_part_problem, null);
        alertDialog.setView(dialogView);

        mDialogBodyParts = (Spinner) dialogView.findViewById(R.id.spinner_body_part);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PatientHumanBodyActivity.this, R.array.body_parts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDialogBodyParts.setAdapter(adapter);

        mDialogProblemText = (EditText) dialogView.findViewById(R.id.input_problem);
        mDialogDescriptionText = (EditText) dialogView.findViewById(R.id.input_description);
        mDialogSeverityRadio = (RadioGroup) dialogView.findViewById(R.id.group_severity_problem);
        mDialogLowButton = (RadioButton) dialogView.findViewById(R.id.radio_severity_low);
        mDialogMediumButton = (RadioButton) dialogView.findViewById(R.id.radio_severity_medium);
        mDialogHighButton = (RadioButton) dialogView.findViewById(R.id.radio_severity_high);

        alertDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String severityProblem = getSelectedSeverity(mDialogSeverityRadio, mDialogLowButton, mDialogMediumButton, mDialogHighButton);
                String part = mDialogBodyParts.getSelectedItem().toString();
                String problem = mDialogProblemText.getText().toString();
                String description = mDialogDescriptionText.getText().toString();

                callService(BodyPartHelper.mBodyPartsMap.get(part).getValue(), problem, description, severityProblem);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, null);

        alertDialog.create();
        alertDialog.show();
    }

    private String getSelectedSeverity(RadioGroup severityRadio, RadioButton lowButton, RadioButton mediumButton, RadioButton highButton) {
        if(severityRadio.getCheckedRadioButtonId() == lowButton.getId()) {
            return ESeverityProblem.Low.getValue();
        } else if (severityRadio.getCheckedRadioButtonId() == mediumButton.getId()) {
            return ESeverityProblem.Medium.getValue();
        } else if (severityRadio.getCheckedRadioButtonId() == highButton.getId()) {
            return ESeverityProblem.High.getValue();
        }
        return ESeverityProblem.Low.getValue();
    }

    private void callService(final String part, final String problem, final String description, final String severity) {
        String url = URLHelper.ADD_PATIENT_BODY_PART_PROBLEM_URL.replace(":id", mPatientId);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.fab_add_problem_humanBody), "Problema adicionado", Snackbar.LENGTH_LONG);
                snackbar.show();

                getBodyPartsFromService();
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
                params.put("part", part);
                params.put("problem", problem);
                params.put("description", description);
                params.put("severity", severity);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

}
