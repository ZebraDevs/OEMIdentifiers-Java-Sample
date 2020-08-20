package com.zebra.jamesswinton.savedeviceidentifierstofile.utilities;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionsHelper {

    // Constants
    public static final int PERMISSIONS_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {
            WRITE_EXTERNAL_STORAGE
    };

    // Variables
    private Activity mActivity;
    private OnPermissionsResultListener mOnPermissionsResultListener;

    // Interfaces
    public interface OnPermissionsResultListener {
        void onPermissionsGranted();
    }

    public PermissionsHelper(@NonNull Activity activity,
                             @NonNull OnPermissionsResultListener onPermissionsResultListener) {
        this.mActivity = activity;
        this.mOnPermissionsResultListener = onPermissionsResultListener;
        forcePermissionsUntilGranted();
    }

    private void forcePermissionsUntilGranted() {
        if (checkStandardPermissions()) {
            mOnPermissionsResultListener.onPermissionsGranted();
        } else {
            requestStandardPermission();
        }
    }

    private boolean checkStandardPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PERMISSION_GRANTED) {
                permissionsGranted = false;
                break;
            }
        }
        return permissionsGranted;
    }

    private void requestStandardPermission() {
        ActivityCompat.requestPermissions(mActivity, PERMISSIONS, PERMISSIONS_REQUEST_CODE);
    }

    public void onRequestPermissionsResult() {
        forcePermissionsUntilGranted();
    }

}
