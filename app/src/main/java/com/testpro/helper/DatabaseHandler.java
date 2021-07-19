package com.testpro.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.testpro.model.DressSlideItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbDressMatch";
    private static final String TABLE_ALL_DRESS = "dmClcn";     // DressMatchCollection for storing all shirts and pants image name
    private static final String DMCLCN_ID = "id";
    private static final String DMCLCN_TYP = "dressTyp";       // Shirt(S) / Pnt(P)
    private static final String DMCLCN_IMG_NM = "imgNm";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ALL_DRESS + "("
                + DMCLCN_ID + " INTEGER PRIMARY KEY,"
                + DMCLCN_TYP + " TEXT,"
                + DMCLCN_IMG_NM + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_DRESS);

        // Create tables again
        onCreate(db);
    }

    public void addDress(DressSlideItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DMCLCN_TYP, item.getDressTyp());
        values.put(DMCLCN_IMG_NM, item.getImgNm());

        // Inserting Row
        db.insert(TABLE_ALL_DRESS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all dress type
    public List<DressSlideItem> getAllDresses(String typ) {    //typ=S/P
        List<DressSlideItem> dressSlideItems = new ArrayList<DressSlideItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALL_DRESS +" WHERE "+ DMCLCN_TYP +"='"+typ+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DressSlideItem item = new DressSlideItem();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setDressTyp(cursor.getString(1));
                item.setImgNm(cursor.getString(2));
                // Adding contact to list
                dressSlideItems.add(item);
            } while (cursor.moveToNext());
        }

        // return dress list
        return dressSlideItems;
    }

    // code to update the single dress
    public int updateDress(DressSlideItem dressSlideItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DMCLCN_TYP, dressSlideItem.getDressTyp());
        values.put(DMCLCN_IMG_NM, dressSlideItem.getImgNm());

        // updating row  
        return db.update(TABLE_ALL_DRESS, values, DMCLCN_ID + " = ?",
                new String[] { String.valueOf(dressSlideItem.getId()) });
    }

    // Deleting single dress
    public void deleteDress(DressSlideItem dressSlideItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALL_DRESS, DMCLCN_ID + " = ?",
                new String[] { String.valueOf(dressSlideItem.getId()) });
        db.close();
    }

    // Getting Dress Count
    public int getDressCount(String typ) {      //typ=S/P
        String countQuery = "SELECT  * FROM " + TABLE_ALL_DRESS +" WHERE "+ DMCLCN_TYP +"='"+typ+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();

        // return count  
        return cursor.getCount();
    }
}
