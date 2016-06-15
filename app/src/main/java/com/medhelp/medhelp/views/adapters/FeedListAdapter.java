package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
import com.medhelp.medhelp.model.CommentItem;
import com.medhelp.medhelp.model.FeedItem;
import com.medhelp.medhelp.model.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;

    private User user;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems, User user) {
        this.activity = activity;
        this.feedItems = feedItems;
        this.user = user;
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

        CircleImageView profilePic = (CircleImageView) view.findViewById(R.id.image_profilePic_feedItem);
        TextView name = (TextView) view.findViewById(R.id.text_userName_feedItem);
        TextView timestamp = (TextView) view.findViewById(R.id.date_timestamp_feedItem);

        TextView text = (TextView) view.findViewById(R.id.text_publication_feedItem);

        final TextView agreeCount = (TextView) view.findViewById(R.id.text_agree_count_feedItem);
        final TextView disagreeCount = (TextView) view.findViewById(R.id.text_disagree_count_feedItem);
        final TextView commentsCount = (TextView) view.findViewById(R.id.text_comment_feedItem);

        final FeedItem item = feedItems.get(i);

//        if (item.getImageUser() != null && !item.getImageUser().isEmpty()) {
//            profilePic.setImageBitmap(ImageHelper.decodeBase64ToImage(item.getImageUser()));
//        }

        name.setText(item.getNameUser());

        CharSequence timeAgo = parseDate(item.getDate());
        timestamp.setText(timeAgo);

        agreeCount.setText(item.getAgree());
        disagreeCount.setText(item.getDisagree());
        commentsCount.setText(item.getComments() + " Comentários");

        if (!TextUtils.isEmpty(item.getText())) {
            text.setText(item.getText());
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }

        ImageButton agree = (ImageButton) view.findViewById(R.id.btn_agree_count_feedItem);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                votePublication(user.get_id(), item.get_id(), "agree");
            }
        });

        ImageButton disagree = (ImageButton) view.findViewById(R.id.btn_disagree_count_feedItem);
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                votePublication(user.get_id(), item.get_id(), "disagree");
            }
        });

        ImageButton commentBtn = (ImageButton) view.findViewById(R.id.btn_comment_feedItem);
        TextView commentText = (TextView) view.findViewById(R.id.text_comment_feedItem);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCommentsFromService(view, item.get_id());
            }
        });

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCommentsFromService(view, item.get_id());
            }
        });

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

    private void votePublication(final String idUser, final String idPublication, final String type) {
        String url = URLHelper.VOTE_PUBLICATION.replace(":id", idPublication);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                activity.recreate();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(calendar.getTimeInMillis());
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String date = format1.format(calendar.getTime());

                Map<String, String> params = new HashMap<>();
                params.put("idUser", idUser);
                params.put("idPublication", idPublication);
                params.put("type", type);
                params.put("date", date);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private void loadCommentsFromService(final View view, final String publicationId) {
        String url = URLHelper.GET_PUBLICATION_COMMENTS.replace(":id", publicationId);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<CommentItem> commentItems = parseResponseJSON(response);

                createCommentsDialog(commentItems, view, publicationId);
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

    private List<CommentItem> parseResponseJSON(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        CommentItem[] commentItems = new CommentItem[]{};
        try {
            commentItems = objectMapper.readValue(response, CommentItem[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(commentItems);
    }

    private void createCommentsDialog(List<CommentItem> commentItems, View view, final String publicationId) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext(), R.style.AppCompatAlertDialogStyle);
        View dialogView = View.inflate(view.getContext(), R.layout.dialog_publication_comments, null);
        alertDialog.setView(dialogView);

        CommentListAdapter listAdapter = new CommentListAdapter(activity, commentItems, user, publicationId);
        ListView listView = (ListView) dialogView.findViewById(R.id.list_publications_comments);
        listView.setAdapter(listAdapter);

        alertDialog.create();
        final AlertDialog dialog = alertDialog.show();

        Button comment = (Button) dialogView.findViewById(R.id.btn_new_publication_comment);
        final EditText newComment = (EditText) dialogView.findViewById(R.id.text_new_publication_comment);

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentPublication(user.get_id(), publicationId, newComment.getText().toString(), dialog);
            }
        });
    }

    private void commentPublication(final String idUser, final String idPublication, final String text, final AlertDialog alertDialog) {
        String url = URLHelper.ADD_PUBLICATION_COMMENT.replace(":id", idPublication);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                alertDialog.dismiss();
                activity.recreate();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(calendar.getTimeInMillis());
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String date = format1.format(calendar.getTime());

                Map<String, String> params = new HashMap<>();
                params.put("idUser", idUser);
                params.put("idPublication", idPublication);
                params.put("text", text);
                params.put("date", date);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

}
