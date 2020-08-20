package com.zebra.jamesswinton.savedeviceidentifierstofile.profilemanager;

public interface OnProfileApplied {
    void profileApplied(String statusCode, String extendedStatusCode);
    void profileError(String statusCode, String extendedStatusCode);
}
