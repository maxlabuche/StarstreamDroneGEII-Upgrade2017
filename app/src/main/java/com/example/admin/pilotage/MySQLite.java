package com.example.admin.pilotage;

/**
 * Created by JC on 15/01/2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class which inherits from SQLiteOpenHelper.
 * Allows the management of the SQLite file.
 */
public class MySQLite extends SQLiteOpenHelper {

    /**
     * Define the name and the version of the file of storage of the bdd
     */
    private static final String DATABASE_NAME = "db.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static MySQLite sInstance;

    public static synchronized MySQLite getInstance(Context context) {
        if (sInstance == null) { sInstance = new MySQLite(context); }
        return sInstance;
    }

    private MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creation of the bdd and his table "navdata_bdd"
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NavdataManager_BDD.CREATE_TABLE_NAVDATA_BDD);
    }

    /**
     * Function for the update of the bdd if the version number was incremented
     * @param sqLiteDatabase
     * @param i
     * @param i2
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        onCreate(sqLiteDatabase);
    }

} // MySQLite's class