package com.example.activity;

import org.official.json.JSONObject;

import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.net.Uri;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	private boolean isPortrait = true;
	private Bitmap qrCode;
	
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
        
        portrait.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0)
			{
				AnimatorSet set;
				set = (AnimatorSet) AnimatorInflater.loadAnimator(ContactDetailsActivity.this, R.animator.flip_out);
				set.setTarget(portrait);
				set.start();
				
				set.addListener(new AnimatorListener()
				{
					@Override
					public void onAnimationEnd(Animator arg0)
					{
						if (isPortrait)
							portrait.setBackground(new BitmapDrawable(qrCode));
						else
							portrait.setBackground(contact.getPortrait());
						isPortrait = !isPortrait;
						
						AnimatorSet set;
						set = (AnimatorSet) AnimatorInflater.loadAnimator(ContactDetailsActivity.this, R.animator.flip_in);
						set.setTarget(portrait);
						set.start();
					}
					
					@Override
					public void onAnimationStart(Animator arg0) {}
					
					@Override
					public void onAnimationRepeat(Animator arg0) {}
					
					@Override
					public void onAnimationCancel(Animator arg0) {}
				});
			}
		});
        
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
        
        generateQrcode(buildJson(contact));
	}
	
	private void generateQrcode(String content)
	{
		QRCodeWriter writer = new QRCodeWriter();
	    try 
	    {
	    	BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
	        int width = 512;
	        int height = 512;
	        qrCode = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	        for (int x = 0; x < width; x++)
	        {
	            for (int y = 0; y < height; y++)
	            {
	                if (bitMatrix.get(x, y)==true)
	                	qrCode.setPixel(x, y, Color.BLACK);
	                else
	                	qrCode.setPixel(x, y, Color.WHITE);
	            }
	        }
	    }
	    catch (WriterException e)
	    {
	    	Toast.makeText(ContactDetailsActivity.this, "QR ERROR " + e, Toast.LENGTH_SHORT).show();
	    }
	}
	
	private String buildJson(Contact contact)
	{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put(DatabaseHandler.KEY_FIRST_NAME, contact.getFirstName());
		jsonObj.put(DatabaseHandler.KEY_LAST_NAME, contact.getLastName());
		jsonObj.put(DatabaseHandler.KEY_COMPANY, contact.getCompany());
		jsonObj.put(DatabaseHandler.KEY_MOBILE_NO, contact.getMobileNumber());
		jsonObj.put(DatabaseHandler.KEY_HOME_NO, contact.getHomeNumber());
		jsonObj.put(DatabaseHandler.KEY_WORK_NO, contact.getWrokNumber());
		jsonObj.put(DatabaseHandler.KEY_EMAILS, contact.getEmails());
		jsonObj.put(DatabaseHandler.KEY_HOME_ADDRESS, contact.getHomeAddress());
		jsonObj.put(DatabaseHandler.KEY_NICK_NAME, contact.getNickName());
		
		Log.i("_________My json", jsonObj.toString());
		
		return jsonObj.toString();
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