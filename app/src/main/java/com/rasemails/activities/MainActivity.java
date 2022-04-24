package com.rasemails.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.rasemails.R;
import com.rasemails.fragments.AddEmailFragment;
import com.rasemails.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {


    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private Context mContext;

    private ImageView mHomeButtonImageView;
    private ImageView mAddEmailButtonImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        checkUpdate();

        mHomeButtonImageView = findViewById(R.id.activity_main_home_button);
        mAddEmailButtonImageView = findViewById(R.id.activity_main_add_email_button);

        showHomeFragment();


        setClickingOnHomeButton();
        setClickingOnAddEmailButton();
    }

    private void setClickingOnHomeButton() {

        mHomeButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showHomeFragment();

            }
        });
    }

    private void setClickingOnAddEmailButton() {

        mAddEmailButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMessageFragment();

            }
        });

    }

    private void showHomeFragment() {

        mHomeButtonImageView.setImageResource(R.drawable.home_blue_light_icon);
        mAddEmailButtonImageView.setImageResource(R.drawable.message_blue_dark_icon);

        HomeFragment homeFragment = new HomeFragment(mContext);
        openFragment(homeFragment);

    }


    private void showMessageFragment() {

        mHomeButtonImageView.setImageResource(R.drawable.home_blue_dark_icon);
        mAddEmailButtonImageView.setImageResource(R.drawable.message_blue_light_icon);

        AddEmailFragment messageFragment = new AddEmailFragment(mContext);
        openFragment(messageFragment);

    }


    private void openFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.activity_main_frame_layout, fragment, null)
                .commit();

    }





    private void checkUpdate() {

        long currentSystemTime = System.currentTimeMillis();

        // https://www.unixtimestamp.com/

        long unixNumberFor_27_4_2022 = 	1651078825;
        Log.i("Unix", "the unix number that comes from the system :  " +
                (currentSystemTime/1000) + "   " + unixNumberFor_27_4_2022);
        if ((currentSystemTime/1000)  > unixNumberFor_27_4_2022) {

            showForceUpdateDialog();

        }

    }

    public void showForceUpdateDialog(){

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Sorry...!");
        alertDialogBuilder.setMessage(R.string.check_update_for_clients);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        alertDialogBuilder.show();
    }

}