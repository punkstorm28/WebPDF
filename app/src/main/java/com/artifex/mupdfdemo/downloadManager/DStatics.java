package com.artifex.mupdfdemo.downloadManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.artifex.mupdfdemo.YoungPioneerMain;

/**
 * Created by vyomkeshjha on 30/06/16.
 */
public class DStatics {


   static void resetPreferences()
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("isDownloading",false);
        edit.putInt("download_id",0);
        edit.putInt("fileSize",0);
        edit.apply();
    }
}
