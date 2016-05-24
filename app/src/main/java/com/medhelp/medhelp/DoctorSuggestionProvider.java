package com.medhelp.medhelp;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DoctorSuggestionProvider extends ContentProvider{

    List<String> mDoctors;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        if (mDoctors == null || mDoctors.isEmpty()){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://raw.githubusercontent.com/CarlosRodrigo/project-euler/master/suggestions.json")
                    .build();

            mDoctors = getSuggestionsFromService(client, request);
        }

        MatrixCursor cursor = new MatrixCursor(
                new String[] {
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                }
        );

        if (mDoctors != null) {
            buildCursor(uri, cursor);
        }

        return cursor;
    }

    private ArrayList getSuggestionsFromService(OkHttpClient client, Request request) {
        ArrayList<String> doctors = new ArrayList<>();

        try {
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                String doctor = jsonArray.getString(i);
                doctors.add(doctor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return doctors;
    }

    private void buildCursor(Uri uri, MatrixCursor cursor) {
        String query = uri.getLastPathSegment().toUpperCase();
        int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

        int lenght = mDoctors.size();
        for (int i = 0; i < lenght && cursor.getCount() < limit; i++) {
            String doctor = mDoctors.get(i);
            if (doctor.toUpperCase().contains(query)){
                cursor.addRow(new Object[]{ i, doctor, doctor });
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
