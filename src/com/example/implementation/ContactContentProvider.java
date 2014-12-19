package com.example.implementation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ContactContentProvider extends ContentProvider
{
	private Context context;
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static 
	{
		sUriMatcher.addURI("com.example.implementation.ContactContentProvider", DatabaseHandler.TABLE_CONTACTS, 1);
		sUriMatcher.addURI("com.example.implementation.ContactContentProvider", DatabaseHandler.TABLE_CONTACTS + "/#", 2);
	}
	
	public void setContext(Context context)
	{
		this.context = context;
	}
	
	@Override
	public boolean onCreate() 
	{
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projections, String where, String[] whereArgs, String sortOrder) 
	{
		/*
		Log.i("________Provider", "in ");
		DatabaseHandler db = new DatabaseHandler(context);
		return db.getReadableDatabase().rawQuery("SELECT * FROM contacts", null);
		*/
		
		DatabaseHandler db = new DatabaseHandler(context);
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		qBuilder.setTables(DatabaseHandler.TABLE_CONTACTS);
        Cursor c = qBuilder.query(db.getReadableDatabase(),
        		projections,
        		where,
        		whereArgs,
                null,
                null,
                sortOrder);
        c.setNotificationUri(context.getContentResolver(), uri);
		
        return c;
	}
	
	@Override
	public String getType(Uri arg0) 
	{
		String type = "";
		
		switch (sUriMatcher.match(arg0))
        {
            case 1:
            	type = "vnd.android.cursor.dir/vnd.com.example.implementation.ContactContentProvider." + DatabaseHandler.TABLE_CONTACTS;
            case 2:
            	type = "vnd.android.cursor.item/vnd.com.example.implementation.ContactContentProvider." + DatabaseHandler.TABLE_CONTACTS;
            default:
        }
		return type;
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2)
	{
		return 0;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) 
	{
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3)
	{
		return 0;
	}
}