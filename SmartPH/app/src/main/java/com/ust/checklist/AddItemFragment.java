package com.ust.checklist;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import com.ust.smartph.R;
/**
 * Created by Jordan on 1/8/2016.
 *
 * This is the fragment to be put on screen when the user presses the FAB to add an item.
 *
 */
public class AddItemFragment extends Fragment
{
    private String mTitleText;
    private String mDetailsText;
    private String mIconTag;

    private EditText mTitleEditText;
    private EditText mDetailsEditText;

    private ImageView mBlankImageView;
    private ImageView mFitnessImageView;
    private ImageView mSchoolImageView;
    private ImageView mGroceriesImageView;
    private ImageView mBirthdayImageView;
    private ImageView mChildImageView;
    private ImageView mCreditImageView;
    private ImageView mPlaneImageView;
    private ImageView mGasImageView;
    private ImageView mMailImageView;
    private ImageView mPeopleImageView;
    private ImageView mFoodImageView;

    private List<ImageView> mIconViews;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Allow options menu.
        setHasOptionsMenu(true);
        //Configure toolbar.
        try
        {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Add Item");
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch(Exception e)
        {
            Log.d("NPE", "No support action bar");
        }

        //Initialize input variables to test for input later.
        mTitleText = "";
        mDetailsText = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_add,parent,false);

        //Handle new item title input.
        mTitleEditText = (EditText)v.findViewById(R.id.new_item_title_input);
        mTitleEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mTitleText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        //Handle new item subtitle input.
        mDetailsEditText = (EditText)v.findViewById(R.id.new_item_details_input);
        mDetailsEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mDetailsText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        /*
        * Icon Selection *
        */

        //Get references to ImageViews.
        mBlankImageView = (ImageView)v.findViewById(R.id.icon_picker_0);
        mFitnessImageView = (ImageView)v.findViewById(R.id.icon_picker_1);
        mSchoolImageView = (ImageView)v.findViewById(R.id.icon_picker_2);
        mGroceriesImageView = (ImageView)v.findViewById(R.id.icon_picker_3);
        mBirthdayImageView = (ImageView)v.findViewById(R.id.icon_picker_4);
        mChildImageView = (ImageView)v.findViewById(R.id.icon_picker_5);
        mCreditImageView = (ImageView)v.findViewById(R.id.icon_picker_6);
        mPlaneImageView = (ImageView)v.findViewById(R.id.icon_picker_7);
        mGasImageView = (ImageView)v.findViewById(R.id.icon_picker_8);
        mMailImageView = (ImageView)v.findViewById(R.id.icon_picker_9);
        mPeopleImageView = (ImageView)v.findViewById(R.id.icon_picker_10);
        mFoodImageView = (ImageView)v.findViewById(R.id.icon_picker_11);

        //Add ImageViews to list.
        mIconViews = new ArrayList<>();
        mIconViews.add(mBlankImageView);
        mIconViews.add(mFitnessImageView);
        mIconViews.add(mSchoolImageView);
        mIconViews.add(mGroceriesImageView);
        mIconViews.add(mBirthdayImageView);
        mIconViews.add(mChildImageView);
        mIconViews.add(mCreditImageView);
        mIconViews.add(mPlaneImageView);
        mIconViews.add(mGasImageView);
        mIconViews.add(mMailImageView);
        mIconViews.add(mPeopleImageView);
        mIconViews.add(mFoodImageView);

        //Start with blank ImageView selected.
        mBlankImageView.setBackgroundResource(R.drawable.color_splotch_selected);
        mIconTag = Keys.BLANK_ICON_TAG;

        //Handle click on ImageViews.
        mBlankImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.BLANK_ICON_TAG;
            }
        });
        mFitnessImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.FITNESS_ICON_TAG;
            }
        });
        mSchoolImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.SCHOOL_ICON_TAG;
            }
        });
        mGroceriesImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.GROCERIES_ICON_TAG;
            }
        });
        mBirthdayImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.BIRTHDAY_ICON_TAG;
            }
        });
        mChildImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.CHILD_ICON_TAG;
            }
        });
        mCreditImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.CREDIT_ICON_TAG;
            }
        });
        mPlaneImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.PLANE_ICON_TAG;
            }
        });
        mGasImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.GAS_ICON_TAG;
            }
        });
        mMailImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.MAIL_ICON_TAG;
            }
        });
        mPeopleImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.PEOPLE_ICON_TAG;
            }
        });
        mFoodImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Reset the other views.
                repaintIcons((ImageView) v);
                //Set the selected icon tag.
                mIconTag = Keys.FOOD_ICON_TAG;
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
        super.onCreateOptionsMenu(menu, inflater);
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
            //Accept Button was pressed.
            case R.id.accept_button:
                //Check for completion.
                if(mTitleText.isEmpty())
                {
                    Snackbar.make(getView(),"Please enter a Title.", Snackbar.LENGTH_LONG).show();
                }
                //If the form is complete.
                else
                {
                    //Create the new Item.
                    Item newItem = new Item(mTitleText, mDetailsText, mIconTag);
                    //Add to database.
                    DatabaseHandler db = new DatabaseHandler(getActivity());
                    int tempid = (int)db.addItem(newItem);
                    //Set the db id for the item.
                    newItem.setId(tempid);
                    //Add to CheckList.
                    CheckList.get(getActivity()).addItem(newItem);
                    //Return to HomeFragment
                    returnToHome();
                }
                return true;
            //Cancel Button was pressed.
            case R.id.cancel_button:
                //Return to HomeFragment without creating a new item.
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

    //This method resets the ImageViews to highlight the selected Icon.
    private void repaintIcons(ImageView thisView)
    {
        //Set this view to selected.
        thisView.setBackgroundResource(R.drawable.color_splotch_selected);

        //Set others to unselected.
        for(ImageView iv : mIconViews)
        {
            if(!iv.equals(thisView))
            {
                iv.setBackgroundResource(R.drawable.color_splotch_selector);
            }
        }
    }
}
