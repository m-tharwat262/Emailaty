package com.rasemails.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rasemails.Constants;
import com.rasemails.data.EmailatyContract.EmailsEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class EmailatyDbHelper extends SQLiteOpenHelper {


    public static final String LOG_TAG = EmailatyDbHelper.class.getSimpleName();
    private Context mContext;

    private static final String DATABASE_NAME = "emails.db";

    private static final int DATABASE_VERSION = 1;


    public EmailatyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + EmailatyContract.EmailsEntry.TABLE_NAME + " ("

                + EmailsEntry._ID                                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EmailsEntry.COLUMN_EMAIL_NAME                      + " TEXT NOT NULL, "
                + EmailsEntry.COLUMN_UNIX                            + " INTEGER NOT NULL DEFAULT 0);";


        db.execSQL(SQL_CREATE_PETS_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to be done here.
    }


    public void performBackup(String folderName, String userId) {

        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = folderName + File.separator + DATABASE_NAME;

        backup(fileName, userId);

    }

    public void backup(String fileName, String userId) {

        //database path
        final String inFileName = mContext.getDatabasePath(userId + "_" + DATABASE_NAME).toString();

        try {
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);


            OutputStream output = new FileOutputStream(fileName);


            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }


            output.flush();
            output.close();
            fis.close();

//            Toast.makeText(mContext, "???? ?????????? ???????????? ????????????????????", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
//            Toast.makeText(mContext, "?????? ?????? ???????? ?????? ????????", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    public void performRestore(String folderName, String userId) {

        String fileName = Constants.BACKUP_PATH + File.separator + folderName + File.separator + DATABASE_NAME;

        try {
            importDB(fileName, userId);
        } catch (Exception e) {
//            Toast.makeText(mContext, "?????? ?????? ???????? ?????? ????????", Toast.LENGTH_SHORT).show();
        }


    }

    public void importDB(String fileName, String userId) {

        final String outFileName = mContext.getDatabasePath(userId + "_" + DATABASE_NAME).toString();

        try {

            File dbFile = new File(fileName);

            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

//            Toast.makeText(mContext, "?????? ?????????????????? ??????????", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
//            Toast.makeText(mContext, "?????? ?????? ???????? ?????? ????????", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}