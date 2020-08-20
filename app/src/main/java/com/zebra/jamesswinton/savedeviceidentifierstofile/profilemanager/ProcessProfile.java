package com.zebra.jamesswinton.savedeviceidentifierstofile.profilemanager;

import android.os.AsyncTask;
import android.util.Log;

import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;
import com.symbol.emdk.ProfileManager.PROFILE_FLAG;

import static com.symbol.emdk.EMDKResults.STATUS_CODE.CHECK_XML;
import static com.symbol.emdk.EMDKResults.STATUS_CODE.FAILURE;
import static com.symbol.emdk.EMDKResults.STATUS_CODE.SUCCESS;

public class ProcessProfile extends AsyncTask<String, Void, EMDKResults> {

  // Debugging
  private static final String TAG = "ProcessProfile";

  // Constants


  // Static Variables


  // Non-Static Variables
  private String mProfileName;
  private ProfileManager mProfileManager;
  private OnProfileApplied mOnProfileApplied;

  public ProcessProfile(String profileName, ProfileManager profileManager, OnProfileApplied onProfileApplied) {
    this.mProfileName = profileName;
    this.mProfileManager = profileManager;
    this.mOnProfileApplied = onProfileApplied;
  }

  @Override
  protected EMDKResults doInBackground(String... params) {
    // Execute Profile
    return mProfileManager.processProfile(mProfileName, PROFILE_FLAG.SET, params);
  }

  @Override
  protected void onPostExecute(EMDKResults results) {
    super.onPostExecute(results);
    // Log Result
    Log.i(TAG, "Profile Manager Result: " + results.statusCode + " | " + results.extendedStatusCode);

    // Notify Class
    if (results.statusCode.equals(CHECK_XML) || results.statusCode.equals(SUCCESS)) {
      Log.i(TAG, "XML: " + results.getStatusString());
      mOnProfileApplied.profileApplied(results.statusCode.toString(),
              results.extendedStatusCode.toString());
    } else if (results.statusCode.equals(FAILURE)) {
      mOnProfileApplied.profileError(results.statusCode.toString(),
              results.extendedStatusCode.toString());
    }
  }
}
