package com.zebra.jamesswinton.savedeviceidentifierstofile.utilities;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public class IniWriter {

    // File Path
    public static final String FILE_NAME = "identifiers.ini";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory()
            + File.separator + FILE_NAME;

    // INI Sections
    private static final String SECTION_IDENTIFIERS = "IDENTIFIERS";
    private static final String KEY_IMEI = "IMEI";
    private static final String KEY_SERIAL = "SERIAL";

    // Variables
    private Wini mWini = null;

    public IniWriter() {
        try {
            File iniFile = new File(FILE_PATH);
            if (iniFile.exists()) {
                mWini = new Wini(iniFile);
            } else {
                if (iniFile.createNewFile()) {
                    mWini = new Wini(iniFile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean writeIdentifiersToFile(@Nullable String imei, @Nullable String serial) {
        // Verify Wini
        if (mWini == null) {
            Log.e("IniWriter", "Wini instance is null");
            return false;
        }

        // Start Up
        if (imei != null) mWini.put(SECTION_IDENTIFIERS, KEY_IMEI, imei);
        if (serial != null) mWini.put(SECTION_IDENTIFIERS, KEY_SERIAL, serial);

        // Apply
        try {
            mWini.store();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } return true;
    }
}
