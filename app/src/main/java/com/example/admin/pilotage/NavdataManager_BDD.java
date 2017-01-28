package com.example.admin.pilotage;

/**
 * Created by JC on 15/01/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class for the management of the table "navdata_bdd", for example to write, read or to delete
 * recordings.
 */

public class NavdataManager_BDD {

    private static final String TABLE_NAME = "navdata_bdd";
    public static final String KEY_ID="id_nav";
    public static final String KEY_BATTERY_NAV ="batterie_nav";
    public static final String KEY_INCLAVAR_NAV="inclavar_nav";
    public static final String KEY_ROTATION_NAV="rotation_nav";
    public static final String KEY_INCLDG_NAV="incldg_nav";
    public static final String KEY_SPEED_NAV ="vitesse_nav";
    public static final String KEY_ALTITUDE_NAV="altitude_nav";
    public static final String CREATE_TABLE_NAVDATA_BDD = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID+" INTEGER primary key," +
            " "+ KEY_BATTERY_NAV +" TEXT," +
            " "+KEY_INCLAVAR_NAV+" TEXT," +
            " "+KEY_ROTATION_NAV+" TEXT," +
            " "+KEY_INCLDG_NAV+" TEXT," +
            " "+ KEY_SPEED_NAV +" TEXT," +
            " "+KEY_ALTITUDE_NAV+" TEXT" +
            ");";
    /**
     * Administrator of the file SQLite
     */
    private MySQLite myBaseSQLite;
    private SQLiteDatabase db;

    /**
     * Constructor
     */
    public NavdataManager_BDD(Context context)
    { myBaseSQLite = MySQLite.getInstance(context); }

    /**
     * Opening of the table in reading and in writing
     */
    public void open()
    {
        db = myBaseSQLite.getWritableDatabase();
    }

    /**
     * Close the access to the bdd
     */
    public void close()
    {
        db.close();
    }

