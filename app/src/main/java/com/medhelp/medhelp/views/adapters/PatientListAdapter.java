package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ImageLoaderHelper;
import com.medhelp.medhelp.model.Appointment;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Appointment> appointments;

    public PatientListAdapter(Activity activity, List<Appointment> appointments) {
        this.activity = activity;
        this.appointments = appointments;
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int location) {
        return appointments.get(location);
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
            view = inflater.inflate(R.layout.patient_item, null);

        CircleImageView profileImage = (CircleImageView) view.findViewById(R.id.profilePic_patientItem);

        TextView name = (TextView) view.findViewById(R.id.name_patientItem);
        TextView date = (TextView) view.findViewById(R.id.appointment_date_patientItem);
        TextView hour = (TextView) view.findViewById(R.id.appointment_hour_patientItem);

        Appointment appointment = appointments.get(i);

        if (appointment.getUser().getProfileImage() != null) {
            ImageLoaderHelper image = new ImageLoaderHelper(new WeakReference<>(profileImage));
            image.execute(appointment.getUser().getProfileImage());
        }
        name.setText(appointment.getUser().getName());
        date.setText(appointment.getDate());
        hour.setText(appointment.getAvailabilityId().toString());

        return view;
    }

}
