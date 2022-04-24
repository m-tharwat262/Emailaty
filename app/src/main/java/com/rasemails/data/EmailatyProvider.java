package com.rasemails.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rasemails.data.EmailatyContract.EmailsEntry;


public class EmailatyProvider extends ContentProvider {


    public static final String LOG_TAG = EmailatyProvider.class.getSimpleName(); // class name.
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    private EmailatyDbHelper mEmailsDbHelper;


    private static final int EMAILS = 100;
    private static final int EMAILS_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(EmailatyContract.CONTENT_AUTHORITY, EmailatyContract.PATH_EMAILS, EMAILS);
        sUriMatcher.addURI(EmailatyContract.CONTENT_AUTHORITY, EmailatyContract.PATH_EMAILS + "/#", EMAILS_ID);
    }



    @Override
    public boolean onCreate() {

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = pref.edit();

        mEmailsDbHelper = new EmailatyDbHelper(getContext());

        return false;

    }

    public void refreshProvider() {

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = pref.edit();

        mEmailsDbHelper.close();

        mEmailsDbHelper = new EmailatyDbHelper(getContext());

    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase yearDatabase = mEmailsDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {

            case EMAILS:

                cursor = yearDatabase.query(EmailsEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);

                break;

            case EMAILS_ID:

                selection = EmailsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = yearDatabase.query(EmailsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;


            default:

                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert( Uri uri, ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case EMAILS:

                return insertEmail(uri, values);


            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertEmail(Uri uri, ContentValues values) {

        SQLiteDatabase semesterDatabase = mEmailsDbHelper.getWritableDatabase();

        long id = semesterDatabase.insert(EmailsEntry.TABLE_NAME, null, values);

        if (id == -1) {

            Log.e(LOG_TAG, "Failed to insert row in year database for " + uri);

            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);

    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case EMAILS:

                return updateEmail(uri, contentValues, selection, selectionArgs);

            case EMAILS_ID:

                selection = EmailsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                return updateEmail(uri, contentValues, selection, selectionArgs);


            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateEmail(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase semesterDatabase = mEmailsDbHelper.getWritableDatabase();

        int rowsUpdated = semesterDatabase.update(EmailsEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase yearsDatabase = mEmailsDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match) {

            case EMAILS:

                rowsDeleted = yearsDatabase.delete(EmailsEntry.TABLE_NAME, selection, selectionArgs);
                break;


            case EMAILS_ID:

                selection = EmailsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = yearsDatabase.delete(EmailsEntry.TABLE_NAME, selection, selectionArgs);
                break;


            default:

                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }



    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case EMAILS:
                return EmailsEntry.CONTENT_LIST_TYPE;

            case EMAILS_ID:
                return EmailsEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }

    }


}
