package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.model.CommentItem;
import com.medhelp.medhelp.model.User;

import java.util.Calendar;
import java.util.List;

public class CommentListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private List<CommentItem> commentItems;

    private User user;
    private String publicationId;

    public CommentListAdapter(Activity activity, List<CommentItem> commentItems, User user, String publicationId) {
        this.activity = activity;
        this.commentItems = commentItems;
        this.user = user;
        this.publicationId = publicationId;
    }

    @Override
    public int getCount() {
        return commentItems.size();
    }

    @Override
    public Object getItem(int i) {
        return commentItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.comment_item, null);

        CommentItem item = commentItems.get(i);

        TextView userName = (TextView) view.findViewById(R.id.text_userName_comment);
        TextView date = (TextView) view.findViewById(R.id.date_timestamp_comment);
        TextView publication = (TextView) view.findViewById(R.id.text_publication_comment);

        userName.setText(item.getNameUser());
        publication.setText(item.getText());
        CharSequence timeAgo = parseDate(item.getDate());
        date.setText(timeAgo);

        return view;
    }

    private CharSequence parseDate(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] dates = date.split("T");

        String[] day = dates[0].split("-");
        String[] hour = dates[1].split(":");
        calendar.set(Integer.parseInt(day[0]), Integer.parseInt(day[1]) - 1, Integer.parseInt(day[2]));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(hour[1]));

        return DateUtils.getRelativeTimeSpanString(
                Long.parseLong(String.valueOf(calendar.getTimeInMillis())),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}
