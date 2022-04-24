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


    private static final int EMAILS = 100; // URI pattern to all the semester_gpa table.
    private static final int EMAILS_ID = 101; // URI pattern to single column in the semester_gpa table.


    // initialize the UriMatcher.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // add the table paths and his pattern number in the uri matcher.
    static {
        sUriMatcher.addURI(EmailatyContract.CONTENT_AUTHORITY, EmailatyContract.PATH_EMAILS, EMAILS);
        sUriMatcher.addURI(EmailatyContract.CONTENT_AUTHORITY, EmailatyContract.PATH_EMAILS + "/#", EMAILS_ID);
    }


    /**
     * initialize databases here.
     */
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



    /**
     * Read from the database (all the database or a single row).
     *
     * @param uri uri for the path in the database.
     * @param projection specific column in the database.
     * @param selection specific row in the database.
     * @param selectionArgs the value for the selection parameter above.
     * @param sortOrder the order that the results must return with.
     *
     * @return Cursor contain the results after the reading from database finish.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // access to databases (semester - cumulative) to read the data from it.
        SQLiteDatabase yearDatabase = mEmailsDbHelper.getReadableDatabase();

        // for the return result from the database.
        Cursor cursor;

        // get the pattern that the uri equal.
        int match = sUriMatcher.match(uri);

        // setup functions for every uri pattern.
        switch (match) {

            // access to semester database with no id.
            case EMAILS:

                // setup the input inside the query function.
                cursor = yearDatabase.query(EmailsEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);

                break;

            // access to semester database with id.
            case EMAILS_ID:

                // setup the input inside the query function (after the WHERE word).
                selection = EmailsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = yearDatabase.query(EmailsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;


            // to handle if the is no match for the uri inserted with the uri patterns.
            default:

                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // return Cursor contain the data from the database.
        return cursor;
    }



    /**
     * Write inside the database to add a new data.
     *
     * @param uri uri for the database path.
     * @param values contain the columns keys and its values.
     *
     * @return uri refer to the row place inside the database.
     */
    @Override
    public Uri insert( Uri uri, ContentValues values) {

        // get the pattern that the uri equal.
        final int match = sUriMatcher.match(uri);

        // setup functions for every uri pattern (no single uris by id).
        switch (match) {

            // semester_gpa table inside semester database.
            case EMAILS:

                // execute helper method to insert the data inside the semester database.
                // return the uri that refer to the row place inside the semester database.
                return insertEmail(uri, values);


            // to handle if the is no match for the uri inserted with the uri patterns.
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    /**
     * (Helper Method to Insert Data Inside semester Database).
     * Insert data inside the semester_gpa table in semester database.
     * Check the validation for the data inserted from the user.
     *
     * @param uri uri for the path inside the database.
     * @param values contain the columns keys and its values.
     *
     * @return uri refer to the row place inside the database.
     */
    private Uri insertEmail(Uri uri, ContentValues values) {


        // access to the database to write inside it.
        SQLiteDatabase semesterDatabase = mEmailsDbHelper.getWritableDatabase();

        // insert the data inside the database and get the row number that the data inserted at.
        long id = semesterDatabase.insert(EmailsEntry.TABLE_NAME, null, values);

        // if the insertion failed show a Log(e) for that.
        // make the uri return value equal null in case the insertion failed.
        if (id == -1) {

            Log.e(LOG_TAG, "Failed to insert row in year database for " + uri);

            return null;
        }

        // Notify that there is changing happened in the database to sync changes to the network or activities.
        getContext().getContentResolver().notifyChange(uri, null);

        // return the uri for the place that the data inserted inside database.
        return ContentUris.withAppendedId(uri, id);

    }


    /**
     * Write inside the database to update data inserted before.
     *
     * @param uri uri for the database path.
     * @param contentValues contain the columns keys and its values.
     * @param selection specific row in the database.
     * @param selectionArgs the value for the selection parameter above.
     *
     * @return uri refer to the row place inside the database.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // get the pattern that the uri equal.
        final int match = sUriMatcher.match(uri);

        // setup functions for every uri pattern.
        switch (match) {

            // semester_gpa database with no id.
            case EMAILS:

                // execute helper method to update the data inside the database.
                // return number of the rows that updated.
                return updateEmail(uri, contentValues, selection, selectionArgs);

            // semester_gpa database with id.
            case EMAILS_ID:

                // setup the input inside the update function (after the WHERE word).
                selection = EmailsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // execute helper method to update the data inside the database.
                // return number of the rows that updated.
                return updateEmail(uri, contentValues, selection, selectionArgs);



            // to handle if the is no match for the uri inserted with the uri patterns.
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * (Helper Method to Update Data Inside semester Database).
     * Insert data inside the semester gpa database.
     * Check the validation for the data inserted from the user.
     *
     * @param uri uri for the database path.
     * @param contentValues contain the columns keys and its values.
     * @param selection specific row in the database.
     * @param selectionArgs the value for the selection parameter above.
     *
     * @return uri refer to the row place inside the database.
     */
    private int updateEmail(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {


        // if there is not keys inside the ContentValues return from the functions early.
        // the number of the rows that updated in this case will equal zero.
        if (contentValues.size() == 0) {
            return 0;
        }

        // access to the database to write inside it.
        SQLiteDatabase semesterDatabase = mEmailsDbHelper.getWritableDatabase();

        // update the data inside the database and get the number of the rows that updated.
        int rowsUpdated = semesterDatabase.update(EmailsEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // Notify that there is changing happened in the database to sync changes to the network or activities.
        getContext().getContentResolver().notifyChange(uri, null);

        // return the number of the rows that updated.
        return rowsUpdated;
    }



    /**
     * delete data from the database (all the database or a single row).
     *
     * @param uri uri for the database path.
     * @param selection specific row in the database.
     * @param selectionArgs the value for the selection parameter above.
     *
     * @return number of the rows that deleted from the database.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // access to databases (semester - cumulative) to write inside it.
        SQLiteDatabase yearsDatabase = mEmailsDbHelper.getWritableDatabase();


        // get the pattern that the uri equal.
        final int match = sUriMatcher.match(uri);

        // number of the rows that will be deleted
        int rowsDeleted;

        // setup functions for every uri pattern.
        switch (match) {

            // semester_gpa database with no id.
            case EMAILS:

                // delete the data from the database.
                // return number of the rows that deleted from the database.
                rowsDeleted = yearsDatabase.delete(EmailsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            // semester_gpa database with id.
            case EMAILS_ID:

                // setup the input inside the update function (after the WHERE word).
                selection = EmailsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // delete the data from the database.
                // return number of the rows that deleted from the database.
                rowsDeleted = yearsDatabase.delete(EmailsEntry.TABLE_NAME, selection, selectionArgs);
                break;


            // to handle if the is no match for the uri inserted with the uri patterns.
            default:

                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // notify the network or the activity when there is changing happened inside the database.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of the rows that deleted from the database.
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
