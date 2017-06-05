package com.example.moudgil.gifzone.utils;

import android.os.Environment;

import com.example.moudgil.gifzone.app.Config;

import java.io.File;

/**
 * Created by apple on 04-06-2017.
 */

public class FileUtils {
    private static FileUtils myObj;
    /**
     * Create private constructor
     */
    private FileUtils(){

    }

    public static FileUtils getInstance(){
        if(myObj == null){
            myObj = new FileUtils();
        }
        return myObj;
    }

    public boolean checkFileExists(String name){


        File sdcard = Environment.getExternalStorageDirectory();

        File folder = new File(sdcard.getAbsoluteFile(), Config.FOLDER_NAME);//the dot makes this directory hidden to the user
        folder.mkdir();
        File file = new File(folder.getAbsoluteFile(),name + ".gif");

        if(file.exists())
            return true;
        else
            return false;

    }


}