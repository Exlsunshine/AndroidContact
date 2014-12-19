package com.example.activity;

import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

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
}