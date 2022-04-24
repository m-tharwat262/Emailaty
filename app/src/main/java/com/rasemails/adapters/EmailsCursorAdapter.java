package com.rasemails.adapters;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.rasemails.R;
import com.rasemails.data.EmailatyContract.EmailsEntry;

public class EmailsCursorAdapter  extends CursorAdapter {


    private static final String LOG_TAG = EmailsCursorAdapter.class.getSimpleName(); // class name.
    private Context mContext;

    private static final int SHOW_MODE = 0;
    private static final int EDIT_MODE = 1;

    private int mMode;


    public EmailsCursorAdapter(Context context, Cursor c, int mode) {
        super(context, c, 0);

        mContext = context;
        mMode = mode;

    }

    protected static class RowViewHolder {
        public TextView mEmailField;
        public TextView mEmailNumber;
        public TextView mEditButton;
        public int position;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View itemView;
        RowViewHolder rowView = new RowViewHolder();
        if (mMode == SHOW_MODE) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_email, parent, false);
            rowView.mEmailField = (TextView) itemView.findViewById(R.id.item_email_email_field);
            rowView.mEmailNumber = (TextView) itemView.findViewById(R.id.item_email_item_number);

        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_edit_email, parent, false);
            rowView.mEmailField = (TextView) itemView.findViewById(R.id.item_edit_email_email_field);
            rowView.mEmailNumber = (TextView) itemView.findViewById(R.id.item_edit_email_item_number);
            rowView.mEditButton = (TextView) itemView.findViewById(R.id.item_edit_email_edit_button);
        }


        rowView.position = cursor.getPosition();



        return itemView;

    }



    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        RowViewHolder rowView = new RowViewHolder();
        rowView.position = cursor.getPosition(); // (same as in newView)


        if (mMode == SHOW_MODE) {
            rowView.mEmailField = view.findViewById(R.id.item_email_email_field);
            rowView.mEmailNumber = view.findViewById(R.id.item_email_item_number);
        } else {
            rowView.mEmailField = view.findViewById(R.id.item_edit_email_email_field);
            rowView.mEmailNumber = view.findViewById(R.id.item_edit_email_item_number);
            rowView.mEditButton = view.findViewById(R.id.item_edit_email_edit_button);
        }


        rowView.mEmailField.setTag(rowView);
        rowView.mEmailNumber.setTag(rowView);
        if (mMode == EDIT_MODE) {
            rowView.mEditButton.setTag(rowView);
        }


        int emailUniqueIdColumnIndex = cursor.getColumnIndexOrThrow(EmailsEntry._ID);
        int emailNameColumnIndex = cursor.getColumnIndexOrThrow(EmailsEntry.COLUMN_EMAIL_NAME);
        int pos = cursor.getPosition();

        rowView.mEmailField.setTag(pos);
        rowView.mEmailNumber.setTag(pos);
        if (mMode == EDIT_MODE) {
            rowView.mEditButton.setTag(pos);
        }



        rowView.mEmailNumber.setText(String.valueOf(cursor.getPosition() + 1));

        String email = cursor.getString(emailNameColumnIndex);
        rowView.mEmailField.setText(email);


        rowView.mEmailField.setTag(cursor.getPosition());


        if (mMode == EDIT_MODE) {
            rowView.mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cursor.moveToPosition(pos);
                    long currentId = cursor.getLong(emailUniqueIdColumnIndex);
                    String currentEmail = cursor.getString(emailNameColumnIndex);

                    displayEditDialog(currentId, currentEmail);

                }
            });
        }

    }

    private void displayEditDialog(long id, String email) {

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_edit_eamils);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        EditText emailFieldTextView = dialog.findViewById(R.id.dialog_edit_emails_email_field);
        TextView editButton = dialog.findViewById(R.id.dialog_edit_emails_edit_button);
        TextView deleteButton = dialog.findViewById(R.id.dialog_edit_emails_delete_button);

        emailFieldTextView.setText(email);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newEmail = emailFieldTextView.getText().toString().trim();
                if (newEmail.isEmpty()) {
                    Toast.makeText(mContext, R.string.can_not_be_empty, Toast.LENGTH_SHORT).show();
                } else if (newEmail.equals(email)) {
                    Toast.makeText(mContext, R.string.no_change_in_eamil, Toast.LENGTH_SHORT).show();
                } else {
                    updateEmailInDatabase(id, newEmail);
                    dialog.dismiss();
                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteFromDatabase(id);
                dialog.dismiss();

            }
        });


        dialog.show();
    }



    private void updateEmailInDatabase(long id, String newEmail) {

        ContentValues values = new ContentValues();
        values.put(EmailsEntry.COLUMN_EMAIL_NAME, newEmail);

        Uri yearUri = new ContentUris().withAppendedId(EmailsEntry.CONTENT_URI, id);

        updateYear(values, yearUri);

    }

    private void updateYear(ContentValues values, Uri yearUri) {

        int rows = mContext.getContentResolver().update(yearUri, values, null, null);

        if (rows == 0) {
            Toast.makeText(mContext, R.string.update_email_inside_database_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.update_email_inside_database_successful, Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteFromDatabase(long id) {

        Uri mediaUri = new ContentUris().withAppendedId(EmailsEntry.CONTENT_URI, id);

        int rows = mContext.getContentResolver().delete(mediaUri, null,null);

        if (rows == 0) {
            Toast.makeText(mContext, R.string.delete_email_inside_database_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.delete_email_inside_database_successful, Toast.LENGTH_SHORT).show();
        }


    }
}
