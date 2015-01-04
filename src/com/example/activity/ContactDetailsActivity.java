package com.example.activity;

import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactDetailsActivity extends Activity
{
	private ImageView portrait;
	private Button dialMobile;
	private Button dialHome;
	private Button dialWork;
	private Button sms;
	private TextView mobileNo;
	private TextView homeNo;
	private TextView workNo;
	private TextView email;
	private TextView homeAddr;
	private TextView nickName;
    
	private int contactId = -1;
	private Contact contact = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        
        Intent it = getIntent();
        contactId = it.getIntExtra(DatabaseHandler.KEY_ID, contactId);
        
        portrait = (ImageView)findViewById(R.id.portrait_details_layout);
        dialMobile = (Button)findViewById(R.id.dial_mobile);
        dialHome = (Button)findViewById(R.id.dial_home);
        dialWork = (Button)findViewById(R.id.dial_work);
        sms = (Button)findViewById(R.id.sms);
        mobileNo = (TextView)findViewById(R.id.mobileNo_details_layout);
        homeNo = (TextView)findViewById(R.id.homeNo_details_layout);
        workNo = (TextView)findViewById(R.id.workNo_details_layout);
        email = (TextView)findViewById(R.id.email_details_layout);
        homeAddr = (TextView)findViewById(R.id.homeAddr_details_layout);
        nickName = (TextView)findViewById(R.id.nickname_details_layout);
        
        showDetails(contactId);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
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
        
        dialMobile.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0)
			{
				Intent dialIntent = new Intent(Intent.ACTION_DIAL);
				dialIntent.setData(Uri.parse("tel:" + contact.getMobileNumber()));
				startActivity(dialIntent);
			}
		});
        
        dialHome.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0)
			{
				Intent dialIntent = new Intent(Intent.ACTION_DIAL);
				dialIntent.setData(Uri.parse("tel:" + contact.getHomeNumber()));
				startActivity(dialIntent);
			}
		});
        
        dialWork.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0)
			{
				Intent dialIntent = new Intent(Intent.ACTION_DIAL);
				dialIntent.setData(Uri.parse("tel:" + contact.getWrokNumber()));
				startActivity(dialIntent);
			}
		});
    }
	
	private void showDetails(int id)
	{
		DatabaseHandler db = new DatabaseHandler(this);
		contact = db.getContact(id);

		portrait.setBackground(contact.getPortrait());
		mobileNo.setText(contact.getMobileNumber());
		homeNo.setText(contact.getHomeNumber());
		workNo.setText(contact.getWrokNumber());
		email.setText(contact.getEmails());
		homeAddr.setText(contact.getHomeAddress());
		nickName.setText(contact.getNickName());
		
        setTitle(contact.getLastName() + " " + contact.getFirstName());
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
			Intent intent = new Intent(ContactDetailsActivity.this,AddNewContactActivity.class);
			intent.putExtra(AddNewContactActivity.INTENT_KEY, String.valueOf(contactId));
			startActivity(intent);
			this.finish();
		}
		return true;
	}
}