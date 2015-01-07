package com.example.view;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.contact.R;
import com.example.implementation.Contact;

public class ContactListViewWidgetAdapter extends BaseAdapter
{
	private static String DEBUG_TAG = "ContactListViewWidgetAdapter______";
	
	private ArrayList<Contact> list;
	private LayoutInflater inflater;
	private Context context;
	
	public ContactListViewWidgetAdapter(Context context,ArrayList<Contact> list)
	{
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
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
			arg1 = inflater.inflate(R.layout.contact_listview_widget_item, null);
			item.protrait = (FadingImageView)arg1.findViewById(R.id.portrait);
			item.name = (TextView)arg1.findViewById(R.id.name);
			arg1.setTag(item);
		}
		else
			item = (ListViewItem)arg1.getTag();
		
		item.protrait.setImageDrawable((Drawable)list.get(arg0).getPortrait());
		item.name.setText((String)list.get(arg0).getLastName() + "\n" + (String)list.get(arg0).getFirstName());
		
		final int index = arg0;
		arg1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Log.i(DEBUG_TAG, "Click");
				Intent dialIntent = new Intent(Intent.ACTION_DIAL);
				dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				dialIntent.setData(Uri.parse("tel:" + list.get(index).getMobileNumber()));
				context.startActivity(dialIntent);
				
				Intent intent = new Intent("Calling");
				context.sendBroadcast(intent);
			}
		});
		
		return arg1;
	}
	
	private class ListViewItem
	{
		public FadingImageView protrait;
		public TextView name;
	}
}