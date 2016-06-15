package com.medhelp.medhelp.views.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.medhelp.medhelp.R;
import com.medhelp.medhelp.helpers.ImageHelper;
import com.medhelp.medhelp.model.Archive;

import java.util.List;

public class ArchiveListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Archive> archives;
    private Bitmap bitmap;

    public ArchiveListAdapter(Activity activity, List<Archive> doctors) {
        this.activity = activity;
        this.archives = doctors;
    }

    @Override
    public int getCount() {
        return archives.size();
    }

    @Override
    public Object getItem(int location) {
        return archives.get(location);
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
            view = inflater.inflate(R.layout.archive_item, null);

        ImageView archiveImage = (ImageView) view.findViewById(R.id.archive);

        Archive archive = archives.get(i);

        if (archive.getArchive() != null) {
            Bitmap img = ImageHelper.decodeBase64ToImage(archive.getArchive());
            if (img != null)
                bitmap = Bitmap.createScaledBitmap(img, 250, 250, false);
                archiveImage.setImageBitmap(bitmap);
        }

        archiveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogImage(view);
            }
        });

        return view;
    }

    private void showDialogImage(View view) {
        Dialog builder = new Dialog(view.getContext());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        ImageView imageView = new ImageView(view.getContext());
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 800, 800, false));
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

}