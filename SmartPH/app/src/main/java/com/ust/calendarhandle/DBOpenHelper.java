package com.ust.calendarhandle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_EVENTS_TABLE = "create table " +DBStructure.EVENT_TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            +DBStructure.EVENT+" TEXT, "+DBStructure.TIME+" TEXT, "+DBStructure.DATE+" TEXT, "+ DBStructure.MONTH+" TEXT, "
            +DBStructure.YEAR+" TEXT, "+DBStructure.Notify+" TEXT, "+DBStructure.TYPE+" TEXT)";

    private static final String DROP_EVENTS_TABLE= "DROP TABLE IF EXISTS "+DBStructure.EVENT_TABLE_NAME;

    public DBOpenHelper(@Nullable Context context) {
        super(context, DBStructure.DB_NAME,null,DBStructure.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EVENTS_TABLE);
        onCreate(db);
    }


    public void SaveEvent(String event, String time, String date, String month, String year, String notify, String type, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.EVENT,event);
        contentValues.put(DBStructure.TIME,time);
        contentValues.put(DBStructure.DATE,date);
        contentValues.put(DBStructure.MONTH,month);
        contentValues.put(DBStructure.YEAR,year);
        contentValues.put(DBStructure.Notify,notify);
        contentValues.put(DBStructure.TYPE,type);
        database.insert(DBStructure.EVENT_TABLE_NAME,null,contentValues);
    }

    public Cursor ReadAllEvents(SQLiteDatabase database){
        String selectQuery = "SELECT * FROM " + DBStructure.EVENT_TABLE_NAME;
        return database.rawQuery(selectQuery,null);
    }

    public Cursor ReadEvents(String date, SQLiteDatabase database){
        String[] Projections = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR,DBStructure.TYPE};
        String Selection = DBStructure.DATE +"=?";
        String[] SelectionArgs = {date};

        return database.query(DBStructure.EVENT_TABLE_NAME, Projections, Selection, SelectionArgs,null,null,null);
    }

    public Cursor ReadIDEvents(String date, String event, String time, SQLiteDatabase database){
        String[] Projections = {DBStructure.ID, DBStructure.Notify};
        String Selection = DBStructure.DATE +"=? and "+DBStructure.EVENT+"=? and "+DBStructure.TIME+"=?";
        String[] SelectionArgs = {date,event,time};

        return database.query(DBStructure.EVENT_TABLE_NAME, Projections, Selection, SelectionArgs,null,null,null);
    }

    public void deleteEvent(String event, String date, String time, String type, SQLiteDatabase database){
        String selection = DBStructure.EVENT+"=? and "+DBStructure.DATE+"=? and "+DBStructure.TIME+"=? and "+DBStructure.TYPE+"=?";
        String[] selectionArg = {event,date,time,type};
        database.delete(DBStructure.EVENT_TABLE_NAME,selection,selectionArg);
    }

    public void DeleteAllEvent(SQLiteDatabase database){
        /*String command = "DELETE FROM" + ;*/
        database.delete(DBStructure.EVENT_TABLE_NAME,null,null);
    }

    public void updateEvent(String date, String event, String time, String notify, String type, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.Notify,notify);
        String Selection = DBStructure.DATE +"=? and "+DBStructure.EVENT+"=? and "+DBStructure.TIME+"=? and "+DBStructure.TYPE+"=?";
        String[] SelectionArgs = {date,event,time,type};
        database.update(DBStructure.EVENT_TABLE_NAME, contentValues, Selection, SelectionArgs);
    }
}
