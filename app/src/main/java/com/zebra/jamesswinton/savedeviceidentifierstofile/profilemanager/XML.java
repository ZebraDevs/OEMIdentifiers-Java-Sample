package com.zebra.jamesswinton.savedeviceidentifierstofile.profilemanager;

import android.content.Context;
import android.content.pm.PackageManager;

import com.zebra.jamesswinton.savedeviceidentifierstofile.utilities.PackageManagerHelper;

import org.apache.commons.codec.DecoderException;

public class XML {

    public static final String GRANT_SERIAL_PERMISSION_NAME = "SerialPermission";
    public static final String GRANT_IMEI_PERMISSION_NAME = "ImeiPermission";

    // Holders
    private String mPackageSignatureHex;
    private String mPackageName;

    public XML(Context context) throws PackageManager.NameNotFoundException, DecoderException {
        mPackageSignatureHex = PackageManagerHelper.getSigningCertBase64(context);
        mPackageName = context.getPackageName();
    }

    public String getSerialPermissionXml() {
        return  "<wap-provisioningdoc>\n" +
                "  <characteristic type=\"Profile\">\n" +
                "    <parm name=\"ProfileName\" value=\"SerialPermission\"/>\n" +
                "    <characteristic version=\"8.3\" type=\"AccessMgr\">\n" +
                "      <parm name=\"OperationMode\" value=\"1\" />\n" +
                "      <parm name=\"ServiceAccessAction\" value=\"4\" />\n" +
                "      <parm name=\"ServiceIdentifier\" value=\"content://oem_info/oem.zebra.secure/build_serial\" />\n" +
                "      <parm name=\"CallerPackageName\" value=" + '"' + mPackageName + '"' + " />\n" +
                "      <parm name=\"CallerSignature\" value=" + '"' + mPackageSignatureHex + '"' + "  />\n" +
                "    </characteristic>\n" +
                "  </characteristic>\n" +
                "</wap-provisioningdoc>";
    }

    public String getImeiPermissionXml() {
        return  "<wap-provisioningdoc>\n" +
                "  <characteristic type=\"Profile\">\n" +
                "    <parm name=\"ProfileName\" value=\"ImeiPermission\"/>\n" +
                "    <characteristic version=\"8.3\" type=\"AccessMgr\">\n" +
                "      <parm name=\"OperationMode\" value=\"1\" />\n" +
                "      <parm name=\"ServiceAccessAction\" value=\"4\" />\n" +
                "      <parm name=\"ServiceIdentifier\" value=\"content://oem_info/wan/imei\" />\n" +
                "      <parm name=\"CallerPackageName\" value=" + '"' + mPackageName + '"' + " />\n" +
                "      <parm name=\"CallerSignature\" value=" + '"' + mPackageSignatureHex + '"' + "  />\n" +
                "    </characteristic>\n" +
                "  </characteristic>\n" +
                "</wap-provisioningdoc>";
    }

}
