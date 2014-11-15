package com.moods.bikersrides.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.moods.bikersrides.common.FragmentCallback;
import com.moods.bikersrides.database.dao.DaoSession;
import com.moods.bikersrides.database.vao.Ride;
import com.moods.bikersrides.database.vao.RideImage;
import com.moods.bikersrides.utils.ImageUtils;

import java.util.List;
import java.util.Random;

public class SaveImageAsync extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private List<RideImage> mSelectedRideImages;
    private String mPath;
    private Ride mRide;
    private DaoSession mDaoSession;
    private FragmentCallback mFragmentCallback;

    private ProgressDialog mProgressDialog;

    public SaveImageAsync(Context context, List<RideImage> selectedImages, Ride ride, DaoSession daoSession, FragmentCallback fragmentCallback) {
        mContext = context;
        mSelectedRideImages = selectedImages;
        mRide = ride;
        mDaoSession = daoSession;
        mFragmentCallback = fragmentCallback;
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
        //FIXME think about better solution (update instead of delete)
        mDaoSession.getRideImageDao().deleteInTx(mRide.getImages());
        for (RideImage rd : mSelectedRideImages) {
//            RideImage ri = new RideImage();
            Bitmap bm = ImageUtils.getOptimisedBitmap(rd.getImgPath());
            rd.setImgPath(ImageUtils.saveToInternalStorage(bm, mContext, "img" + new Random().nextInt()));
            rd.setRideId(mRide.getId());


            mDaoSession.insert(rd);

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
        Log.i(getClass().toString(), "ASYNC-TASK-SAVE-IMAGE");
        mFragmentCallback.onFinished();
    }
}
