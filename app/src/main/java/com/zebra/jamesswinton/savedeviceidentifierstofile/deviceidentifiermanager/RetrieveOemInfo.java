package com.zebra.jamesswinton.savedeviceidentifierstofile.deviceidentifiermanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.zebra.jamesswinton.savedeviceidentifierstofile.R;
import com.zebra.jamesswinton.savedeviceidentifierstofile.utilities.CustomDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetrieveOemInfo extends AsyncTask<Void, Void, Void> {

    // debugging
    private static final String TAG = "RetrieveOemInfo";

    // Main Thread Handler
    private Handler mHandler = new Handler(Looper.getMainLooper());

    // Variables
    private WeakReference<Context> mContextWeakRef;
    private Uri[] mContentProviderUris;
    private OnOemInfoRetrievedListener mOnOemInfoRetrievedListener;
    private AlertDialog mProgressDialog;

    public RetrieveOemInfo(Context context, Uri[] contentProviderUris,
                           OnOemInfoRetrievedListener onOemInfoRetrievedListener) {
        this.mContextWeakRef = new WeakReference<>(context);
        this.mContentProviderUris = contentProviderUris;
        this.mOnOemInfoRetrievedListener = onOemInfoRetrievedListener;
        this.mProgressDialog = CustomDialog.buildLoadingDialog(mContextWeakRef.get(),
                "Obtaining OEM Info...", false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mHandler.post(() -> mProgressDialog.show());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Context cx = mContextWeakRef.get();
        if (cx != null) {
            // Init Holder
            Map<String, String> oemIdentifiers = new HashMap<>();

            // Loop Content Providers
            for (Uri contentProviderUri : mContentProviderUris) {
                // Grab Cursor using URI
                Cursor cursor = cx.getContentResolver().query(contentProviderUri, null,
                        null, null, null);

                // Validate Permissions
                if (cursor == null || cursor.getCount() < 1) {
                    mHandler.post(() -> mOnOemInfoRetrievedListener.onPermissionError(
                            cx.getString(R.string.permissions_error_message)));
                    return null;
                }

                // Loop Cursor
                while (cursor.moveToNext()) {
                    // Validate Cursor
                    if (cursor.getColumnCount() == 0) {
                        mHandler.post(() -> mOnOemInfoRetrievedListener.onUnknownError(
                                cx.getString(R.string.permissions_dialog_message)));
                    } else {
                        // Loop Cursor Columns
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            try {
                                oemIdentifiers.put(cursor.getColumnName(i), cursor.getString(
                                        cursor.getColumnIndex(cursor.getColumnName(i))));
                            } catch (Exception e) {
                                mHandler.post(() ->
                                        mOnOemInfoRetrievedListener.onUnknownError(e.getMessage()));
                            }
                        }
                    }
                }

                // Close Cursor
                cursor.close();
            }

            // Return Values
            mHandler.post(() -> mOnOemInfoRetrievedListener.onDetailsRetrieved(oemIdentifiers));
        } else {
            Log.e(TAG, "Could not access valid context, quitting.");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mHandler.post(() -> mProgressDialog.dismiss());
    }

    public interface OnOemInfoRetrievedListener {
        void onDetailsRetrieved(Map<String, String> oemIdentifiers);
        void onPermissionError(String e);
        void onUnknownError(String e);
    }

}
