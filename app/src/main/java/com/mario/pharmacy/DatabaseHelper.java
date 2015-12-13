package com.mario.pharmacy;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by mario on 12/11/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_PATH = "/data/data/com.mario.pharmacy/databases/";
    private static final String DATABASE_NAME = "pharmacy.db";
    private static String TABLE_NAME = "pharmacy";
    private static String ID = "_id";
    private static String NAME = "name";
    private static String ADDRESS = "address";
    private static String LATITUDE = "latitude";
    private static String LONGITUDE = "longitude";
    private static String PICTURE = "picture";
    private static String PHONE = "phone";
    private static String MAIL = "mail";
    private static String WORKDAY = "workday";
    private static String SATURDAY = "saturday";

    private static final int VERSION = 1;
    private static File DATABASE_FILE;

    private boolean mInvalidDatabaseFile = false;
    private boolean mIsUpgraded = false;
    private Context mContext;

    private int mOpenConnections = 0;

    private static DatabaseHelper mInstance;

    synchronized static public DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;

        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            if (db != null) {
                db.close();
            }

            DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);

            if (mInvalidDatabaseFile) {
                copyDatabase();
            }
            if (mIsUpgraded) {
                doUpgrade();
            }
        } catch (SQLiteException e) {
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mInvalidDatabaseFile = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          int old_version, int new_version) {
        mInvalidDatabaseFile = true;
        mIsUpgraded = true;
    }


    private void doUpgrade() {

    }

    @Override
    public synchronized void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // increment the number of users of the database connection.
        mOpenConnections++;
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    //to avoid closing database while it's still in use
    @Override
    public synchronized void close() {
        mOpenConnections--;
        if (mOpenConnections == 0) {
            super.close();
        }
    }

    //fill newly created database with our pharmacy.db
    private void copyDatabase() {
        AssetManager assetManager = mContext.getResources().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(DATABASE_NAME);
            out = new FileOutputStream(DATABASE_FILE);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        setDatabaseVersion();
        mInvalidDatabaseFile = false;
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version = " + VERSION);
        } catch (SQLiteException e) {
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    //query for getting latitude from database
    public ArrayList<String> getLatitude() {
        DatabaseHelper helper = DatabaseHelper.getInstance(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sqlQuery = "SELECT " + DatabaseHelper.LATITUDE + " FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<String> values = new ArrayList<>();

        while (cursor.moveToNext()) {
            values.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return values;
    }

    public ArrayList<String> getLongitude() {
        DatabaseHelper helper = DatabaseHelper.getInstance(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sqlQuery = "SELECT " + DatabaseHelper.LONGITUDE + " FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<String> values = new ArrayList<>();

        while (cursor.moveToNext()) {
            values.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return values;
    }


    public ArrayList<String> getName() {
        DatabaseHelper helper = DatabaseHelper.getInstance(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sqlQuery = "SELECT " + DatabaseHelper.NAME + " FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<String> values = new ArrayList<>();

        while (cursor.moveToNext()) {
            values.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return values;
    }

    public ArrayList<String> getAddress() {
        DatabaseHelper helper = DatabaseHelper.getInstance(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sqlQuery = "SELECT " + DatabaseHelper.ADDRESS + " FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<String> values = new ArrayList<>();

        while (cursor.moveToNext()) {
            values.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return values;
    }

    //query for getting Rating from database
    public ArrayList<String> getData(String name) {
        DatabaseHelper helper = DatabaseHelper.getInstance(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<String> values = new ArrayList<>();

        String sqlQuery = "SELECT " + DatabaseHelper.ADDRESS + "," + DatabaseHelper.PICTURE + "," + DatabaseHelper.PHONE + "," + DatabaseHelper.MAIL + "," + DatabaseHelper.WORKDAY + "," + DatabaseHelper.SATURDAY + "," + DatabaseHelper.LATITUDE +  "," + DatabaseHelper.LONGITUDE +" FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.NAME + "=\"" + name + "\";";

        Cursor cursor = db.rawQuery(sqlQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            values.add(cursor.getString(0));
            values.add(cursor.getString(1));
            values.add(cursor.getString(2));
            values.add(cursor.getString(3));
            values.add(cursor.getString(4));
            values.add(cursor.getString(5));
            values.add(cursor.getString(6));
            values.add(cursor.getString(7));
            cursor.moveToNext();
        }
    cursor.close();
    db.close();
    return values;

}


}
