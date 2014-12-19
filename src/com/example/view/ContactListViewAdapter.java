package com.example.view;

import java.util.ArrayList;
import java.util.HashMap;
import com.example.contact.R;
import com.example.implementation.DatabaseHandler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListViewAdapter extends BaseAdapter
{
	private ArrayList<HashMap<String,Object>> list;
	private LayoutInflater inflater;
	
	public ContactListViewAdapter(Context context,ArrayList<HashMap<String, Object>> list)
	{
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		ListViewItem item = new ListViewItem();
		
		if (arg1 == null)
		{
			arg1 = inflater.inflate(R.layout.contact_listview_item, null);
			item.protrait = (ImageView)arg1.findViewById(R.id.portrait);
			item.name = (TextView)arg1.findViewById(R.id.name);
			item.phoneNo = (TextView)arg1.findViewById(R.id.mobileNo);
			arg1.setTag(item);
		}
		else
			item = (ListViewItem)arg1.getTag();
		
		item.protrait.setBackground((Drawable)list.get(arg0).get(DatabaseHandler.KEY_PORTRAIT));
		item.name.setText((String)list.get(arg0).get(DatabaseHandler.KEY_LAST_NAME) + " " + (String)list.get(arg0).get(DatabaseHandler.KEY_FIRST_NAME));
		item.phoneNo.setText((String)list.get(arg0).get(DatabaseHandler.KEY_MOBILE_NO));
		
		return arg1;
	}
	
	private class ListViewItem
	{
		public ImageView protrait;
		public TextView name;
		public TextView phoneNo;
	}
}