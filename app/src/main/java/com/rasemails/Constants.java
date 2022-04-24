package com.rasemails;

import android.os.Environment;

import java.io.File;

public class Constants {


    public static final String APP_PATH =  Environment.getExternalStorageDirectory() + File.separator + "TODO";

    public static final String BACKUP_PATH =  APP_PATH + File.separator + "Backup";

    public static final String TEMPORARY_PATH =  APP_PATH + File.separator + "Temporary";


}
