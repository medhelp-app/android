package com.medhelp.medhelp.activities.patient;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

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
import com.medhelp.medhelp.model.BodyPart;
import com.medhelp.medhelp.model.Problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientHumanBodyActivity extends Activity {

    private String mPatientId = "574b6303d4e2f40300154fda";
    private BodyPart[] mBodyParts;

    private ImageButton mBodyImage;
    private ImageButton mHeadImage;
    private ImageButton mChestImage;
    private ImageButton mAbsImage;
    private ImageButton mRightArmImage;
    private ImageButton mLeftArmImage;
    private ImageButton mRightLegImage;
    private ImageButton mLeftLegImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_human_body);

        mPatientId = (String) getIntent().getSerializableExtra("patientId");

        initFields();

        getBodyPartsFromService();

        setBodyImages();

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
    }

    private void getBodyPartTouch(float x, float y) {
        //Head
        if (x > 230 && y > 30 && x < 430 && y < 180) {
            List<Problem> problems = mBodyParts[6].getProblems();
            createProblemsListDialog(problems);
        }

        //Chest
        else if (x > 200 && y > 180 && x < 450 && y < 350) {
            List<Problem> problems = mBodyParts[5].getProblems();
            createProblemsListDialog(problems);
        }

        //Abs
        else if (x > 200 && y > 180 && x < 450 && y < 520) {
            List<Problem> problems = mBodyParts[4].getProblems();
            createProblemsListDialog(problems);
        }

        // RightArm
        else if (x > 30 && y > 180 && x < 150 && y < 620) {
            List<Problem> problems = mBodyParts[0].getProblems();
            createProblemsListDialog(problems);
        }//LeftArm
        else if (x > 460 && y > 180 && x < 630 && y < 620) {
            List<Problem> problems = mBodyParts[1].getProblems();
            createProblemsListDialog(problems);
        }

        //RightLeg
        else if (x > 200 && y > 520 && x < 300 && y < 1000) {
            List<Problem> problems = mBodyParts[2].getProblems();
            createProblemsListDialog(problems);
        }//LeftLeg
        else if (x > 300 && y > 520 && x < 460 && y < 1000) {
            List<Problem> problems = mBodyParts[3].getProblems();
            createProblemsListDialog(problems);
        }
    }

    private void createProblemsListDialog(List<Problem> problems) {
        List<String> problemsText = new ArrayList<>();
        for (Problem problem : problems) {
            problemsText.add(problem.getProblem());
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PatientHumanBodyActivity.this);
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
                mBodyParts = parseResponseJSON(response);
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

        return parts;
    }

    private void setBodyImages() {
        mHeadImage.setImageResource(R.drawable.body_head_red);
        mChestImage.setImageResource(R.drawable.body_chest_yellow);
        mAbsImage.setImageResource(R.drawable.body_abs_red);
        mRightArmImage.setImageResource(R.drawable.body_right_arm_red);
        mLeftArmImage.setImageResource(R.drawable.body_left_arm_red);
        mRightLegImage.setImageResource(R.drawable.body_right_leg_yellow);
        mLeftLegImage.setImageResource(R.drawable.body_left_leg_yellow);
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

}
