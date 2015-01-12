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
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class MainActivity extends Activity
{
	private ListView contactListview;
	private SlideBar slideBar;
	private TextView indexIndicator;
	
	private ArrayList<Contact> contactList;
	private ContactListViewAdapter contactAdapter;
	
	// 2015-1-9
	private ArrayList<String> deleteList;
	private static int DELETE_MODE = 1;
	private static int SELECT_MODE = 2;
	private static int MODE = SELECT_MODE;
	private TextView delete;
	private TextView all;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume()
	{              
		super.onResume();
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
		
		// 2015-1-9
		deleteList = new ArrayList<String>();
		contactListview.setBackgroundColor(Color.WHITE);
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
				if (MODE == SELECT_MODE)
				{
					Intent it = new Intent(MainActivity.this, ContactDetailsActivity.class);
					it.putExtra(DatabaseHandler.KEY_ID, ((Integer)contactList.get(arg2).getID()).intValue());
					
					MainActivity.this.startActivity(it);
					
					int version = Integer.valueOf(android.os.Build.VERSION.SDK);  
					if(version >= 5)
						overridePendingTransition(R.anim.close_enter, R.anim.close_exit);
				}
				else if (MODE == DELETE_MODE)
				{
					if (deleteList.contains(String.valueOf(arg2)))
					{
						deleteList.remove(String.valueOf(arg2));
						arg1.setBackgroundColor(Color.WHITE);
					}
					else
					{
						deleteList.add(String.valueOf(arg2));
						arg1.setBackgroundColor(Color.LTGRAY);
					}
				}
			}
		});
		
		// 2015-1-9
		delete = (TextView)findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (deleteList.isEmpty())
				{
					Toast.makeText(MainActivity.this, "Please select items.", Toast.LENGTH_SHORT).show();
					return ;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Are you sure to delete contact?");
				builder.setMessage("Click Confirm to delete!");
				builder.setCancelable(true);
				builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						if (!deleteList.isEmpty() && MODE == DELETE_MODE)
						{
							DatabaseHandler db = new DatabaseHandler(MainActivity.this);
							
							for (int i = 0; i < deleteList.size(); i++)
							{
								Contact contact = contactList.get(Integer.parseInt(deleteList.get(i)));
								db.deleteContact(contact);
							}
							
							Toast.makeText(MainActivity.this, "Delete successful!", Toast.LENGTH_SHORT).show();
							deleteList.clear();
							onResume();
						}
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						arg0.cancel();
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		all = (TextView)findViewById(R.id.all);
		all.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (MODE == DELETE_MODE)
				{
					if (deleteList.size() != contactListview.getChildCount())
					{
						deleteList.clear();
						for (int i = 0; i < contactListview.getChildCount(); i++)
						{
							View view = contactListview.getChildAt(i);
							view.setBackgroundColor(Color.LTGRAY);
							deleteList.add(String.valueOf(i));
						}
					}
					else
					{
						deleteList.clear();
						for (int i = 0; i < contactListview.getChildCount(); i++)
						{
							View view = contactListview.getChildAt(i);
							view.setBackgroundColor(Color.WHITE);
						}
					}
				}
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
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.background)
			this.finish();
		return true;
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
	    
	    
	    MenuItem mode = menu.findItem(R.id.action_delete_mode);
	    mode.setOnMenuItemClickListener(new OnMenuItemClickListener() 
	    {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) 
			{
				if (MODE == SELECT_MODE)
				{
					delete.setVisibility(View.VISIBLE);
					delete.setClickable(true);
					all.setVisibility(View.VISIBLE);
					all.setClickable(true);
					MODE = DELETE_MODE;
				}
				else if (MODE == DELETE_MODE)
				{
					delete.setVisibility(View.GONE);
					delete.setClickable(false);
					all.setVisibility(View.GONE);
					all.setClickable(false);
					MODE = SELECT_MODE;
					
					for (int i = 0; i < contactListview.getChildCount(); i++)
					{
						View view = contactListview.getChildAt(i);
						view.setBackgroundColor(Color.WHITE);
					}
					deleteList.clear();
					
					//onRestart();
				}
				
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
				
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);  
				if(version >= 5)
					overridePendingTransition(R.anim.open_enter, R.anim.open_exit);
				
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
	
	/**
	 * Get suggestions when user executes a search query.
	 * <br>This operation supports <b>fuzzy query.</b>
	 * So the result will contain any contact whose last name, first name or mobile number contains the <b>query</b> string.
	 * @param query the content that's the user queried.
	 * <br> the query could be contact's last name, first name or mobile number.
	 * */
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
                DatabaseHandler.KEY_FIRST_NAME + " LIKE ? or " + DatabaseHandler.KEY_MOBILE_NO + " LIKE ? or " + DatabaseHandler.KEY_LAST_NAME + " LIKE ? ",          // WHERE clause.
                new String [] {query + "%", query + "%", query + "%"},          // WHERE clause value substitution
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