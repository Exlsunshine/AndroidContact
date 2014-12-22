package com.example.implementation;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper
{
	private static final String DEBUG_TAG = "DatabaseHandler";
	private Context context;
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "contactsManager";
	public static final String TABLE_CONTACTS = "contacts";
	
	public static final String KEY_ID = "id";
	public static final String KEY_PORTRAIT = "portrait";
	public static final String KEY_FIRST_NAME = "first_name";
	public static final String KEY_LAST_NAME = "last_name";
	public static final String KEY_COMPANY = "company";
	public static final String KEY_MOBILE_NO = "mobile_number";
	public static final String KEY_WORK_NO = "work_number";
	public static final String KEY_HOME_NO = "home_number";
	public static final String KEY_EMAILS = "emails";
	public static final String KEY_HOME_ADDRESS = "home_address";
	public static final String KEY_NICK_NAME = "nick_name";
	
	public DatabaseHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		 
	    //Add the first contact as the owner of the app,
	    //This contact id should be No.1.
		if (getContactsCount() == 0)
		{
			Contact contact = new Contact(context);
			addContact(contact);
	    }
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		Log.i(DEBUG_TAG, "onCreate...");
	    
		String CREATE_CONTACTS_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY NOT NULL UNIQUE, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);", TABLE_CONTACTS,KEY_ID,KEY_PORTRAIT,KEY_FIRST_NAME,KEY_LAST_NAME,KEY_COMPANY,KEY_MOBILE_NO,KEY_WORK_NO,KEY_HOME_NO,KEY_EMAILS,KEY_HOME_ADDRESS,KEY_NICK_NAME);
	    db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.i(DEBUG_TAG, "onUpgrade..."); 
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        Log.i(DEBUG_TAG, "Move version from " + String.valueOf(oldVersion) + " to " + String.valueOf(newVersion));
        onCreate(db);
	}
	
	/*		Create, Read, Update and Delete		*/
	public void addContact(Contact contact)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_PORTRAIT, contact.getPortraitData());
		values.put(KEY_FIRST_NAME, contact.getFirstName());
		values.put(KEY_LAST_NAME, contact.getLastName());
		values.put(KEY_COMPANY, contact.getCompany());
		values.put(KEY_MOBILE_NO, contact.getMobileNumber());
		values.put(KEY_WORK_NO, contact.getWrokNumber());
		values.put(KEY_HOME_NO, contact.getHomeNumber());
		values.put(KEY_EMAILS, contact.getEmails());
		values.put(KEY_HOME_ADDRESS, contact.getHomeAddress());
		values.put(KEY_NICK_NAME, contact.getNickName());
		
		db.insert(TABLE_CONTACTS, null, values);
		db.close();
	}
	
	public Contact getContact(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Contact contact = null;
		Cursor cursor = db.query(TABLE_CONTACTS, new String[] {KEY_ID,KEY_PORTRAIT,KEY_FIRST_NAME,KEY_LAST_NAME,KEY_COMPANY,KEY_MOBILE_NO,KEY_WORK_NO,KEY_HOME_NO,KEY_EMAILS,KEY_HOME_ADDRESS,KEY_NICK_NAME}, KEY_ID + "=?", new String [] {String.valueOf(id)}, null, null, null, null);
		if (cursor != null)
		{
			cursor.moveToFirst();
			contact = new Contact(Integer.parseInt(cursor.getString(0)), null, cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),cursor.getString(10));
			contact.setPortraitData(cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_PORTRAIT)));
		}
		cursor.close();
		db.close();
		
		return contact;
	}
	
	public List<Contact> getAllContacts()
	{
		List<Contact> contactList = new ArrayList<Contact>();
		
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst())
		{
			do
			{
				Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), null, cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10));
				contact.setPortraitData(cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_PORTRAIT)));
				contactList.add(contact);
			}while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		return contactList;
	}
	
	public int getContactsCount()
	{
		String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		int count = cursor.getCount();
		cursor.close();
		db.close();
		
		return count;
	}
	
	public int updateContact(Contact contact)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_FIRST_NAME, contact.getFirstName());
		values.put(KEY_LAST_NAME, contact.getLastName());
		values.put(KEY_COMPANY, contact.getCompany());
		values.put(KEY_MOBILE_NO, contact.getMobileNumber());
		values.put(KEY_WORK_NO, contact.getWrokNumber());
		values.put(KEY_HOME_NO, contact.getHomeNumber());
		values.put(KEY_EMAILS, contact.getEmails());
		values.put(KEY_HOME_ADDRESS, contact.getHomeAddress());
		values.put(KEY_NICK_NAME, contact.getNickName());
		
		int status =  db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String [] {String.valueOf(contact.getID())});
		db.close();
		
		return status;
	}
	
	public void deleteContact(Contact contact)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String [] {String.valueOf(contact.getID())});
		db.close();
	}
	
	public void cleanDatabase()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_CONTACTS, null, null);
		db.close();
	}
}