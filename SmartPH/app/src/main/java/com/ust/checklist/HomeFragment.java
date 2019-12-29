package com.ust.checklist;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import com.ust.smartph.R;
/**
 * Created by Jordan on 12/29/2015.
 *
 * This is the Home Screen.
 *
 * This fragment will display all of the Items in CheckList to the user in a list.
 */
public class HomeFragment extends Fragment
{
    private List<Item> items;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Allow options menu.
        setHasOptionsMenu(true);
        //Configure toolbar.
        try
        {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My List");
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        catch(Exception e)
        {
            Log.d("NPE","No support action bar");
        }

        //Hide the keyboard if it has been left up.
        hideKeyboard(getActivity());

        //Get items to display in list.
        items = CheckList.get(getActivity()).getCheckList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home,parent,false);

        //Configure listview.
        ItemListAdapter adapter = new ItemListAdapter(getContext(),items);
        mListView = (ListView)v.findViewById(R.id.item_listView);

        //Set the view to show if list is empty.
        mListView.setEmptyView(v.findViewById(R.id.empty_list_view));

        //Set adapter.
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        //User pressed on an item.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //We need to launch the EditItemFragment so the user can view/edit the item.
                EditItemFragment fragment = new EditItemFragment();
                //We need to attach the position in the list of the selected item.
                Bundle args = new Bundle();
                args.putInt(Keys.EDIT_ITEM_KEY,position);
                fragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,fragment);
                ft.commit();
            }
        });

        //The user long pressed an item in the list.
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {

            }

            //Inflate the delete item menu.
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.delete_item_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            //User pressed menu item.
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                ItemListAdapter adapter = (ItemListAdapter) mListView.getAdapter();
                CheckList checkList = CheckList.get(getActivity());
                switch (item.getItemId())
                {
                    //User wants to delete some items.
                    case R.id.menu_item_delete:
                        //Loop through the selected items.
                        for (int i = adapter.getCount()-1; i >= 0; i--)
                        {
                            //If item is selected, we need to delete it.
                            if (mListView.isItemChecked(i))
                            {
                                //Remove from db.
                                DatabaseHandler db = new DatabaseHandler(getActivity());
                                db.deleteItem(adapter.getItem(i));
                                //Remove from check list.
                                checkList.deleteItem(adapter.getItem(i));
                            }
                        }
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {

            }
        });

        //When the FAB is pressed we need to switch to the AddItemFragment.
        v.findViewById(R.id.add_item_fab).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //We need to put the AddItemFragment on screen.
                AddItemFragment newFragment = new AddItemFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, newFragment);
                ft.commit();
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
        inflater.inflate(R.menu.overflow_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //User pressed a menu item.
        switch(item.getItemId())
        {
            //User pressed delete all checked.
            case R.id.delete_all_checked_menu_item:
                //We need to delete all checked items from the list.
                int count = CheckList.get(getActivity()).deleteAllChecked();
                //Update the adapter to reflect the changes.
                ((ItemListAdapter)mListView.getAdapter()).notifyDataSetChanged();
                //Show snackbar indicating number of items deleted.
                Snackbar.make(getView(), "" + count + " item(s) deleted.", Snackbar.LENGTH_LONG).show();
                return true;
            //User pressed reminder settings.
            case R.id.reminder_settings_menu_item:
                //We need to change screen to the reminder settings fragment.
                FragmentReminderSettings newFragment = new FragmentReminderSettings();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,newFragment);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Close the keyboard if open.
    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if(v == null)
        {
            v = new View(activity);
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }
}
