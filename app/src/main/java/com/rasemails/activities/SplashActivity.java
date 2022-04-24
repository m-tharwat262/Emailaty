package com.rasemails.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rasemails.R;


public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        mContext = SplashActivity.this;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();


        TextView appNameTextView = findViewById(R.id.activity_splash_app_description_name);
        TextView developerLogText = findViewById(R.id.activity_splash_screen_developer_logo_text);
        ImageView developerLogoImage = findViewById(R.id.activity_splash_screen_developer_logo_image);
        LinearLayout developerLogo = findViewById(R.id.activity_splash_screen_developer_logo);

        Animation animationFromLeftToRight = AnimationUtils.loadAnimation(this, R.anim.animation_from_left_to_right);
        Animation animationFromRightToLeft = AnimationUtils.loadAnimation(this, R.anim.animation_from_right_to_left);

        appNameTextView.setAnimation(animationFromLeftToRight);
        developerLogText.setAnimation(animationFromRightToLeft);
        developerLogoImage.setAnimation(animationFromRightToLeft);

        int SPLASH_SCREEN = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);

    }


}








