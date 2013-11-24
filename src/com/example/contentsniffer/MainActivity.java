package com.example.contentsniffer;

import java.util.StringTokenizer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button AddData, UpdateView;
	TextView data;
	TextView input;
	ContentResolver resolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddData = (Button) findViewById(R.id.AddDataButton);
        UpdateView = (Button) findViewById(R.id.UpdateViewButton);
        data = (TextView) findViewById(R.id.contentData);
        input = (TextView) findViewById(R.id.input);
        resolver = getContentResolver();
        AddData.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){
        		//put some data into the database
        		String[] projection = new String[]{BaseColumns._ID,"ACTION", "CATEGORY", "URI", "FLAGS", "SCHEME", "TYPE", "COUNT", "EXTRAS"};
        		StringTokenizer st = new StringTokenizer(input.getText().toString());
        		String selection = "ACTION=? and CATEGORY=? and URI=? and FLAGS=? and SCHEME=? and TYPE=?";
        		String[] selectionArgs = new String[]{st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken()};
        		Cursor cursor = resolver.query(Uri.parse("content://com.example.contentsniffer.database/sniffertable"),
        				projection, selection, selectionArgs, null);
        		if(!cursor.moveToFirst()){
        			//The data isn't in the db, so just insert it
        			ContentValues values = new ContentValues();
            		st = new StringTokenizer(input.getText().toString());
            		values.put("ACTION", st.nextToken());
            		values.put("CATEGORY", st.nextToken());
            		values.put("URI", st.nextToken());
            		values.put("FLAGS", st.nextToken());
            		values.put("SCHEME", st.nextToken());
            		values.put("TYPE", st.nextToken());
            		values.put("COUNT", 1);
            		
            		resolver.insert(Uri.parse("content://com.example.contentsniffer.database/sniffertable"), values);
            		Log.i("Main", values.toString());
        		}
        		else{
        			//the data is already in the db, so update the count...
        			st = new StringTokenizer(input.getText().toString());
        			selectionArgs = new String[]{cursor.getString(0)};
        			ContentValues values = new ContentValues();
        			values.put("ACTION", st.nextToken());
        			values.put("CATEGORY", st.nextToken());
            		values.put("URI", st.nextToken());
            		values.put("FLAGS", cursor.getInt(4));
            		st.nextToken();
            		values.put("SCHEME", st.nextToken());
            		values.put("TYPE", st.nextToken());
            		values.put("COUNT", cursor.getInt(7)+1);
            		
            		
            		Log.i("updating", "count: " + cursor.getInt(7));
            		resolver.update(Uri.parse("content://com.example.contentsniffer.database/sniffertable"), values, "_ID=?", selectionArgs) ;
        		}
        		
        	}
        });
        UpdateView.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){
        		//Refresh the data on the screen
        		String output = "";
        		String[] projection = new String[]{BaseColumns._ID,"ACTION", "CATEGORY", "URI", "FLAGS", "SCHEME", "TYPE", "COUNT", "EXTRAS"};
        		Cursor cursor = resolver.query(Uri.parse("content://com.example.contentsniffer.database/sniffertable"),
        				projection, null, null, null);
        		if(cursor.moveToFirst()){
        			do{
        				output += cursor.getString(0) + cursor.getString(1) + cursor.getString(2)+ cursor.getString(3) + cursor.getString(4)  + cursor.getString(5) + cursor.getString(6)+"," + cursor.getInt(7) + cursor.getString(8) + "\n";
        			}while(cursor.moveToNext());
        		}
        		data.setText(output);
        	}
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
