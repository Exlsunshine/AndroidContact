package com.example.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.ContactContentProvider;
import com.example.implementation.DatabaseHandler;
import com.example.view.ContactListViewAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private Button addNewContact;
	private ListView contactListview;
	
	private ArrayList<HashMap<String,Object>> contactList;
	private ContactListViewAdapter contactAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initUISettings();
		initVariable();
	}
	
	private void initVariable()
	{
		contactList = new ArrayList<HashMap<String,Object>>();
		
		DatabaseHandler db = new DatabaseHandler(this);
		List<Contact> contact = db.getAllContacts();
		for (Contact con : contact)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(DatabaseHandler.KEY_ID, con.getID());
			map.put(DatabaseHandler.KEY_PORTRAIT, con.getPortrait());
			map.put(DatabaseHandler.KEY_FIRST_NAME, con.getFirstName());
			map.put(DatabaseHandler.KEY_LAST_NAME, con.getLastName());
			map.put(DatabaseHandler.KEY_COMPANY, con.getCompany());
			map.put(DatabaseHandler.KEY_MOBILE_NO, con.getMobileNumber());
			map.put(DatabaseHandler.KEY_WORK_NO, con.getWrokNumber());
			map.put(DatabaseHandler.KEY_HOME_NO, con.getHomeNumber());
			map.put(DatabaseHandler.KEY_EMAILS, con.getEmails());
			map.put(DatabaseHandler.KEY_HOME_ADDRESS, con.getHomeAddress());
			map.put(DatabaseHandler.KEY_NICK_NAME, con.getNickName());

			contactList.add(map);
		}
		
		contactAdapter = new ContactListViewAdapter(this,contactList);
		contactListview.setAdapter(contactAdapter);
	}
	
	private void initUISettings()
	{
		contactListview = (ListView)findViewById(R.id.contactListview);
		contactListview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				Intent it = new Intent(MainActivity.this, ContactDetailsActivity.class);
				it.putExtra(DatabaseHandler.KEY_ID, ((Integer)contactList.get(arg2).get(DatabaseHandler.KEY_ID)).intValue());
				
				MainActivity.this.startActivity(it);
			}
		});
		
		addNewContact = (Button)findViewById(R.id.addNewContact);
		addNewContact.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent it = new Intent(MainActivity.this,AddNewContactActivity.class);
				startActivity(it);
				
				startActivityForResult(it, 11);  			
				
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);  
				if(version >= 5)
					overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			}
		});
	}
	
	//Toast.makeText(MainActivity.this, "back", Toast.LENGTH_SHORT).show();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.search_menu, menu);
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    
	    searchView.setOnSearchClickListener(new OnClickListener()
	    {
			@Override
			public void onClick(View arg0)
			{
				contactList.clear();
        		contactAdapter.notifyDataSetChanged();
			}
		});
	    
	    searchView.setOnQueryTextListener(new OnQueryTextListener()
	    {
			@Override
			public boolean onQueryTextSubmit(String arg0)
			{	
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0)
			{
				contactList.clear();
        		contactAdapter.notifyDataSetChanged();
				return true;
			}
		});
	    
	    return true;
	}
	
	@Override
    public void onNewIntent(Intent intent)
	{
        super.onNewIntent(intent);

        String query = intent.getStringExtra(SearchManager.QUERY);
        if(Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
        	String [] projectionsAll = null;
        	ContactContentProvider ccp = new ContactContentProvider();
        	ccp.setContext(MainActivity.this);
        	Cursor managedCursor = 
        	ccp.query(
                    Uri.parse("content://" + "com.example.implementation.ContactContentProvider" + "/" + "contacts"),//.fromParts("content", "com.example.implementation.ContactContentProvider.contacts", null),
                    projectionsAll,    // Which columns to return.
                    DatabaseHandler.KEY_FIRST_NAME + " LIKE ? or " + DatabaseHandler.KEY_MOBILE_NO + " LIKE ?",          // WHERE clause.
                    new String [] {query + "%",query + "%"},          // WHERE clause value substitution
                    DatabaseHandler.KEY_FIRST_NAME);   // Sort order.
        	
        	if (managedCursor != null && managedCursor.moveToFirst())
    		{
    			do
    			{
    				Contact con = new Contact(Integer.parseInt(managedCursor.getString(0)), null, managedCursor.getString(2), managedCursor.getString(3), managedCursor.getString(4), managedCursor.getString(5), managedCursor.getString(6), managedCursor.getString(7), managedCursor.getString(8), managedCursor.getString(9), managedCursor.getString(10));
    				con.setPortraitData(managedCursor.getBlob(managedCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PORTRAIT)));
    				
    				HashMap<String, Object> map = new HashMap<String, Object>();
    				map.put(DatabaseHandler.KEY_ID, con.getID());
    				map.put(DatabaseHandler.KEY_PORTRAIT, con.getPortrait());
    				map.put(DatabaseHandler.KEY_FIRST_NAME, con.getFirstName());
    				map.put(DatabaseHandler.KEY_LAST_NAME, con.getLastName());
    				map.put(DatabaseHandler.KEY_COMPANY, con.getCompany());
    				map.put(DatabaseHandler.KEY_MOBILE_NO, con.getMobileNumber());
    				map.put(DatabaseHandler.KEY_WORK_NO, con.getWrokNumber());
    				map.put(DatabaseHandler.KEY_HOME_NO, con.getHomeNumber());
    				map.put(DatabaseHandler.KEY_EMAILS, con.getEmails());
    				map.put(DatabaseHandler.KEY_HOME_ADDRESS, con.getHomeAddress());
    				map.put(DatabaseHandler.KEY_NICK_NAME, con.getNickName());
    				contactList.add(map);
    			}while (managedCursor.moveToNext());
    			contactAdapter.notifyDataSetChanged();
    		}
        }
    }
}