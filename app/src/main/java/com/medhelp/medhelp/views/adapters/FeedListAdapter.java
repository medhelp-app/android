package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.medhelp.medhelp.R;
import com.medhelp.medhelp.model.FeedItem;

import java.util.Calendar;
import java.util.List;

public class FeedListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
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
            view = inflater.inflate(R.layout.feed_item, null);

        TextView name = (TextView) view.findViewById(R.id.text_userName_feedItem);

        TextView timestamp = (TextView) view
                .findViewById(R.id.date_timestamp_feedItem);
        TextView statusMsg = (TextView) view
                .findViewById(R.id.text_publication_feedItem);
        NetworkImageView profilePic = (NetworkImageView) view
                .findViewById(R.id.image_profilePic_feedItem);


        FeedItem item = feedItems.get(i);

        name.setText(item.getName());

        CharSequence timeAgo = parseDate(item.getDate());
        timestamp.setText(timeAgo);

        if (!TextUtils.isEmpty(item.getText())) {
            statusMsg.setText(item.getText());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            statusMsg.setVisibility(View.GONE);
        }

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
