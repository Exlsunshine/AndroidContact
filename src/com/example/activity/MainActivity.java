package com.example.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.ContactContentProvider;
import com.example.implementation.DatabaseHandler;
import com.example.implementation.SlideBar;
import com.example.view.ContactListViewAdapter;
import com.zijunlin.Zxing.Demo.CaptureActivity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

public class MainActivity extends Activity
{
	private ListView contactListview;
	private SlideBar slideBar;
	private TextView indexIndicator;
	
	private ArrayList<Contact> contactList;
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
		contactList = new ArrayList<Contact>();
		
		DatabaseHandler db = new DatabaseHandler(this);
		List<Contact> contact = db.getAllContacts();
		for (Contact con : contact)
		{
			//ignore my profile
			if (con.getID() == 1)
				continue;
			contactList.add(con);
		}
		Collections.sort(contactList);
		
		contactAdapter = new ContactListViewAdapter(this,contactList);
		contactListview.setAdapter(contactAdapter);
	}
	
	private void initUISettings()
	{
		indexIndicator = (TextView)findViewById(R.id.indexIndicator);
		slideBar = (SlideBar)findViewById(R.id.sliderBar);
		slideBar.setOnTouchingLetterChangedListener(new LetterListViewListener()); 
		contactListview = (ListView)findViewById(R.id.contactListview);
		contactListview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				Intent it = new Intent(MainActivity.this, ContactDetailsActivity.class);
				it.putExtra(DatabaseHandler.KEY_ID, ((Integer)contactList.get(arg2).getID()).intValue());
				
				MainActivity.this.startActivity(it);
			}
		});
	}
	
	private class LetterListViewListener implements  com.example.implementation.SlideBar.OnTouchingLetterChangedListener  
	{  
		@Override  
		public void onTouchingLetterChanged(final String s, float y, float x)  
		{  
			indexIndicator.setVisibility(View.VISIBLE);
			indexIndicator.setText(s);
			for (int i = 0; i < contactList.size(); i++)
			{
				String lastName = (String)contactList.get(i).getLastName();
				lastName = lastName.toUpperCase();
				if (lastName.startsWith(String.valueOf(s)))
				{
					contactListview.setSelection(i);
					return ;
				}
			}
		}  
		
		@Override  
		public void onTouchingLetterEnd()  
		{  
			indexIndicator.setVisibility(View.GONE);
		}  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.search_menu, menu);
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    
	    MenuItem scan = menu.findItem(R.id.action_scan);
	    scan.setOnMenuItemClickListener(new OnMenuItemClickListener()
	    {
			@Override
			public boolean onMenuItemClick(MenuItem arg0)
			{
				Intent scanIntent = new Intent(MainActivity.this, CaptureActivity.class);
				startActivity(scanIntent);
				return true;
			}
		});
	    
	    MenuItem me = menu.findItem(R.id.action_me);
	    me.setOnMenuItemClickListener(new OnMenuItemClickListener() 
	    {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) 
			{
				Intent it = new Intent(MainActivity.this,MyProfileActivity.class);
				startActivity(it);
				
				return true;
			}
		});
	    
	    
	    MenuItem add = menu.findItem(R.id.action_add);
	    add.setOnMenuItemClickListener(new OnMenuItemClickListener()
	    {
			@Override
			public boolean onMenuItemClick(MenuItem arg0)
			{
				Intent it = new Intent(MainActivity.this,AddNewContactActivity.class);
				it.putExtra(AddNewContactActivity.INTENT_KEY,AddNewContactActivity.INTENT_INVALID_DATA);
				startActivity(it);
				
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);  
				if(version >= 5)
					overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				return true;
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
        		getSuggestions(arg0);
				return true;
			}
		});
	    
	    menu.findItem(R.id.action_search).setOnMenuItemClickListener(new OnMenuItemClickListener()
	    {
			@Override
			public boolean onMenuItemClick(MenuItem arg0)
			{
				contactList.clear();
        		contactAdapter.notifyDataSetChanged();
				return true;
			}
		});
	    
		menu.findItem(R.id.action_search).setOnActionExpandListener(new OnActionExpandListener()
	    {
			@Override
			public boolean onMenuItemActionExpand(MenuItem arg0)
			{
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem arg0)
			{
				initVariable();
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
        	getSuggestions(query);
        }
    }
	
	private void getSuggestions(String query)
	{
		contactList.clear();
		contactAdapter.notifyDataSetChanged();
		
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
				
				contactList.add(con);
			}while (managedCursor.moveToNext());
			Collections.sort(contactList);
			contactAdapter.notifyDataSetChanged();
		}
	}
}