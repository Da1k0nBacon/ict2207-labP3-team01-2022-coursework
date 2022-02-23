package com.danstoakes.fileexplorer.activity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class ViewAssistClass {
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

    protected void writeToFile(String data, Context context, String path) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(path, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
//    protected void loadupSMS()
//    {
//        ContentResolver cResolver =
//    }


}
