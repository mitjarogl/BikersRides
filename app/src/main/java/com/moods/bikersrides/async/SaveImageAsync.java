package com.moods.bikersrides.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.vao.Ride;
import com.moods.bikersrides.database.vao.RideImage;
import com.moods.bikersrides.utils.ImageUtils;

import java.util.List;
import java.util.Random;

public class SaveImageAsync extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private List<Bitmap> mSelectedImages;
    private String mPath;
    private Ride mRide;
    private DaoSession mDaoSession;

    private ProgressDialog mProgressDialog;

    public SaveImageAsync(Context context, List<Bitmap> selectedImages, Ride ride, DaoSession daoSession) {
        mContext = context;
        mSelectedImages = selectedImages;
        mRide = ride;
        mDaoSession = daoSession;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Saving ride");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(Void... filePath) {
        Log.d("ASYNC-TASK-SAVE-IMAGE", "working");
        for(Bitmap bm : mSelectedImages) {
            RideImage ri = new RideImage();
            ri.setImgPath(ImageUtils.saveToInternalStorage(bm, mContext, "img" + new Random().nextInt()));
            ri.setRideId(mRide.getId());
            mDaoSession.insert(ri);
        }
        return mPath;
    }

    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String filename) {
        super.onPostExecute(filename);
        mProgressDialog.dismiss();
        mProgressDialog = null;
        mPath = filename;
        Log.d("ASYNC-TASK-SAVE-IMAGE", "finished");
    }
}
