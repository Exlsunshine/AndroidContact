package com.example.implementation;

import java.io.ByteArrayOutputStream;
import com.example.contact.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Contact
{
	private static String DEBUG_TAG = "Contact________";
	
	private int id;
	private Drawable portrait;
	private String firstName;
	private String lastName;
	private String company;
	private String mobileNumber;
	private String workNumber;
	private String homeNumber;
	private String emails;
	private String homeAddress;
	private String nickName;

	public Contact(int id, Drawable portrait, String firstName, String lastName, String company, String mobileNumber, String workNumber, String homeNumber, String emails, String homeAddress, String nickName)
	{
		this.id = id;
		this.portrait = portrait;
		this.firstName = firstName;
		this.lastName = lastName;
		this.company = company;
		this.mobileNumber = mobileNumber;
		this.workNumber = workNumber;
		this.homeNumber = homeNumber;
		this.emails = emails;
		this.homeAddress = homeAddress;
		this.nickName = nickName;
	}
	
	public Contact(Drawable portrait, String firstName, String lastName, String company, String mobileNumber, String workNumber, String homeNumber, String emails, String homeAddress, String nickName)
	{
		this.portrait = portrait;
		this.firstName = firstName;
		this.lastName = lastName;
		this.company = company;
		this.mobileNumber = mobileNumber;
		this.workNumber = workNumber;
		this.homeNumber = homeNumber;
		this.emails = emails;
		this.homeAddress = homeAddress;
		this.nickName = nickName;
	}
	
	public Contact(Context context)
	{
		this.portrait = context.getResources().getDrawable(R.drawable.ic_launcher);
		this.firstName = "default value";
		this.lastName = "default value";
		this.company = "default value";
		this.mobileNumber = "default value";
		this.workNumber = "default value";
		this.homeNumber = "default value";
		this.emails = "default value";
		this.homeAddress = "default value";
		this.nickName = "default value";
	}
	
	public void setPortraitData(byte[] pictureData)
	{
		portrait = byteToDrawable(pictureData);
    }
	
	public void setPortraitData(Bitmap bmp)
	{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteData = baos.toByteArray();
        
        this.portrait = byteToDrawable(byteData);
    }

    public byte[] getPortraitData()
    {
        return drawableToByteArray(portrait);
    }
	
	private byte[] drawableToByteArray(Drawable d)
	{
	    if (d != null)
	    {
	        Bitmap imageBitmap = ((BitmapDrawable) d).getBitmap();
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	        byte[] byteData = baos.toByteArray();

	        return byteData;
	    }
	    else
	    {
	    	Log.e(DEBUG_TAG, "null in drawableToByteArray");
	    	return null;
	    }
	}
	
	private Drawable byteToDrawable(byte[] data)
	{
	    if (data == null)
	        return null;
	    else
	        return new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
	}
	
	public String toString()
	{
		String res = "";
		
		res += this.portrait + "\n";
		res += this.firstName + "\n";
		res += this.lastName + "\n";
		res += this.company + "\n";
		res += this.mobileNumber + "\n";
		res += this.workNumber + "\n";
		res += this.homeNumber + "\n";
		res += this.emails + "\n";
		res += this.homeAddress + "\n";
		res += this.nickName + "\n";
		
		return res;		
	}
	
	//	getters
	public int getID() { return this.id; }
	
	public Drawable getPortrait() { return this.portrait; }
	
	public String getFirstName() { return this.firstName; }
	
	public String getLastName() { return this.lastName; }
	
	public String getCompany() { return this.company; }
	
	public String getMobileNumber() { return this.mobileNumber; }
	
	public String getWrokNumber() { return this.workNumber; }
	
	public String getHomeNumber() { return this.homeNumber; }
	
	public String getEmails() { return this.emails; };
	
	public String getHomeAddress() { return this.homeAddress; }
	
	public String getNickName() { return this.nickName; }
	
	//	setters
	public void setID(int id) { this.id = id; }
	
	public void setPortrait(Drawable portrait) { this.portrait = portrait; }
	
	public void setFirstName(String firstName) { this.firstName = firstName; }
	
	public void setLastName(String lastName) { this.lastName = lastName; }
	
	public void setCompany(String company) { this.company = company; }
	
	public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
	
	public void setWorkNumber(String workNumber) { this.workNumber = workNumber; }
	
	public void setHomeNumber(String homeNumber) { this.homeNumber = homeNumber; }
	
	public void setEmails(String emails) { this.emails = emails; }
	
	public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
	
	public void setNickName(String NickName) { this.nickName = nickName; }
}