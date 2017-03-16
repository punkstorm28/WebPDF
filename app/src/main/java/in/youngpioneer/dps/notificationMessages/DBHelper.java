package in.youngpioneer.dps.notificationMessages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by vyomkeshjha on 23/04/16.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notifications.db";
    public static final String NotificationsTable = "Notifications";
    public static final String Message = "Message";
    public static final String Timestamp = "Timestamp";


//The table name is Notifications

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table Notifications " +
                        "(id integer primary key, Title text,Timestamp text,Message text, ImageURL text)"
        );
        //Added Image URL here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Notifications");
        onCreate(db);
    }

    public boolean insertProvider(String Title, String Message, String Timestamp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Title", Title);
        contentValues.put("Timestamp", Timestamp);
        contentValues.put("Message", Message);
        contentValues.put("ImageURL","null");

        if(numberOfRows()<20)
            db.insert("Notifications", null, contentValues);
        else
        {
            replaceOldest(Title,Timestamp,Message,null);
        }
        return true;
    }
    // copy method, to be used when an imageURL is present
    public boolean insertProvider(String Title, String Message, String Timestamp,String ImageURL)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Title", Title);
        contentValues.put("Timestamp", Timestamp);
        contentValues.put("Message", Message);
        contentValues.put("ImageURL",ImageURL);
        if(numberOfRows()<20)
        db.insert("Notifications", null, contentValues);
        else
        {
            replaceOldest(Title,Timestamp,Message,ImageURL);
        }
        return true;
    }


    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Notifications where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, NotificationsTable);
        return numRows;
    }

    public int findOldestEntry()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor res =  db.rawQuery( "select * from Notifications", null );
        Cursor res =  db.rawQuery( "select * from Notifications ORDER BY Timestamp ", null );

        res.moveToFirst();


            array_list.add(res.getString(res.getColumnIndex(Timestamp)));

        return Integer.parseInt(array_list.get(0));
    }

    public boolean replaceOldest( String Title, String Timestamp, String Message,String ImageURL)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Title", Title);
        contentValues.put("Timestamp", Timestamp);
        contentValues.put("Message", Message);
        if(ImageURL!=null)
        {
            contentValues.put("ImageURL",ImageURL);
        }
        db.update("Notifications", contentValues, "Timestamp = ? ", new String[] { Timestamp } );
        return true;
    }

    public Integer deleteProvider(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Notifications",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllMessages()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
       // Cursor res =  db.rawQuery( "select * from Notifications", null );
        Cursor res =  db.rawQuery( "select * from Notifications ORDER BY Timestamp ", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Message)));
            res.moveToNext();
        }
        Log.i("Messages"," "+array_list);
        return array_list;
    }
    public ArrayList<DbDataMap>  getDataFromDB()
    {
        try{
            Log.i("OLDEST TIME"," entry : " +findOldestEntry());
        }
        catch (Exception w)
        {

        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Notifications ORDER BY Timestamp DESC", null );
        res.moveToFirst();
        ArrayList<DbDataMap> dataFromDB = new ArrayList<>();
        while(res.isAfterLast() == false){
            DbDataMap map = new DbDataMap();
            map.setMessage(res.getString(res.getColumnIndex(Message)));
            map.setTimestamp(res.getString(res.getColumnIndex(Timestamp)));
            map.setTitle(res.getString(res.getColumnIndex("Title")));
            if(res.getString(res.getColumnIndex("ImageURL"))!=null)
            map.setImageUrl(res.getString(res.getColumnIndex("ImageURL")));

            dataFromDB.add(map);
            res.moveToNext();
        }

        return dataFromDB;
    }
    public String getLatestTimestamp()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor res =  db.rawQuery( "select * from Notifications", null );
        Cursor res =  db.rawQuery( "select * from Notifications ORDER BY Timestamp", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Timestamp)));
            res.moveToNext();
        }
        try {
            return array_list.get(array_list.size()-1);
        }
        catch (ArrayIndexOutOfBoundsException e){
            return String.valueOf(0000);
        }
    }

}