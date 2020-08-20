package com.zebra.jamesswinton.savedeviceidentifierstofile.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;
import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import static android.content.pm.PackageManager.GET_SIGNATURES;
import static android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES;

public class PackageManagerHelper {
    @SuppressWarnings("deprecation")
    @SuppressLint("PackageManagerGetSignatures")
    public static Signature[] getSigningCertificateHex(Context cx)
            throws PackageManager.NameNotFoundException {
        Signature[] sigs;
        SigningInfo signingInfo;
        signingInfo = cx.getPackageManager().getPackageInfo(cx.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES).signingInfo;
        sigs = signingInfo.getApkContentsSigners();
        for (Signature sig : sigs)
        {
            Log.d("PackageManagerHelper", "Signature : " + sig.toCharsString() + " Length: " + sig.toCharsString().length());
        }
        return sigs;
    }

    public static String getSigningCertBase64(Context cx) throws PackageManager.NameNotFoundException, DecoderException {
        //convert String to char array (1st step)
        char[] charArray = getSigningCertificateHex(cx)[0].toChars();

        // decode the char array to byte[] (2nd step)
        byte[] decodedHex = Hex.decodeHex(charArray);

        // The String decoded to Base64 (3rd step)
        return Base64.encodeBase64String(decodedHex);
    }

    public static Signature[] getSig(Context cx) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = cx.getPackageManager();
        Signature[] sigs = packageManager.getPackageInfo(cx.getPackageName(), GET_SIGNING_CERTIFICATES).signingInfo.getApkContentsSigners();
        return sigs;
    }
}
