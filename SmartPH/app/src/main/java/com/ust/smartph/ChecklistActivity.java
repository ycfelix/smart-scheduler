package com.ust.smartph;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ust.checklist.CheckList;
import com.ust.checklist.HomeFragment;
import com.ust.checklist.Keys;
import com.ust.checklist.ReminderService;
import com.ust.checklist.ReminderSettings;

public class ChecklistActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null)
        {
            fragment = new HomeFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment, Keys.HOME_FRAGMENT_TAG).commit();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //Start/stop the reminders.
        ReminderService.setServiceAlarm(this, ReminderSettings.get(this).isEnabled(), ReminderSettings.get(this).getTimeInMillis(), CheckList.get(this).getNumberUnchecked());
    }

    /*
    * Back Button
    * */

    //Handle the user pressing the back button.
    @Override
    public void onBackPressed()
    {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        //If we are on the home screen just close the app.
        if(currentFragment instanceof HomeFragment)
        {
            finish();
        }
        //If we are somewhere else navigate back to home screen.
        else
        {
            returnToHome();
        }
    }

    //This method swaps from AddItemFragment to HomeFragment.
    public void returnToHome()
    {
        HomeFragment newFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,newFragment);
        ft.commit();
    }
}
