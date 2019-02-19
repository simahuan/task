package com.zt.task.system.util;

import android.graphics.drawable.Drawable;

public class MyFile {

    Drawable apk_icon;

    public Drawable getApk_icon() {
        return apk_icon;
    }

    public void setApk_icon(Drawable pApk_icon) {
        apk_icon = pApk_icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String pPackageName) {
        packageName = pPackageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String pFilePath) {
        filePath = pFilePath;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String pVersionName) {
        versionName = pVersionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int pVersionCode) {
        versionCode = pVersionCode;
    }

    public int getInstalled() {
        return installed;
    }

    public void setInstalled(int pInstalled) {
        installed = pInstalled;
    }

    String packageName;

    String filePath;

    String versionName;

    int versionCode;

    int installed;

}
