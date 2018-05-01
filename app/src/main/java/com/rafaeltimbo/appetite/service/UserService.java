package com.rafaeltimbo.appetite.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rafaeltimbo.appetite.database.DatabaseContract;
import com.rafaeltimbo.appetite.database.DatabaseManager;
import com.rafaeltimbo.appetite.model.UserModel;

import static com.rafaeltimbo.appetite.utils.Constants.tableCreatorString;
import static com.rafaeltimbo.appetite.utils.Constants.tables;


public class UserService {
    private Context context;
    private DatabaseManager dbm;

    public UserService(Context context) {
        this.dbm = new DatabaseManager(context);
        this.context = context;
        dbm.dbInitialize(tables, tableCreatorString);
    }

    public UserModel getUser(String email, String password ) {
        UserModel userInDatabse = null;

        SQLiteDatabase db = dbm.getReadableDatabase();
        String[] params = new String[]{ email, password };
        String[] columns = new String[]{DatabaseContract.UserEntry._ID, DatabaseContract.UserEntry.COLUMN_EMAIL, DatabaseContract.UserEntry.COLUMN_PASSWORD, DatabaseContract.UserEntry.COLUMN_USERNAME};
        Cursor cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, columns,
                DatabaseContract.UserEntry.COLUMN_EMAIL + " = ? AND " +
                        DatabaseContract.UserEntry.COLUMN_PASSWORD + "= ?", params,
                null, null, null);

        if (cursor.moveToFirst()) {
            userInDatabse = new UserModel();
            //userInDatabse.id = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry._ID));
            userInDatabse.setUsername(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_USERNAME)));
            userInDatabse.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_EMAIL)));
            userInDatabse.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_PASSWORD)));
        }
        cursor.close();
        return userInDatabse;
    }

    public boolean userEmailExists(String email ) {
        boolean result = false;

        SQLiteDatabase db = dbm.getReadableDatabase();
        String[] params = new String[]{ email };
        String[] columns = new String[]{DatabaseContract.UserEntry._ID, DatabaseContract.UserEntry.COLUMN_EMAIL, DatabaseContract.UserEntry.COLUMN_PASSWORD};
        Cursor cursor = db.query(DatabaseContract.UserEntry.TABLE_NAME, columns,
                        DatabaseContract.UserEntry.COLUMN_EMAIL + "= ?", params,
                null, null, null);

        if (cursor.moveToFirst()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    public boolean saveUser(String email, String password){
        try {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseContract.UserEntry.COLUMN_EMAIL, email);
            cv.put(DatabaseContract.UserEntry.COLUMN_PASSWORD, password);

            dbm.addRecordUsingContentValues(cv, DatabaseContract.UserEntry.TABLE_NAME);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void close(){
        dbm.close();
    }

}
