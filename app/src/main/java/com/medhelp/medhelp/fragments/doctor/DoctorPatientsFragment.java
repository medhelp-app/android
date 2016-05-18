package com.medhelp.medhelp.fragments.doctor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medhelp.medhelp.R;

public class DoctorPatientsFragment extends Fragment {


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
        return inflater.inflate(R.layout.fragment_doctor_patients, container, false);
    }
}
