package com.ust.checklist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import com.ust.smartph.R;
/**
 * Created by Jordan on 1/13/2016.
 */
public class FragmentReminderSettings extends Fragment
{
    private RadioGroup mRadioGroup;

    private SwitchCompat mReminderSwitch;

    private int newReminderInterval;

    private boolean newIsEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Enable options menu.
        setHasOptionsMenu(true);
        //Configure toolbar.
        try
        {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Notifications");
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch(Exception e)
        {
            Log.d("NPE","No support action bar");
        }

        //Initial values.
        newIsEnabled = ReminderSettings.get(getActivity()).isEnabled();
        newReminderInterval = ReminderSettings.get(getActivity()).getReminderInterval();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_reminder_settings,parent,false);

        //Get references to views.
        mRadioGroup = (RadioGroup)v.findViewById(R.id.radioGroup_reminder_interval);
        mReminderSwitch = (SwitchCompat)v.findViewById(R.id.switch_reminder_enable);

        //Initialize radio button.
        mRadioGroup.check(newReminderInterval);
        //Enable/Disable buttons accordingly.
        setEnabled(newIsEnabled);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            //User selected a new radio button.
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                //We need to log the new state.
                newReminderInterval = checkedId;
            }
        });

        //Initialize switch.
        mReminderSwitch.setChecked(newIsEnabled);
        mReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            //User pressed the switch.
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //We need to log the new state.
                newIsEnabled = isChecked;
                //Enable/Disable buttons according to new state.
                setEnabled(newIsEnabled);
            }
        });

        return v;
    }

    /*
    * Options Menu
    * */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        //Reset menu to remove overflow menu.
        menu.clear();
        //Inflate the menu for this fragment.
        inflater.inflate(R.menu.confirm_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.accept_button:
                //We need to finalize the changes.
                ReminderSettings.get(getActivity()).setEnabled(newIsEnabled);
                ReminderSettings.get(getActivity()).setReminderInterval(newReminderInterval);
                //Also save the changes.
                ReminderSettings.get(getActivity()).saveSettings();
                //Now revert back to home fragment.
                returnToHome();
                return true;
            case R.id.cancel_button:
                //Simply revert back to home fragment.
                returnToHome();
                return true;
            case android.R.id.home:
                returnToHome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //This method swaps from AddItemFragment to HomeFragment.
    public void returnToHome()
    {
        HomeFragment newFragment = new HomeFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container,newFragment);
        ft.commit();
    }

    //Enable/Disable radio buttons.
    public void setEnabled(boolean isEnabled)
    {
        //Enable/Disable radio buttons.
        for(int i = 0; i < mRadioGroup.getChildCount();i++)
        {
            mRadioGroup.getChildAt(i).setEnabled(isEnabled);
        }
        //Set text on switch.
        if(isEnabled)
        {
            mReminderSwitch.setText("Enabled ");
        }
        else
        {
            mReminderSwitch.setText("Disabled ");
        }
    }
}
