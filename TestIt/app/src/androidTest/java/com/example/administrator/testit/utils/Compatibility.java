package com.example.administrator.testit.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by vivian on 2016/2/18.
 */
public class Compatibility {
    private static final String LOG_TAG = Compatibility.class.getSimpleName();
    private static final Method METHOD_GET_EXTERNAL_FILES_DIR;

    private Compatibility() {
    }

    public static File getExternalFilesDir(Context context, String type) {
        if(METHOD_GET_EXTERNAL_FILES_DIR == null) {
            File e = Environment.getExternalStorageDirectory();
            if(e == null) {
                return null;
            } else {
                String packageName = context.getApplicationContext().getPackageName();
                return new File(e, "Android/data/" + packageName + "/files");
            }
        } else {
            try {
                return (File)METHOD_GET_EXTERNAL_FILES_DIR.invoke(context, new Object[]{type});
            } catch (Exception var4) {
                Log.e(LOG_TAG, "Could not invoke getExternalFilesDir: " + var4.getMessage(), var4);
                return null;
            }
        }
    }

    static {
        Method method = null;

        try {
            method = Context.class.getMethod("getExternalFilesDir", new Class[]{String.class});
        } catch (Exception var2) {
            ;
        }

        METHOD_GET_EXTERNAL_FILES_DIR = method;
    }
}
