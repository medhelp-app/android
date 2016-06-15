package com.medhelp.medhelp.helpers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageLoaderHelper extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<CircleImageView> imageViewReference;

    public ImageLoaderHelper(WeakReference<CircleImageView> imageViewReference) {
        this.imageViewReference = imageViewReference;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String base64Image = strings[0];

        Bitmap bitmap = null;
        try {
            Bitmap img = ImageHelper.decodeBase64ToImage(base64Image);
            if (img != null)
                bitmap = Bitmap.createScaledBitmap(img, 100, 100, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}