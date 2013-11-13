package com.example.contentsniffer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;
import android.util.Base64;
import android.util.Log;

public class intentReceiver extends BroadcastReceiver{
    Set<String> keys, categories;
    ContentResolver resolver;
	@Override
	//action, package, category, uri, flags, scheme, type, extras(number, 
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i("YAY", arg1.toString());
		List<String> sortedCategories;
		resolver = arg0.getContentResolver();
		//Variables corresponding to database entry columns
		String action="", category="", uri="", scheme="", type="", flags="", extras="";
		//initialize categories to a csv of the categories in the intent (arg1)
		categories = arg1.getCategories();	
		if(categories != null){
			sortedCategories = new ArrayList<String>(categories);
			java.util.Collections.sort(sortedCategories);
			for(String i : sortedCategories)
				category+=i + ",";
		}
		if(arg1.getDataString() != null)
			uri = arg1.getDataString();
		flags = ((Integer)arg1.getFlags()).toString();
		if(arg1.getScheme() != null)
			scheme = arg1.getScheme();
		if(arg1.getType() != null)
			type = arg1.getType();
		action = arg1.getAction();
		
		if(arg1.getExtras() != null){
			final Parcel parcel = Parcel.obtain();
			parcel.writeBundle(arg1.getExtras());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream zos;
			try {
				zos = new GZIPOutputStream(new BufferedOutputStream(bos));
				zos.write(parcel.marshall());
				zos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			extras = Base64.encodeToString(bos.toByteArray(), 0);
			parcel.recycle();
		}
		//now we have values in all fields that can have values
		//now query the db to see if we should insert or update
		//put some data into the database
		String[] projection = new String[]{BaseColumns._ID,"ACTION", "CATEGORY", "URI", "FLAGS", "SCHEME", "TYPE", "COUNT", "EXTRAS"};
		String selection = "ACTION=? and CATEGORY=? and URI=? and FLAGS=? and SCHEME=? and TYPE=? and EXTRAS=?";
		String[] selectionArgs = new String[]{action, category, uri, flags, scheme, type, extras};
		Cursor cursor = resolver.query(Uri.parse("content://com.example.contentsniffer.database/sniffertable"),
				projection, selection, selectionArgs, null);
		if(!cursor.moveToFirst()){
			//The data isn't in the db, so just insert it
			ContentValues values = new ContentValues();
    		values.put("ACTION", action);
    		values.put("CATEGORY",category);
    		values.put("URI", uri);
    		values.put("FLAGS", flags);
    		values.put("SCHEME",scheme);
    		values.put("TYPE", type);
    		values.put("COUNT", 1);
    		values.put("EXTRAS", extras);
    		
    		resolver.insert(Uri.parse("content://com.example.contentsniffer.database/sniffertable"), values);
		}
		else{
			//the data is already in the db, so update the count...
			selectionArgs = new String[]{cursor.getString(0)};
			ContentValues values = new ContentValues();
			values.put("ACTION", action);
			values.put("CATEGORY", category);
    		values.put("URI", uri);
    		values.put("FLAGS", flags);
    		values.put("SCHEME",scheme );
    		values.put("TYPE", type);
    		values.put("COUNT", cursor.getInt(7)+1);
    		values.put("EXTRAS", extras);
    		
    		Log.i("updating", "count: " + cursor.getInt(7));
    		resolver.update(Uri.parse("content://com.example.contentsniffer.database/sniffertable"), values, "_ID=?", selectionArgs) ;
		}
		
	}

}
