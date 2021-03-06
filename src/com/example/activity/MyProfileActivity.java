package com.example.activity;

import org.official.json.JSONObject;
import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import com.example.view.FadingImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zijunlin.Zxing.Demo.CaptureActivity;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * <b>Descriptioin:</b><br>
 * This Activiy is used for editing or checking user's own profile.
 * @author EXLsunshine
 * */
public class MyProfileActivity extends Activity
{
	private EditText firstName;
	private EditText lastName;
	private EditText company;
	private EditText mobileNo;
	private EditText workNo;
	private EditText homeNo;
	private EditText emails;
	private EditText homeAddr;
	private EditText nickName;
	private FadingImageView qrCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		setUI();
		showData();
	}
	
	
	/**
	 * Get user's own profile from database. Load data to current window.
	 * */
	private void showData()
	{
		DatabaseHandler db = new DatabaseHandler(MyProfileActivity.this);
		Contact contact = db.getContact(1);
		
		firstName.setText(contact.getFirstName());
		lastName.setText(contact.getLastName());
		company.setText(contact.getCompany());
		mobileNo.setText(contact.getMobileNumber());
		workNo.setText(contact.getWrokNumber());
		homeNo.setText(contact.getHomeNumber());
		emails.setText(contact.getEmails());
		homeAddr.setText(contact.getHomeAddress());
		nickName.setText(contact.getNickName());
		
		String content = buildJson();
		generateQrcode(content);
	}
	
	/**
	 * Set UI settings.
	 * */
	private void setUI()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		firstName = (EditText)findViewById(R.id.first_name_me);
		lastName = (EditText)findViewById(R.id.last_name_me);
		company = (EditText)findViewById(R.id.company_me);
		mobileNo = (EditText)findViewById(R.id.mobile_no_me);
		workNo = (EditText)findViewById(R.id.work_no_me);
		homeNo = (EditText)findViewById(R.id.home_no_me);
		emails = (EditText)findViewById(R.id.emails_me);
		homeAddr = (EditText)findViewById(R.id.home_addr_me);
		nickName = (EditText)findViewById(R.id.nick_name_me);
		qrCode = (FadingImageView)findViewById(R.id.qrcode_me);
	}
	
	/**
	 * Save the changes to database.
	 * */
	private void saveToDatabase()
	{
		DatabaseHandler db = new DatabaseHandler(MyProfileActivity.this);
		Contact myProfile = db.getContact(1);
		
		if (myProfile == null)
		{
			myProfile = new Contact(1, null, firstName.getText().toString(), lastName.getText().toString(), company.getText().toString(), mobileNo.getText().toString(), workNo.getText().toString(), homeNo.getText().toString(), emails.getText().toString(), homeAddr.getText().toString(), nickName.getText().toString());
			db.addContact(myProfile);
		}
		else
		{
			myProfile.setFirstName(firstName.getText().toString());
			myProfile.setLastName(lastName.getText().toString());
			myProfile.setCompany(company.getText().toString());
			myProfile.setMobileNumber(mobileNo.getText().toString());
			myProfile.setHomeNumber(homeNo.getText().toString());
			myProfile.setWorkNumber(workNo.getText().toString());
			myProfile.setEmails(emails.getText().toString());
			myProfile.setHomeAddress(homeAddr.getText().toString());
			myProfile.setNickName(nickName.getText().toString());
			db.updateContact(myProfile);
		}
	}
	
	/**
	 * Convert the give String to a QRcode. Load the QRcode to ImageView.
	 * @param content the content to be generated in the QRcode.
	 * */
	private void generateQrcode(String content)
	{
		QRCodeWriter writer = new QRCodeWriter();
	    try 
	    {
	    	BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
	        int width = 512;
	        int height = 512;
	        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	        for (int x = 0; x < width; x++)
	        {
	            for (int y = 0; y < height; y++)
	            {
	                if (bitMatrix.get(x, y)==true)
	                    bmp.setPixel(x, y, Color.BLACK);
	                else
	                    bmp.setPixel(x, y, Color.WHITE);
	            }
	        }
	        qrCode.setImageBitmap(bmp);
	    }
	    catch (WriterException e)
	    {
	    	Toast.makeText(MyProfileActivity.this, "QR ERROR " + e, Toast.LENGTH_SHORT).show();
	    }
	}
	
	/**
	 * Convert the user's profile to a string according to the Json format.
	 * @return the Json string which represents the user's profile.
	 * */
	private String buildJson()
	{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put(DatabaseHandler.KEY_FIRST_NAME, firstName.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_LAST_NAME, lastName.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_COMPANY, company.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_MOBILE_NO, mobileNo.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_HOME_NO, homeNo.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_WORK_NO, workNo.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_EMAILS, emails.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_HOME_ADDRESS, homeAddr.getText().toString());
		jsonObj.put(DatabaseHandler.KEY_NICK_NAME, nickName.getText().toString());
		
		Log.e("_________My json", jsonObj.toString());
		
		return jsonObj.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.my_profile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			Intent it = new Intent(MyProfileActivity.this,MainActivity.class);
			startActivity(it);
			
			int version = Integer.valueOf(android.os.Build.VERSION.SDK);  
			if(version >= 5)
				overridePendingTransition(R.anim.open_enter, R.anim.open_exit);
			finish();
		}
		else if (item.getItemId() == R.id.action_refresh)
		{
			saveToDatabase();
			String content = buildJson();
			generateQrcode(content);
			
			Toast.makeText(MyProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
		}
		return true;
	}
}