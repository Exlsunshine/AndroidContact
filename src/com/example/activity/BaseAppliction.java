package com.example.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import com.example.view.ContactListViewWidgetAdapter;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class BaseAppliction extends Application
{
	WindowManager mWM;
	WindowManager.LayoutParams mWMParams;

	private static boolean isExpand = false;
	private static String DEBUG_TAG = "BaseAppliction______";
	
	private ArrayList<Contact> contactList;
	private ContactListViewWidgetAdapter contactAdapter;
	private ListView contactListview;
	private View win;
	
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
		
		contactAdapter = new ContactListViewWidgetAdapter(BaseAppliction.this, contactList);
		contactListview.setAdapter(contactAdapter);
	}
	
	@Override
	public void onCreate()
	{
		registerReceiver(broadcastReceiver, makeGattUpdateIntentFilter());
		
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		win = LayoutInflater.from(this).inflate(R.layout.top_window, null);
		final ImageView img = (ImageView)win.findViewById(R.id.widgetListener);
		
		win.setOnTouchListener(new OnTouchListener()
		{
			float lastX, lastY;

			public boolean onTouch(View v, MotionEvent event)
			{
				final int action = event.getAction();

				float x = event.getX();
				float y = event.getY();
				if (action == MotionEvent.ACTION_DOWN)
				{
					lastX = x;
					lastY = y;
					
					if (!isExpand)
					{
						img.getLayoutParams().height = 200;
						img.getLayoutParams().width = 200;
						
						contactListview = (ListView)win.findViewById(R.id.contactListview_top_window);
						contactListview.setVisibility(View.VISIBLE);
						
						initVariable();
						mWMParams.width = 700;
						mWMParams.height = 500;
						mWM.updateViewLayout(win, mWMParams);
					}
					else
					{
						img.getLayoutParams().height = 100;
						img.getLayoutParams().width = 100;
						
						//reset listView
						contactListview = (ListView)win.findViewById(R.id.contactListview_top_window);
						contactListview.setVisibility(View.GONE);
						mWMParams.width = 100;
						mWMParams.height = 100;
						mWM.updateViewLayout(win, mWMParams);
					}	
					isExpand = !isExpand;
				} 
				else if (action == MotionEvent.ACTION_MOVE)
				{
					mWMParams.x += (int) (x - lastX);
					mWMParams.y += (int) (y - lastY);
					mWM.updateViewLayout(win, mWMParams);
				}
				return true;
			}
		});
		
		WindowManager wm = mWM;
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWMParams = wmParams;
		wmParams.type = 2002; //type是关键，这里的2002表示系统级窗口，你也可以试试2003。可取查帮助文档
		wmParams.format = 1;
		wmParams.flags = 40;

		wmParams.width = 700;//设定大小
		wmParams.height = 500;

		wm.addView(win, wmParams);
	}
	
	public final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context arg0, Intent arg1)
		{
			//reset listView
			contactListview = (ListView)win.findViewById(R.id.contactListview_top_window);
			contactListview.setVisibility(View.GONE);
			mWMParams.width = 100;
			mWMParams.height = 100;
			mWM.updateViewLayout(win, mWMParams);
		}
	};
	
	public IntentFilter makeGattUpdateIntentFilter() 
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Calling");
        
        return intentFilter;
    }
}