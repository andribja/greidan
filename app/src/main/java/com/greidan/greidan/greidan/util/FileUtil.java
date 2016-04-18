package com.greidan.greidan.greidan.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static void copyFile(String source, String dest) throws IOException {
        File sourceFile = new File(source);
        File destFile = new File(dest);

        File destDir = destFile.getParentFile();
        if(!destDir.exists()) {
            if(destDir.mkdirs()) {
                // Dir successfully created
                copyFile(sourceFile, destFile);
            } else {
                // oops
                Log.e(TAG, "Could not create destination directory");
            }
        } else {
            copyFile(sourceFile, destFile);
        }
    }

    // sourceFile and destFile must point to an existing directory
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        Log.i(TAG, "Copying file " + sourceFile.getPath() + " to " + destFile.getPath());
        InputStream is = new FileInputStream(sourceFile);
        OutputStream os = new FileOutputStream(destFile);

        byte[] buffer = new byte[1024];
        int len;
        while((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
    }
}
