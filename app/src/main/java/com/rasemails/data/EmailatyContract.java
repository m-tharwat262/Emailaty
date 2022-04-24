package com.rasemails.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class EmailatyContract {


    public static final String CONTENT_AUTHORITY = "com.rasemails"; // the content authority.

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY); // the uri for the database.

    public static final String PATH_EMAILS = "emails";




    public static final class EmailsEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EMAILS);

        public final static String TABLE_NAME = "emails"; // table name.

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_EMAIL_NAME = "email_name";
        public final static String COLUMN_UNIX = "unix";



        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EMAILS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EMAILS;


    }


}
