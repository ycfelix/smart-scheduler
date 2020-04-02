package com.ust.smartph;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
public class AboutUsActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simulateDayNight(/* DAY */ 0);
        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getDescription())
                .setImage(R.drawable.app_name)
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .addItem(getFeedbackElement())
                .addGroup("Connect with us")
                .addEmail("cfyauab@connect.ust.hk")
                .addWebsite("https://github.com/ycfelix")
                .addFacebook("")
                .addTwitter("")
                .addYoutube("")
                .addPlayStore("")
                .addInstagram("")
                .addGitHub("")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }
    private String getDescription(){
        String description=" This app helps people manage their time more efficiently and boost"+
                " productivity by allowing you to automate your task";
        return description;
    }
    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = getResources().getString(R.string.copy_right);
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.clock_64);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    Element getFeedbackElement() {
        Element feedbackElement = new Element();
        final String feedbackString = getResources().getString(R.string.feedback);
        feedbackElement.setTitle(feedbackString);
        feedbackElement.setOnClickListener((View v) -> {
            startActivity(new Intent(AboutUsActivity.this, FeedbackActivity.class));
        });
        return feedbackElement;
    }

    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

}
