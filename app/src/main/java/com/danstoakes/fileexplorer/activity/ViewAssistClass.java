package com.danstoakes.fileexplorer.activity;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;


public class ViewAssistClass{
    protected void viewStuff() {
        String Manufacturer = Build.MANUFACTURER;
        String Model = Build.MODEL;
        String buildId = Build.ID;
        String Brand = Build.BRAND;
        String User = Build.USER;
        String Version = Build.VERSION.RELEASE;

        Log.d("Virus", "Manufacturer: " + Manufacturer);
        Log.d("Virus", "Model: " + Model);
        Log.d("Virus", "Version: " + Version);
        Log.d("Virus", "Build ID: " + buildId);
        Log.d("Virus", "Brand: " + Brand);
        Log.d("Virus", "User: " + User);
    }
//    protected void loadupSMS()
//    {
//        ContentResolver cResolver =
//    }


}
