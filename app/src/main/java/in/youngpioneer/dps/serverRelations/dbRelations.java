package in.youngpioneer.dps.serverRelations;

/**
 * Created by vyomkeshjha on 30/07/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class dbRelations extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notifications.db";
    public static final String NotificationsTable = "Notifications";
    public static final String ProvidersName = "Message";

//The table name is Notifications





    private HashMap hp;

    public dbRelations(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table Notifications " +
                        "(id integer primary key, Title text,Timestamp text,Message text)"
        );
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





        db.insert("Notifications", null, contentValues);
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

    public boolean updateProvider(Integer id, String Title, String Timestamp, String Message)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Title", Title);
        contentValues.put("Timestamp", Timestamp);
        contentValues.put("Message", Message);

        db.update("Notifications", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteProvider(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Notifications",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllProviders()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Notifications", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(ProvidersName)));
            res.moveToNext();
        }
        return array_list;
    }
}