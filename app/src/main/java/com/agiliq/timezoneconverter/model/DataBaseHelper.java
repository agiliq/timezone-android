package com.agiliq.timezoneconverter.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper mDatabaseHelper = null;
    private static String DB_PATH;
    private static String DB_NAME = "world_time.db";
    private final Context mContext;
    private SQLiteDatabase mTimeZoneDataBase;

    public static DataBaseHelper getInstance(Context context){

        if(mDatabaseHelper == null){
            mDatabaseHelper = new DataBaseHelper(context.getApplicationContext());
        }
        return mDatabaseHelper;
    }


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        mContext = context;
        DB_PATH = mContext.getFilesDir().getAbsolutePath() + File.separator;
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (!dbExist) {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        String myPath = DB_PATH + DB_NAME;
        return new File(myPath).exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        mTimeZoneDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (mTimeZoneDataBase != null)
            return mTimeZoneDataBase;

        return super.getReadableDatabase();
    }

    @Override
    public synchronized void close() {
        if (mTimeZoneDataBase != null)
            mTimeZoneDataBase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