    /**
     * Function to add a recording in the table
     * @param navdata_bdd
     * @return the id of the new record or -1 in case of error
     */
    public long addNavdata(Navdata_BDD navdata_bdd) {

        ContentValues values = new ContentValues();
        values.put(KEY_BATTERY_NAV, navdata_bdd.getBattery_Navdata());
        values.put(KEY_INCLAVAR_NAV, navdata_bdd.getInclavar_Navdata());
        values.put(KEY_ROTATION_NAV, navdata_bdd.getRotation_Navdata());
        values.put(KEY_INCLDG_NAV, navdata_bdd.getIncldg_Navdata());
        values.put(KEY_SPEED_NAV, navdata_bdd.getSpeed_Navdata());
        values.put(KEY_ALTITUDE_NAV, navdata_bdd.getAltitude_Navdata());

        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Function not used in our project, but allows the modification of a record in the table
     * @param navdata_bdd
     * @return The number of records affected by the request
     */
    public int modNavdata(Navdata_BDD navdata_bdd) {

        ContentValues values = new ContentValues();
        values.put(KEY_BATTERY_NAV, navdata_bdd.getBattery_Navdata());
        values.put(KEY_INCLAVAR_NAV, navdata_bdd.getInclavar_Navdata());
        values.put(KEY_ROTATION_NAV, navdata_bdd.getRotation_Navdata());
        values.put(KEY_INCLDG_NAV, navdata_bdd.getIncldg_Navdata());
        values.put(KEY_SPEED_NAV, navdata_bdd.getSpeed_Navdata());
        values.put(KEY_ALTITUDE_NAV, navdata_bdd.getAltitude_Navdata());

        String where = KEY_ID+" = ?";
        String[] whereArgs = {navdata_bdd.getId_Navdata()+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }


    /**
     * Function not used in our project, but allows the deletion of a record in the table
     * @param navdata_bdd
     * @return The number of records affected by the WHERE clause, 0 otherwise
     */
    public int supNavdata(Navdata_BDD navdata_bdd) {

        String where = KEY_ID+" = ?";
        String[] whereArgs = {navdata_bdd.getId_Navdata()+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    /**
     * Function not used in our project, but allows the read of one recording whose id is passed as a parameter
     * @param id Id of the recording
     * @return The record corresponding to the requested id
     */
    public Navdata_BDD getNavdata(int id) {

        Navdata_BDD a=new Navdata_BDD(0,"","","","","","");

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID+"="+id, null);
        if (c.moveToFirst()) {
            a.setId_Navdata(c.getInt(c.getColumnIndex(KEY_ID)));
            a.setBattery_Navdata(c.getString(c.getColumnIndex(KEY_BATTERY_NAV)));
            a.setInclavar_Navdata(c.getString(c.getColumnIndex(KEY_INCLAVAR_NAV)));
            a.setRotation_Navdata(c.getString(c.getColumnIndex(KEY_ROTATION_NAV)));
            a.setIncldg_Navdata(c.getString(c.getColumnIndex(KEY_INCLDG_NAV)));
            a.setSpeed_Navdata(c.getString(c.getColumnIndex(KEY_SPEED_NAV)));
            a.setAltitude_Navdata(c.getString(c.getColumnIndex(KEY_ALTITUDE_NAV)));
            c.close();
        }

        return a;
    }

    /**
     * Function which selects all table records
     * @return A cursor of the records
     */
    public Cursor getNavdata() {

        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);


    }

    /**
     * Function that calculates and returns the maximum altitude contained in the bdd
     * @return The maximum altitude
     */
    public int Altmax(){

        int iAltitudemax=0;
        int iAltitudeattime=0;
        Cursor c = getNavdata();


        if (c.moveToFirst()) {

            do {

                iAltitudeattime=Integer.valueOf(c.getString(c.getColumnIndex(NavdataManager_BDD.KEY_ALTITUDE_NAV)));

                if(iAltitudeattime>iAltitudemax){
                    iAltitudemax=iAltitudeattime;
                }

            }
            while (c.moveToNext());
        }
        c.close(); //Close of the cursor
        return iAltitudemax;
    }

    /**
     * Function that calculates and returns the maximum speed contained in the bdd
     * @return
     */
    public int Speedmax(){

        int iSpeedmax=0;
        int iSpeedattime=0;
        Cursor c = getNavdata();

        if (c.moveToFirst()) {

            do {

                iSpeedattime=Integer.valueOf(c.getString(c.getColumnIndex(NavdataManager_BDD.KEY_SPEED_NAV)));

                if(iSpeedattime>iSpeedmax){
                    iSpeedmax=iSpeedattime;
                }

            }
            while (c.moveToNext());
        }
        c.close(); //Close of the cursor

        return iSpeedmax;

    }

    /**
     * Function that calculates and returns the average altitude of the fly
     * @return
     */
    public int Altitudemoy(){

        int iTotal=0;
        int iAltmoy=0;
        int iCounter=0;
        Cursor c = getNavdata();


        if (c.moveToFirst()) {

            do {

                iTotal=iTotal + Integer.valueOf(c.getString(c.getColumnIndex(NavdataManager_BDD.KEY_ALTITUDE_NAV)));
                iCounter=iCounter+1;
            }
            while (c.moveToNext());

            iAltmoy=iTotal/iCounter;

        }
        c.close(); // Close of the cursor

        return iAltmoy;

    }


    /**
     * Function that calculates and returns the average speed of the fly
     * @return
     */
    public int Speedmoy(){

        int iTotal=0;
        int iSpeedmoy=0;
        int iCounter=0;
        Cursor c = getNavdata();


        if (c.moveToFirst()) {

            do {

                iTotal=iTotal + Integer.valueOf(c.getString(c.getColumnIndex(NavdataManager_BDD.KEY_SPEED_NAV)));
                iCounter=iCounter+1;
            }
            while (c.moveToNext());

            iSpeedmoy=iTotal/iCounter;

        }
        c.close(); //Close of the cursor
        return iSpeedmoy;

    }


} //NavdataManager_BDD's class