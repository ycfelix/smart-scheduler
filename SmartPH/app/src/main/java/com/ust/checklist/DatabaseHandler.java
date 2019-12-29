package com.ust.checklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan on 1/9/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "myDB";
    private static final String TABLE_ITEMS = "items";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_CHECKED = "checked";
    private static final String KEY_ICON = "icon";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEMS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_DETAILS + " TEXT," + KEY_CHECKED + " INTEGER," + KEY_ICON + " TEXT" + ")";
        db.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public long addItem(Item item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Store item values.
        values.put(KEY_TITLE,item.getTitle());
        values.put(KEY_DETAILS, item.getDetails());
        //Store boolean as 1 -> true, 0 -> false.
        if(item.isChecked())
        {
            values.put(KEY_CHECKED,1);
        }
        else
        {
            values.put(KEY_CHECKED,0);
        }
        values.put(KEY_ICON,item.getIconKey());

        //Insert into table.
        long tempid = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return tempid;
    }

    public Item getItem(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] keyString = new String[]{KEY_ID,KEY_TITLE,KEY_DETAILS,KEY_CHECKED,KEY_ICON};
        String[] idString = new String[]{String.valueOf(id)};

        Cursor cursor = db.query(TABLE_ITEMS, keyString, KEY_ID + "=?", idString, null, null, null, null);

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        db.close();

        return new Item(cursor);
    }

    public List<Item> getCheckList()
    {
        List<Item> checkList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //Loop through all rows and add to list.
        if(cursor.moveToFirst())
        {
            do
            {
                checkList.add(new Item(cursor));
            }while(cursor.moveToNext());
        }

        db.close();

        return checkList;
    }

    public void deleteItem(Item item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_ID + " = ?",new String[]{String.valueOf(item.getId())});
        db.close();
    }

    public void updateItem(Item item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //Put item information.
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE,item.getTitle());
        values.put(KEY_DETAILS,item.getDetails());
        //Store boolean as 1 -> true, 0 -> false.
        if(item.isChecked())
        {
            values.put(KEY_CHECKED,1);
        }
        else
        {
            values.put(KEY_CHECKED,0);
        }
        values.put(KEY_ICON,item.getIconKey());

        db.update(TABLE_ITEMS, values, KEY_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }
}
