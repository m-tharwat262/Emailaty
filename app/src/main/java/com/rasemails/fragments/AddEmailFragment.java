package com.rasemails.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.rasemails.R;
import com.rasemails.data.EmailatyContract.EmailsEntry;

public class AddEmailFragment extends Fragment {

    private static final String LOG_TAG = AddEmailFragment.class.getSimpleName();
    private Context mContext;

    private View mMainView;
    private EditText mEmailAddressField;
    private TextView mAddEmailButton;


    public AddEmailFragment(Context context) {
        // Required empty public constructor
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_add_email, container, false);

        mEmailAddressField = mMainView.findViewById(R.id.fragment_add_email_email_address_field);
        mAddEmailButton = mMainView.findViewById(R.id.fragment_add_email_add_email_button);



        setClickingOnAddEmailButton();

        // Inflate the layout for this fragment
        return mMainView;
    }

    private void setClickingOnAddEmailButton() {

        mAddEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = mEmailAddressField.getText().toString().trim();

                if (emailAddress.isEmpty()) {
                    Toast.makeText(mContext, R.string.please_add_email_address, Toast.LENGTH_SHORT).show();
                } else {
                    addEmailToDatabase(emailAddress);
                }

            }
        });

    }

    private void addEmailToDatabase(String emailAddress) {

        long time = System.currentTimeMillis();


        // initialize and setup the ContentValues to contain the data that will be insert inside the database.
        ContentValues values = new ContentValues();
        values.put(EmailsEntry.COLUMN_EMAIL_NAME, emailAddress);
        values.put(EmailsEntry.COLUMN_UNIX, time);


        insertEmail(values);

    }

    private void insertEmail(ContentValues values) {

        // insert the new semester to the database and get the uri for that semester.
        Uri uri = mContext.getContentResolver().insert(EmailsEntry.CONTENT_URI, values);

        // check if the semester inserted successfully or failed.
        if (uri == null) {
            // show a toast message to the user says that "Error with saving semester".
            Toast.makeText(mContext, R.string.insert_email_inside_database_failed, Toast.LENGTH_SHORT).show();
        } else {
            // show a toast message to the user says that "Semester saved".
            Toast.makeText(mContext, R.string.insert_email_inside_database_successful, Toast.LENGTH_SHORT).show();
            mEmailAddressField.setText("");
        }

    }

}