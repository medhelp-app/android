package com.medhelp.medhelp.activities.patient;

import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.medhelp.medhelp.R;

public class PatientHumanBodyActivity extends Activity {

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

        initFields();

        setBodyImages();

        mLeftLegImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                if (x > 230 && y > 30 && x < 430 && y < 180) {
                    Toast.makeText(getBaseContext(),"head", Toast.LENGTH_SHORT).show();
                }

                else if (x > 200 && y > 180 && x < 450 && y < 350) {
                    Toast.makeText(getBaseContext(),"chest", Toast.LENGTH_SHORT).show();
                }

                else if (x > 200 && y > 180 && x < 450 && y < 520) {
                    Toast.makeText(getBaseContext(),"abs", Toast.LENGTH_SHORT).show();
                }

                else if (x > 30 && y > 180 && x < 150 && y < 620) {
                    Toast.makeText(getBaseContext(),"rightArm", Toast.LENGTH_SHORT).show();
                } else if (x > 460 && y > 180 && x < 630 && y < 620) {
                    Toast.makeText(getBaseContext(),"leftArm", Toast.LENGTH_SHORT).show();
                }

                else if (x > 200 && y > 520 && x < 300 && y < 1000) {
                    Toast.makeText(getBaseContext(),"rightLeg", Toast.LENGTH_SHORT).show();
                } else if (x > 300 && y > 520 && x < 460 && y < 1000) {
                    Toast.makeText(getBaseContext(),"leftLeg", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(getBaseContext(), String.valueOf(motionEvent.getX()) + ", " + String.valueOf(motionEvent.getY()), Toast.LENGTH_LONG).show();
                return false;
            }
        });
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
