package com.danstoakes.fileexplorer.activity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class ViewAssistClass {

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
