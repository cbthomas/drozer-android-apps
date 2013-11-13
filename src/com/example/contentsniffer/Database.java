package com.example.contentsniffer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class Database extends ContentProvider {
	private static final String DBNAME = "snifferdb"; //name of the db
	private SQLiteDatabase db; //holds the db instance
	private MainDatabaseHelper mOpenHelper;
	public static final Uri SNIFFER_URI = Uri.parse("content://com.example.contentsniffer.database/sniffertable");
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		//determine which table to open, do error checking, etc.
		db = mOpenHelper.getWritableDatabase();
		//First query the db to see if the intent already is logged
		db.insert("sniffertable", null, arg1);
		db.close();
		getContext().getContentResolver().notifyChange(arg0, null); //not sure why
		return null;
	}


	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mOpenHelper = new MainDatabaseHelper(
				getContext(), //application's context
				DBNAME, //name of the db
				null, //use the default SQLite cursor
				1);
		return true;
	}


	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder sq = new SQLiteQueryBuilder();
		sq.setTables("sniffertable");
		db = mOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("sniffertable", arg1, arg2, arg3, null, null, arg4);
		cursor.setNotificationUri(getContext().getContentResolver(), arg0);
		return cursor;
	}


	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		db = mOpenHelper.getWritableDatabase();
		db.update("sniffertable", arg1, arg2, arg3);
		getContext().getContentResolver().notifyChange(arg0, null);
		return 0;
	}
    
private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
				"sniffertable" + //table name
				"(" + //the columns in the table
				"_ID INTEGER PRIMARY KEY AUTOINCREMENT," + 
				"ACTION TEXT NOT NULL," +
				"CATEGORY TEXT," +
				"URI TEXT NOT NULL," + 
				"FLAGS TEXT NOT NULL," +
				"SCHEME TEXT NOT NULL," +
				"TYPE TEXT NOT NULL," +
				"COUNT INTEGER NOT NULL," + 
				"EXTRAS TEXT" +
				");";
protected static final class MainDatabaseHelper extends SQLiteOpenHelper{
	MainDatabaseHelper(Context context, String DBNAME, CursorFactory factory, int version){
		super(context, DBNAME, null, 1);
	}
	public void onCreate(SQLiteDatabase db){
		db.execSQL(SQL_CREATE_MAIN);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
}
