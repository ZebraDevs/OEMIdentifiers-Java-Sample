package com.zebra.jamesswinton.savedeviceidentifierstofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.zebra.jamesswinton.savedeviceidentifierstofile.deviceidentifiermanager.RetrieveOemInfo;
import com.zebra.jamesswinton.savedeviceidentifierstofile.profilemanager.PermissionsActivity;
import com.zebra.jamesswinton.savedeviceidentifierstofile.utilities.CustomDialog;
import com.zebra.jamesswinton.savedeviceidentifierstofile.utilities.IniWriter;
import com.zebra.jamesswinton.savedeviceidentifierstofile.utilities.PermissionsHelper;

import org.ini4j.Ini;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RetrieveOemInfo.OnOemInfoRetrievedListener {

    // Debugging
    private static final String TAG = "MainActivity";

    // Permissions Callback Code
    private PermissionsHelper mPermissionsHelper;
    private static final int PERMISSION_ACTIVITY_RESULT_CODE = 100;

    // Content URIs
    private static final String SERIAL_URI = "content://oem_info/oem.zebra.secure/build_serial";
    private static final String IMEI_URI = "content://oem_info/wan/imei";
    private static final Uri[] CONTENT_PROVIDER_URIS = {
            Uri.parse(SERIAL_URI),
            Uri.parse(IMEI_URI)
    };

    // Content Provider Keys
    private static final String IMEI = "imei";
    private static final String BUILD_SERIAL = "build_serial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Grab Permissions
        mPermissionsHelper = new PermissionsHelper(this, () -> {
            Log.i(TAG, "Permissions Granted");

            // Attempt to get Serial & IMEI from Content Providers
            new RetrieveOemInfo(this, CONTENT_PROVIDER_URIS, this)
                    .execute();
        });
    }

    /**
     *
     * OEM CALL BACKS
     *
     */

    @Override
    public void onDetailsRetrieved(Map<String, String> oemIdentifiers) {
        // Init Writer & Holders
        String imei = null;
        String buildSerial = null;
        IniWriter iniWriter = new IniWriter();

        // Grab Values
        for (String key : oemIdentifiers.keySet()) {
            Log.i(TAG, "OEM Info | " + key + oemIdentifiers.get(key));
            if (key.equals(IMEI)) {
                imei = oemIdentifiers.get(key);
            } else if (key.equals(BUILD_SERIAL)) {
                buildSerial = oemIdentifiers.get(key);
            }
        }

        // Write Values to Ini
        if (iniWriter.writeIdentifiersToFile(imei, buildSerial)) {
            CustomDialog.showCustomDialog(this, CustomDialog.DialogType.SUCCESS,
                    "Identifiers Saved",
                    "Identifiers successfully saved to file: " + IniWriter.FILE_PATH,
                    "OK", (dialogInterface, i) -> finish(),
                    null, null);
        } else {
            CustomDialog.showCustomDialog(this, CustomDialog.DialogType.ERROR,
                    "File Writing Failed!",
                    "Identifiers could not be saved to file, would you like to try again?",
                    "OK", (dialogInterface, i) -> finish(),
                    null, null);
        }
    }

    @Override
    public void onPermissionError(String e) {
        Log.w(TAG, "Permissions Error: " + e);
        CustomDialog.showCustomDialog(this, CustomDialog.DialogType.WARN,
                getString(R.string.permissions_dialog_title),
                getString(R.string.permissions_dialog_message),
                "OK", (dialogInterface, i) -> requestOemInfoPermissions(),
                "EXIT", (dialogInterface, i) -> finish());
    }

    @Override
    public void onUnknownError(String e) {
        Log.e(TAG, "Unknown Error: " + e);
        CustomDialog.showCustomDialog(this, CustomDialog.DialogType.ERROR,
                getString(R.string.unknown_error_dialog_title), e,
                "OK", (dialogInterface, i) -> finish(),
                null, null);
    }

    /**
     * Permissions Management
     */

    private void requestOemInfoPermissions() {
        startActivityForResult(new Intent(this, PermissionsActivity.class),
                PERMISSION_ACTIVITY_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_ACTIVITY_RESULT_CODE) {
            if (data != null) {
                boolean permissionsGranted = data.getBooleanExtra(
                        PermissionsActivity.PERMISSIONS_GRANTED_EXTRA, false);
                if (permissionsGranted) {
                    new RetrieveOemInfo(this, CONTENT_PROVIDER_URIS, this).execute();
                } else {
                    String statusCode = data.getStringExtra(PermissionsActivity.PERMISSIONS_STATUS_CODE);
                    String extendedStatusCode = data.getStringExtra(PermissionsActivity.PERMISSIONS_EXTENDED_STATUS_CODE);
                    CustomDialog.showCustomDialog(this, CustomDialog.DialogType.ERROR,
                            "Failed to Grant Permissions",
                            "Status Code: " + statusCode + " | \n\n Extended Status Code: " + extendedStatusCode,
                            "RETRY", (dialogInterface, i) -> startActivityForResult(new Intent(MainActivity.this, PermissionsActivity.class), PERMISSION_ACTIVITY_RESULT_CODE),
                            "QUIT", (dialogInterface, i) -> finish());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionsHelper.onRequestPermissionsResult();
    }
}