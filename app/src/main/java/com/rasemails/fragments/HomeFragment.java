package com.rasemails.fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import com.rasemails.R;
import com.rasemails.adapters.EmailsCursorAdapter;
import com.rasemails.data.EmailatyContract;
import com.rasemails.data.EmailatyContract.EmailsEntry;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private Context mContext;

    private View mMainView;
    private TextView mEmailFieldTextView;
    private TextView mEmailNumberTextView;
    private TextView mCopyButton;
    private TextView mNextButton;
    private TextView mShowEmailsButton;
    private TextView mEditEmailsButton;

    private String mCurrentEmail;

    private Cursor mEmailCursor;
    private int mCursorPosition = 0;
    private EmailsCursorAdapter mShowEmailsCursorAdapter;
    private EmailsCursorAdapter mEditEmailsCursorAdapter;
    private static final int SHOW_EMAIL_LOADER = 0; // number for the semester loader.
    private static final int EDIT_EMAIL_LOADER = 1; // number for the semester loader.



    public HomeFragment(Context context) {
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

        mMainView = inflater.inflate(R.layout.fragment_home, container, false);


        mEmailFieldTextView = mMainView.findViewById(R.id.fragment_home_item_email_field);
        mEmailNumberTextView = mMainView.findViewById(R.id.fragment_home_email_number);
        mCopyButton = mMainView.findViewById(R.id.fragment_home_copy_button);
        mNextButton = mMainView.findViewById(R.id.fragment_home_next_button);
        mShowEmailsButton = mMainView.findViewById(R.id.fragment_home_show_emails_button);
        mEditEmailsButton = mMainView.findViewById(R.id.fragment_home_edit_emails_button);



        mShowEmailsCursorAdapter = new EmailsCursorAdapter(mContext, null, 0);
        mEditEmailsCursorAdapter = new EmailsCursorAdapter(mContext, null, 1);

        LoaderManager.getInstance(this).initLoader(SHOW_EMAIL_LOADER, null, this);
        LoaderManager.getInstance(this).initLoader(EDIT_EMAIL_LOADER, null, this);

//        setupHomeEmails();
//
//        setClickingOnCopyButton();
//        setClickingOnNextButton();
//        setClickingOnShowEmailsButton();
//        setClickingOnEditEmailsButton();

        // Inflate the layout for this fragment
        return mMainView;

    }

    private void setupHomeEmails() {

        if (mEmailCursor.getCount() != 0) {

            mEmailCursor.moveToPosition(mCursorPosition);

//            mCursorPosition = mEmailCursor.getPosition();

            int emailColumnIndex = mEmailCursor.getColumnIndexOrThrow(EmailsEntry.COLUMN_EMAIL_NAME);

            mCurrentEmail = mEmailCursor.getString(emailColumnIndex);
            mEmailFieldTextView.setText(mCurrentEmail);

            int emailNumber = mEmailCursor.getPosition() + 1;
            mEmailNumberTextView.setText(String.valueOf(emailNumber));

        } else {

//            String email = mEmailCursor.getString(R.string.no_emails_yet);
            mEmailFieldTextView.setText(R.string.no_emails_yet);
            mEmailFieldTextView.setTextColor(mContext.getResources().getColor(R.color.color_gray));

            mEmailNumberTextView.setText(String.valueOf(0));
            mEmailNumberTextView.setTextColor(mContext.getResources().getColor(R.color.color_gray));

        }


    }

    private void setClickingOnNextButton() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("Unix", "the mCursorPosition is : " + mCursorPosition + "    and the getCount si : " + mEmailCursor.getCount());

                if (mCursorPosition + 1 < mEmailCursor.getCount()) {

                    mEmailCursor.moveToNext();

                    mCursorPosition = mEmailCursor.getPosition();

                    int emailColumnIndex = mEmailCursor.getColumnIndexOrThrow(EmailsEntry.COLUMN_EMAIL_NAME);

                    mCurrentEmail = mEmailCursor.getString(emailColumnIndex);
                    mEmailFieldTextView.setText(mCurrentEmail);

                    int emailNumber = mEmailCursor.getPosition() + 1;
                    mEmailNumberTextView.setText(String.valueOf(emailNumber));

                } else {

                    Toast.makeText(mContext, R.string.no_emails_next, Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    private void setClickingOnCopyButton() {

        mCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("email_text", mCurrentEmail);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(mContext, R.string.text_copied, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setClickingOnShowEmailsButton() {

        mShowEmailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayShowEmailsDialog();

            }
        });

    }

    private void displayShowEmailsDialog() {

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_emails);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView titleTextView = dialog.findViewById(R.id.dialog_show_emails_title);
        ListView emailsListView = dialog.findViewById(R.id.dialog_show_emails_list_view);



        titleTextView.setText(R.string.show_emails);


        emailsListView.setAdapter(mShowEmailsCursorAdapter);


        emailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mEmailCursor.moveToPosition(position);

                mCursorPosition = mEmailCursor.getPosition();

                int emailColumnIndex = mEmailCursor.getColumnIndexOrThrow(EmailsEntry.COLUMN_EMAIL_NAME);

                mCurrentEmail = mEmailCursor.getString(emailColumnIndex);
                mEmailFieldTextView.setText(mCurrentEmail);

                int emailNumber = mEmailCursor.getPosition() + 1;
                mEmailNumberTextView.setText(String.valueOf(emailNumber));

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private void setClickingOnEditEmailsButton() {

        mEditEmailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayEditEmailsDialog();

            }
        });

    }

    private void displayEditEmailsDialog() {

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_show_emails);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView titleTextView = dialog.findViewById(R.id.dialog_show_emails_title);
        ListView emailsListView = dialog.findViewById(R.id.dialog_show_emails_list_view);


        titleTextView.setText(R.string.edit_emails);

        emailsListView.setAdapter(mEditEmailsCursorAdapter);


        dialog.show();

    }




    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {



        switch (loaderID) {

            case SHOW_EMAIL_LOADER:
            case EDIT_EMAIL_LOADER:

                String sortOrder = EmailsEntry.COLUMN_UNIX + " ASC";


                return new CursorLoader(mContext,
                        EmailsEntry.CONTENT_URI,
                        null, // null because all the table columns will be used.
                        null,
                        null,
                        sortOrder);


            default:
                return null;
        }

    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // handle each loader.
        switch (loader.getId()) {

            // for semester loader.
            case SHOW_EMAIL_LOADER:

                mShowEmailsCursorAdapter.swapCursor(cursor);
//                mEmailCursor = cursor;

//                setupHomeEmails();
//
//                setClickingOnCopyButton();
//                setClickingOnNextButton();
//                setClickingOnShowEmailsButton();
//                setClickingOnEditEmailsButton();


                break;

            // for semester loader.
            case EDIT_EMAIL_LOADER:

                mEditEmailsCursorAdapter.swapCursor(cursor);
                mEmailCursor = cursor;

                setupHomeEmails();

                setClickingOnCopyButton();
                setClickingOnNextButton();
                setClickingOnShowEmailsButton();
                setClickingOnEditEmailsButton();


                break;


        }


    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mShowEmailsCursorAdapter.swapCursor(null);
        mEditEmailsCursorAdapter.swapCursor(null);

    }


}



