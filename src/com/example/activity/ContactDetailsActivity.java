package com.example.activity;

import org.official.json.JSONObject;

import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import com.zijunlin.Zxing.Demo.CaptureActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactDetailsActivity extends Activity
{
	private TextView name;
	private ImageView portrait;
	private TextView mobileNo;
	private ImageView dial;
	private ImageView sms;
    
	private int contactId = -1;
	private Contact contact = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        
        Intent it = getIntent();
        contactId = it.getIntExtra(DatabaseHandler.KEY_ID, contactId);
        
        name = (TextView)findViewById(R.id.name_details_layout);
        portrait = (ImageView)findViewById(R.id.portrait_details_layout);
        mobileNo = (TextView)findViewById(R.id.mobileNo_details_layout);
        dial = (ImageView)findViewById(R.id.dial);
        sms = (ImageView)findViewById(R.id.sms);
        
        sms.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0)
			{
				Intent sms = new Intent(Intent.ACTION_VIEW);
				//sms.setData(Uri.parse("sms:"));
				sms.setType("vnd.android-dir/mms-sms");
				sms.putExtra("address", "12125551212");
				sms.putExtra("sms_body", "tttttt");
				startActivity(sms);
			}
		});
        
        dial.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0)
			{
				Intent dialIntent = new Intent(Intent.ACTION_DIAL);
				dialIntent.setData(Uri.parse("tel:" + contact.getMobileNumber()));
				startActivity(dialIntent);
			}
		});
        
        getDetails(contactId);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
	
	private void getDetails(int id)
	{
		DatabaseHandler db = new DatabaseHandler(this);
		contact = db.getContact(id);
	
		name.setText(contact.getLastName() + " " + contact.getFirstName());
		mobileNo.setText(contact.getMobileNumber());
		portrait.setBackground(contact.getPortrait());
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.contact_details, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			finish();
		}
		else if (item.getItemId() == R.id.action_edit)
		{
			Intent intent1 = new Intent();
			intent1.putExtra(AddNewContactActivity.INTENT_KEY, AddNewContactActivity.MODIFY_FLAG + buildJson());
			intent1.putExtra(DatabaseHandler.KEY_ID, String.valueOf(contactId));
			intent1.setClass(ContactDetailsActivity.this,AddNewContactActivity.class);
			startActivity(intent1);
			finish();
		}
		return true;
	}
    
    private String buildJson()
	{
		JSONObject jsonObj = new JSONObject();
		DatabaseHandler db = new DatabaseHandler(ContactDetailsActivity.this);
		Contact myProfile = db.getContact(contactId);
		
		jsonObj.put(DatabaseHandler.KEY_FIRST_NAME, myProfile.getFirstName());
		jsonObj.put(DatabaseHandler.KEY_LAST_NAME, myProfile.getLastName());
		jsonObj.put(DatabaseHandler.KEY_COMPANY, myProfile.getCompany());
		jsonObj.put(DatabaseHandler.KEY_MOBILE_NO, myProfile.getMobileNumber());
		jsonObj.put(DatabaseHandler.KEY_HOME_NO, myProfile.getHomeNumber());
		jsonObj.put(DatabaseHandler.KEY_WORK_NO, myProfile.getWrokNumber());
		jsonObj.put(DatabaseHandler.KEY_EMAILS, myProfile.getEmails());
		jsonObj.put(DatabaseHandler.KEY_HOME_ADDRESS, myProfile.getHomeAddress());
		jsonObj.put(DatabaseHandler.KEY_NICK_NAME, myProfile.getNickName());
		
		Log.e("_________My json", jsonObj.toString());
		
		return jsonObj.toString();
	}
}