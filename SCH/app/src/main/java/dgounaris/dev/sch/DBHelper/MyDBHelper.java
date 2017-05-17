package dgounaris.dev.sch.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import dgounaris.dev.sch.Bins.Bin;
import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.People.Service;
import dgounaris.dev.sch.adapter.Trophy;

/**
 * Created by DimitrisLPC on 13/5/2017.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    private Context mContext;

    private static final String DATABASE_NAME = "schdb.db";
    private static String DATABASE_PATH = "";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase myDatabase;

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DATABASE_PATH = mContext.getApplicationInfo().dataDir + "/databases/";
        boolean dbexist = checkDatabase();
        if (dbexist) {
            openDatabase();
        }
        else {
            createDatabase();
        }
    }

    private void createDatabase() {
        boolean dbexist = checkDatabase();
        if (dbexist) {}
        else {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDatabase() {
        boolean checkdb = false;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbFile = new File(myPath);
            checkdb = dbFile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copyDatabase() throws IOException {
        InputStream myinput = mContext.getAssets().open(DATABASE_NAME);
        String outfilename = DATABASE_PATH + DATABASE_NAME;
        OutputStream myoutput = new FileOutputStream(outfilename);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer))>0) {
            myoutput.write(buffer, 0, length);
        }

        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    private void openDatabase() {
        String mypath = DATABASE_PATH + DATABASE_NAME;
        myDatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close(){
        if(myDatabase != null){
            myDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Person getPerson(int id) {

        Person mPerson = null;
        String myQuery = "select * from " + MyDBContract.People.TABLE_NAME + " p where p." + MyDBContract.People.COLUMN_NAME_ID + " = " + id;
        this.openDatabase();
        Cursor cursor = this.myDatabase.rawQuery(myQuery, null);
        if (cursor.moveToFirst()) {
            byte[] myImgByte = cursor.getBlob(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_IMAGE)); //image
            if (myImgByte != null) {
                mPerson = new Person(
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_ID)), //id
                        cursor.getString(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_NAME)), //name
                        cursor.getString(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_SURNAME)), //surname
                        cursor.getInt(cursor. getColumnIndex(MyDBContract.People.COLUMN_NAME_POINTS)), //points
                        BitmapFactory.decodeByteArray(myImgByte, 0, myImgByte.length)
                );
            }
        }
        cursor.close();
        this.close();
        return mPerson;
    }

    public boolean setPersonTrophies(Person myperson) {
        ArrayList<Trophy> myTrophies = new ArrayList<>();
        String myQuery = "select t." + MyDBContract.Trophies.COLUMN_NAME_NAME + ", t." + MyDBContract.Trophies.COLUMN_NAME_DESCRIPTION + ", t." + MyDBContract.Trophies.COLUMN_NAME_IMAGE +
                " from " + MyDBContract.People_Trophies.TABLE_NAME + " pt, " + MyDBContract.Trophies.TABLE_NAME + " t " +
                "where pt." + MyDBContract.People_Trophies.COLUMN_NAME_PERSON_ID + " = " + myperson.getId() + " and pt." + MyDBContract.People_Trophies.COLUMN_NAME_TROPHY_ID + " = t." + MyDBContract.Trophies.COLUMN_NAME_ID;
        this.openDatabase();
        Cursor cursor = this.myDatabase.rawQuery(myQuery, null);
        if (cursor.moveToFirst()) {
            do {
                byte[] myImgByte = cursor.getBlob(cursor.getColumnIndex(MyDBContract.Trophies.COLUMN_NAME_IMAGE)); //image
                myperson.addTrophy(new Trophy(
                        cursor.getString(cursor.getColumnIndex(MyDBContract.Trophies.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndex(MyDBContract.Trophies.COLUMN_NAME_DESCRIPTION)),
                        BitmapFactory.decodeByteArray(myImgByte, 0, myImgByte.length)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return true;
    }

    public ArrayList<Service> getServices() {
        ArrayList<Service> services = new ArrayList<>();
        String myQuery = "select s." + MyDBContract.Services.COLUMN_NAME_ID + ", s." + MyDBContract.Services.COLUMN_NAME_NAME + ", s." + MyDBContract.Services.COLUMN_NAME_POINTS + ", s." + MyDBContract.Services.COLUMN_NAME_EMPTY_SLOTS +
                " from " + MyDBContract.Services.TABLE_NAME + " s where " + MyDBContract.Services.COLUMN_NAME_EMPTY_SLOTS + " > 0 order by " + MyDBContract.Services.COLUMN_NAME_POINTS + " desc";
        this.openDatabase();
        Cursor cursor = this.myDatabase.rawQuery(myQuery, null);
        if (cursor.moveToFirst()) {
            do {
                services.add(new Service(
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.Services.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndex(MyDBContract.Services.COLUMN_NAME_NAME)),
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.Services.COLUMN_NAME_EMPTY_SLOTS)),
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.Services.COLUMN_NAME_POINTS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return services;
    }

    public int redeemService(int serviceId, int pointsNeeded, int personId) {
        this.openDatabase();
        String myQuery = "select " + MyDBContract.People.COLUMN_NAME_POINTS + " from " + MyDBContract.People.TABLE_NAME + " where " + MyDBContract.People.COLUMN_NAME_ID + " = " + personId;
        Cursor cursor = this.myDatabase.rawQuery(myQuery, null);
        int currPoints=0;
        if (cursor.moveToFirst()) {
            currPoints = cursor.getInt(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_POINTS));
        }
        if (currPoints<pointsNeeded) {
            return -1;
        }
        currPoints -= pointsNeeded;
        myQuery = "update " + MyDBContract.People.TABLE_NAME + " set " + MyDBContract.People.COLUMN_NAME_POINTS + " = " + currPoints +
                " where " + MyDBContract.Services.COLUMN_NAME_ID + " = " + serviceId;
        cursor = this.myDatabase.rawQuery(myQuery,null);
        cursor.moveToFirst();
        cursor.close();
        myQuery = "update " + MyDBContract.Services.TABLE_NAME + " set " + MyDBContract.Services.COLUMN_NAME_EMPTY_SLOTS + " = " + MyDBContract.Services.COLUMN_NAME_EMPTY_SLOTS + " - " + 1 +
                " where " + MyDBContract.Services.COLUMN_NAME_ID + " = " + serviceId;
        cursor = this.myDatabase.rawQuery(myQuery, null);
        cursor.moveToFirst();
        cursor.close();
        this.close();
        return currPoints;
    }

    public int set_balance(int points_given, int person_id) {
        this.openDatabase();
        String sql_get_points = "select " + MyDBContract.People.COLUMN_NAME_POINTS + " from " + MyDBContract.People.TABLE_NAME + " where " + person_id + " = " + MyDBContract.People.COLUMN_NAME_ID;
        Cursor cursor;
        cursor = this.myDatabase.rawQuery(sql_get_points, null);
        int data = 0;
        if(cursor.moveToFirst())
            data = cursor.getInt(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_POINTS));
        data += points_given;
        String sql = "update " + MyDBContract.People.TABLE_NAME + " set " + MyDBContract.People.COLUMN_NAME_POINTS + " = " + data + " where " + person_id + " = " + MyDBContract.People.COLUMN_NAME_ID;
        cursor = this.myDatabase.rawQuery(sql,null);
        cursor.moveToFirst();
        cursor.close();
        this.close();
        return data;
    }

    public ArrayList<Person> top_5(){
        this.openDatabase();
        ArrayList<Person> personArrayList = new ArrayList<>();
        String sql = "select " + MyDBContract.People.COLUMN_NAME_ID + ", " + MyDBContract.People.COLUMN_NAME_NAME + " , " + MyDBContract.People.COLUMN_NAME_SURNAME + " , "
                + MyDBContract.People.COLUMN_NAME_POINTS + " , " + MyDBContract.People.COLUMN_NAME_IMAGE +
                " from " + MyDBContract.People.TABLE_NAME + " order by " + MyDBContract.People.COLUMN_NAME_POINTS + " desc " + " limit 3";
        Cursor cursor = this.myDatabase.rawQuery(sql,null);
        if (cursor.moveToFirst()) {
            do {
                byte[] myImgByte = cursor.getBlob(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_IMAGE)); //image
                personArrayList.add(new Person(
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_SURNAME)),
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.People.COLUMN_NAME_POINTS)),
                        BitmapFactory.decodeByteArray(myImgByte, 0, myImgByte.length)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return personArrayList;
    }

    public ArrayList<Bin> getBins() { //perhaps in the future define close area, dont fetch all bins from start
        this.openDatabase();
        ArrayList<Bin> binArrayList = new ArrayList<>();
        String myQuery = "select * from " + MyDBContract.Bins.TABLE_NAME;
        Cursor cursor = this.myDatabase.rawQuery(myQuery, null);
        if (cursor.moveToFirst()) {
            do {
                binArrayList.add(new Bin(
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.Bins.COLUMN_NAME_ID)),
                        new LatLng(cursor.getDouble(cursor.getColumnIndex(MyDBContract.Bins.COLUMN_NAME_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(MyDBContract.Bins.COLUMN_NAME_LONGITUDE))),
                        cursor.getInt(cursor.getColumnIndex(MyDBContract.Bins.COLUMN_NAME_SPACE)) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return binArrayList;
    }
}

