package com.example.activity;

import org.official.json.JSONObject;
import com.example.contact.R;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
	private ImageView qrCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		setUI();
	}
	
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
		qrCode = (ImageView)findViewById(R.id.qrcode_me);
	}
	
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
		
		Toast.makeText(MyProfileActivity.this, jsonObj.toString(), Toast.LENGTH_SHORT).show();
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
			finish();
		}
		else if (item.getItemId() == R.id.action_refresh)
		{
			saveToDatabase();
			String content = buildJson();
			generateQrcode(content);
		}
		return true;
	}
}