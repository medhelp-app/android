package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.model.Doctor;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Doctor> doctors;

    public DoctorListAdapter(Activity activity, List<Doctor> doctors) {
        this.activity = activity;
        this.doctors = doctors;
    }

    @Override
    public int getCount() {
        return doctors.size();
    }

    @Override
    public Object getItem(int location) {
        return doctors.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.doctor_item, null);

        CircleImageView profileImage = (CircleImageView) view.findViewById(R.id.profilePic_doctorItem);

        TextView name = (TextView) view.findViewById(R.id.name_doctorItem);
        TextView timestamp = (TextView) view
                .findViewById(R.id.email_doctorItem);

        Doctor doctor = doctors.get(i);

        if (doctor.getProfileImage() != null) {
            profileImage.setImageBitmap(ImageHelper.decodeBase64ToImage(doctor.getProfileImage()));
        }
        name.setText(doctor.getName());
        timestamp.setText(doctor.getEmail());

        return view;
    }
}
