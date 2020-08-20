package com.zebra.jamesswinton.savedeviceidentifierstofile.profilemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;
import com.zebra.jamesswinton.savedeviceidentifierstofile.R;
import com.zebra.jamesswinton.savedeviceidentifierstofile.utilities.CustomDialog;

import org.apache.commons.codec.DecoderException;

public class PermissionsActivity extends AppCompatActivity implements EMDKManager.EMDKListener {

    // Debugging
    private static final String TAG = "PermissionsActivity";

    // Global Variables
    public static final String PERMISSIONS_GRANTED_EXTRA = "permissions-granted-extra";
    public static final String PERMISSIONS_STATUS_CODE = "permissions-status-code-extra";
    public static final String PERMISSIONS_EXTENDED_STATUS_CODE = "permissions-extended-status-code-extra";

    // Private Variables
    private EMDKManager mEmdkManager = null;
    private ProfileManager mProfileManager = null;

    // UI
    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        // Init Progress Dialog
        mProgressDialog = CustomDialog.buildLoadingDialog(this,
                "Applying MX XML to Grant Permissions...", false);
        mProgressDialog.show();

        // Init EMDK
        EMDKResults emdkManagerResults = EMDKManager.getEMDKManager(this, this);

        // Verify EMDK Manager
        if (emdkManagerResults == null || emdkManagerResults.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            // Log Error
            Log.e(TAG, "onCreate: Failed to get EMDK Manager -> " +
                    (emdkManagerResults == null ? "No Results Returned" : emdkManagerResults.statusCode));
            Toast.makeText(this, "Failed to get EMDK Manager!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release EMDK Manager Instance
        if (mEmdkManager != null) {
            mEmdkManager.release();
            mEmdkManager = null;
        }

        // Remove Progress
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * EMDK Methods
     */

    @Override
    public void onOpened(EMDKManager emdkManager) {
        // Assign EMDK Reference
        mEmdkManager = emdkManager;

        // Get Profile & Version Manager Instances
        mProfileManager = (ProfileManager) mEmdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

        // Apply Profile
        if (mProfileManager != null) {
            try {
                // Init XML
                XML permissionXml = new XML(this);

                // Process
                new ProcessProfile(XML.GRANT_SERIAL_PERMISSION_NAME, mProfileManager, onProfileApplied)
                        .execute(permissionXml.getSerialPermissionXml());

                // Process
                new ProcessProfile(XML.GRANT_IMEI_PERMISSION_NAME, mProfileManager, onProfileApplied)
                        .execute(permissionXml.getImeiPermissionXml());

            } catch (PackageManager.NameNotFoundException | DecoderException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Error Obtaining ProfileManager!");
            Toast.makeText(this, "Error Obtaining ProfileManager!", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onClosed() {
        // Release EMDK Manager Instance
        if (mEmdkManager != null) {
            mEmdkManager.release();
            mEmdkManager = null;
        }
    }

    /**
     * Callback
     */

    private OnProfileApplied onProfileApplied = new OnProfileApplied() {

        // Holder - this is needed because we can't apply two access manager permissions in a single profile
        int numberOfResults = 0;
        int numberOfPermissionsToGrant = 2;

        // Return Intent for StartActivityForResult
        Intent resultIntent = new Intent();

        @Override
        public void profileApplied(String statusCode, String extendedStatusCode) {
            // Update Results Holder
            if (++numberOfResults == numberOfPermissionsToGrant) {
                resultIntent.putExtra(PERMISSIONS_GRANTED_EXTRA, true);
                resultIntent.putExtra(PERMISSIONS_STATUS_CODE, statusCode);
                resultIntent.putExtra(PERMISSIONS_EXTENDED_STATUS_CODE, extendedStatusCode);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }

        @Override
        public void profileError(String statusCode, String extendedStatusCode) {
            resultIntent.putExtra(PERMISSIONS_GRANTED_EXTRA, false);
            resultIntent.putExtra(PERMISSIONS_STATUS_CODE, statusCode);
            resultIntent.putExtra(PERMISSIONS_EXTENDED_STATUS_CODE, extendedStatusCode);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    };
}