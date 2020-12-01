package com.zebra.jamesswinton.savedeviceidentifierstofile.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;
import android.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class PackageManagerHelper {

    public static String getSigningCertBase64(Context cx) throws PackageManager.NameNotFoundException, DecoderException {
        //convert String to char array (1st step)
        char[] charArray = getSigningCertificateHex(cx)[0].toChars();

        // decode the char array to byte[] (2nd step)
        byte[] decodedHex = Hex.decodeHex(charArray);

        // The String decoded to Base64 (3rd step)
        // return Base64.encodeBase64String(decodedHex); -> Throws error on Android 8
        return android.util.Base64.encodeToString(decodedHex, Base64.NO_WRAP);
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static Signature[] getSigningCertificateHex(Context cx)
            throws PackageManager.NameNotFoundException {
        Signature[] sigs;
        SigningInfo signingInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            signingInfo = cx.getPackageManager().getPackageInfo(cx.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES).signingInfo;
            sigs = signingInfo.getApkContentsSigners();
        } else {
            sigs = cx.getPackageManager().getPackageInfo(cx.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
        }
        return sigs;
    }
}
