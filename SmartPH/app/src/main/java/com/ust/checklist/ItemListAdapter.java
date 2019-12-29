package com.ust.checklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import com.ust.smartph.R;
/**
 * Created by Jordan on 12/29/2015.
 */
public class ItemListAdapter extends BaseAdapter
{
    private Context mContext;
    private List<Item> mItems;

    private Item mCurrentItem;

    public ItemListAdapter(Context context, List<Item> items)
    {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public Item getItem(int position)
    {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return mItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup Parent)
    {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout,null);
            holder = new ViewHolder();

            //Get references to View elements.
            holder.titleTextView = (TextView)convertView.findViewById(R.id.list_item_title_textView);
            holder.infoTextView = (TextView)convertView.findViewById(R.id.list_item_info_textView);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.list_item_checkBox);
            holder.iconImageView = (ImageView)convertView.findViewById(R.id.list_item_colorSplotch);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        //Get the item for this list element.
        mCurrentItem = mItems.get(position);

        //Set this list elements information.
        holder.titleTextView.setText(mCurrentItem.getTitle());
        holder.infoTextView.setText(mCurrentItem.getDetails());
        holder.checkBox.setChecked(mCurrentItem.isChecked());
        holder.iconImageView.setImageDrawable(CheckList.get(mContext).getIconDrawable(mCurrentItem.getIconKey()));

        //Tag the View so we know what item was checked.
        holder.checkBox.setTag(mCurrentItem.getId());

        //User pressed the checkbox, we need to change the appropriate item.
        holder.checkBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //We need first to find which item was checked.
                Item item = CheckList.get(mContext).getListItem(Integer.parseInt(v.getTag().toString()));
                //Now we need to change the items isChecked to the other option.
                Boolean prevBool = item.isChecked();
                item.setChecked(!prevBool);
                //We also need to update the item in the db.
                DatabaseHandler db = new DatabaseHandler(mContext);
                db.updateItem(item);
            }
        });

        return convertView;
    }

    private static class ViewHolder
    {
        TextView titleTextView;
        TextView infoTextView;
        CheckBox checkBox;
        ImageView iconImageView;
    }
}
