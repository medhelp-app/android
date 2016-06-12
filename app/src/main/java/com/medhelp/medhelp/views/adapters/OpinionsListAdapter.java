package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.model.Opinion;

import java.util.List;

public class OpinionsListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Opinion> opinions;

    public OpinionsListAdapter(Activity activity, List<Opinion> opinions) {
        this.activity = activity;
        this.opinions = opinions;
    }

    @Override
    public int getCount() {
        return opinions.size();
    }

    @Override
    public Object getItem(int location) {
        return opinions.get(location);
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
            view = inflater.inflate(R.layout.dialog_opinions_list, null);

        AppCompatRatingBar rating = (AppCompatRatingBar) view.findViewById(R.id.ratingGeneral_opinion);
        TextView comment = (TextView) view.findViewById(R.id.comment_opinion);

        Opinion opinion = opinions.get(i);

        if (opinion.getGeneralRating() != null) {
            rating.setRating(Float.parseFloat(opinion.getGeneralRating()));
        }
        if (opinion.getComment() != null) {
            comment.setText(opinion.getComment());
        }

        return view;
    }
}
