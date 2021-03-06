package com.example.activity;

import java.util.HashMap;
import java.util.Map;
import org.official.json.JSONObject;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.contact.R;
import com.example.implementation.ButtonAnimations;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import com.example.implementation.ImageUtils;
import com.example.view.FadingImageView;
import com.zijunlin.Zxing.Demo.CaptureActivity;

/**
 * <b>Descriptioin:</b><br>
 * This Activiy is used for adding new contact or editing existing contact.
 * <br>There are three ways to activate
 * this activity:
 * <br><b>#1</b> From a button triggered by {@link MainActivity}.
 * <br><b>#2</b> From a QRcode result processed by {@link CaptureActivity}.
 * <br><b>#3</b> From a button triggered by {@link ContactDetailsActivity}.
 * <br><br>Both <b>#1</b> & <b>#2</b> are going to add new contact.
 * <br><b>#3</b> is going to edit an existing contact.
 * @author EXLsunshine
 * */
public class AddNewContactActivity extends Activity
{	
	public static String INTENT_KEY = "QRCODE_INTENT_KEY";
	public static String INTENT_INVALID_DATA = "INVALID";
	private static String DEBUG_TAG = "AddNewContactActivity_________";
	
	private Button cancel;
	private Button save;
	private Button addPhone;
	private Button addOthers;
	private ImageView imgCenter;
	private ImageView imgGallery;
	private ImageView imgCamera;
	private FadingImageView portrait;
	private EditText firstName;
	private EditText lastName;
	private EditText company;
	
	/**
	 * Phone type. There are there types for the phone number:
	 * <br><b>#1</b> Mobile number
	 * <br><b>#2</b> Home number
	 * <br><b>#3</b> Work number
	 * */
	private String [] phoneType = {"Mobile","Home","Work"};
	
	/**
	 * Additional information type. There are there types for the additional information:
	 * <br><b>#1</b> E-mails
	 * <br><b>#2</b> Home addres
	 * <br><b>#3</b> Nick name
	 * */
	private String [] othersType = {"E-mails","Home addres","Nick name"};
	private int cnt = 0;
	private int othersId = 0;
	private boolean hasInit = false;
	
	private int ENTER_TYPE;
	private static class EnterType
	{
		static public int FROM_ADD_BUTTON = 1;
		static public int FROM_SCAN_BUTTON = 2;
		static public int FROM_EDIT_BUTTON = 3;
	}
	String qrInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_contact);
		
		cancel = (Button)findViewById(R.id.cancel);
		save = (Button)findViewById(R.id.done);
		addPhone = (Button)findViewById(R.id.add_phone);
		addOthers = (Button)findViewById(R.id.add_others);
		imgCenter = (ImageView)findViewById(R.id.imgCenter);
		imgGallery = (ImageView)findViewById(R.id.imgGallery);
		imgCamera = (ImageView)findViewById(R.id.imgCamera);
		portrait = (FadingImageView)findViewById(R.id.portrait);
		firstName = (EditText)findViewById(R.id.first_name);
		lastName = (EditText)findViewById(R.id.last_name);
		company = (EditText)findViewById(R.id.company);
		
		imgGallery.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				selectFromMedia();
			}
		});
		
		imgCamera.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				selectFromCamera();
			}
		});
		
		imgCenter.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (hasInit)
					return ;
				else
				{
					hasInit = true;
					ButtonAnimations btn =  new ButtonAnimations(imgCenter, imgCamera, imgGallery);
				}
			}
		});
		
		addOthers.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				addOthersView("",0);
			}
		});
		
		addPhone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				addPhoneView("",0);
			}
		});
		
		save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("firstName", ((EditText)findViewById(R.id.first_name)).getText().toString());
				map.put("lastName", ((EditText)findViewById(R.id.last_name)).getText().toString());
				map.put("company", ((EditText)findViewById(R.id.company)).getText().toString());
				
				LinearLayout addPhoneOutterLayout = (LinearLayout)findViewById(R.id.add_phone_outter_layout);
				for (int i = 2; i < addPhoneOutterLayout.getChildCount(); i++)
				{
					View view = addPhoneOutterLayout.getChildAt(i);
					Spinner sp = (Spinner)view.findViewById(110);
					EditText et = (EditText)view.findViewById(10);
					Log.e(DEBUG_TAG, phoneType[sp.getSelectedItemPosition()]);
					Log.e(DEBUG_TAG, et.getText().toString());
					map.put(phoneType[sp.getSelectedItemPosition()], et.getText().toString());
				}
				
				LinearLayout addOthersOutterLayout = (LinearLayout)findViewById(R.id.add_others_outter_layout);
				for (int i = 2; i < addOthersOutterLayout.getChildCount(); i++)
				{
					View view = addOthersOutterLayout.getChildAt(i);
					Spinner sp = (Spinner)view.findViewById(110);
					EditText et = (EditText)view.findViewById(10);
					map.put(othersType[sp.getSelectedItemPosition()], et.getText().toString());
				}
				
				map.put("portrait", portrait.getDrawable());
				Contact contact = new Contact((Drawable)map.get("portrait"), (String)map.get("firstName"), (String)map.get("lastName"), (String)map.get("company"), (String)map.get("Mobile"), (String)map.get("Home"), (String)map.get("Work"), (String)map.get("E-mails"), (String)map.get("Home addres"), (String)map.get("Nick name"));
				saveToDatabase(contact);
				
				AddNewContactActivity.this.finish();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent it = new Intent(AddNewContactActivity.this, MainActivity.class);
				startActivity(it);
				
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);  
				if(version >= 5)
					overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				
				AddNewContactActivity.this.finish();
			}
		});
		
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        qrInfo = getIntent().getExtras().getString(INTENT_KEY);
        splitQrInfo(qrInfo);
	}
	
	/**
	 * Add a specific type of phone number to the current window.
	 * @param value the phone number
	 * @param indexOfPhoneType the index of the {@link #phoneType}
	 * 
	 * @see {@link #phoneType}
	 * 
	 * */
	private void addPhoneView(String value, int indexOfPhoneType)
	{
		LinearLayout addPhoneOutterLayout = (LinearLayout)findViewById(R.id.add_phone_outter_layout);
		LinearLayout layout = new LinearLayout(AddNewContactActivity.this);
		layout.setId(cnt++);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		
		Spinner sp = new Spinner(AddNewContactActivity.this);
		sp.setId(110);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddNewContactActivity.this, android.R.layout.simple_spinner_item, phoneType);
		sp.setAdapter(adapter);
		sp.setSelection(indexOfPhoneType);
		
		EditText et = new EditText(AddNewContactActivity.this);
		et.setText(value);
		et.setId(10);
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		ImageView img = new ImageView(AddNewContactActivity.this);
		img.setBackgroundResource(R.drawable.minus);
		LayoutParams params = new LayoutParams(100,100);
		img.setLayoutParams(params);
		img.setOnClickListener(deleteClickListener);
		
		layout.addView(img);
		layout.addView(sp);
		layout.addView(et);
		addPhoneOutterLayout.addView(layout);
		
		AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
		alpha.setDuration(1000);
		layout.startAnimation(alpha);
	}
	
	/**
	 * Add a specific type of additional information to the current window.
	 * @param value the information
	 * @param indexOfOthersType the index of the {@link #othersType}
	 * 
	 * @see {@link #othersType}
	 * 
	 * */
	private void addOthersView(String value, int indexOfOthersType)
	{
		LinearLayout addOthersOuterLayout = (LinearLayout)findViewById(R.id.add_others_outter_layout);
		
		LinearLayout layout = new LinearLayout(AddNewContactActivity.this);
		layout.setId(othersId++);
		layout.setOrientation(LinearLayout.HORIZONTAL);
						
		Spinner sp = new Spinner(AddNewContactActivity.this);
		sp.setId(110);
		ArrayAdapter<String> arrAdp = new ArrayAdapter<String>(AddNewContactActivity.this, android.R.layout.simple_spinner_item, othersType);
		sp.setAdapter(arrAdp);
		sp.setSelection(indexOfOthersType);
		
		EditText et = new EditText(AddNewContactActivity.this);
		et.setId(10);
		et.setText(value);
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		ImageView img = new ImageView(AddNewContactActivity.this);
		img.setBackgroundResource(R.drawable.minus);
		LayoutParams params = new LayoutParams(100,100);
		img.setLayoutParams(params);
		img.setOnClickListener(deleteOthersClickListener);
		
		layout.addView(img);
		layout.addView(sp);
		layout.addView(et);
		addOthersOuterLayout.addView(layout);
		
		AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
		alpha.setDuration(1000);
		layout.startAnimation(alpha);
	}
	
	/**
	 * Check whether the given String is a number.
	 * @param num the number to be checked.
	 * @return 	true if the <b>num</b> is a number.
	 * 			<br>false if <b>num</b> is not a number.
	 * */
	private boolean isNumberic(String num)
	{
		for (char c : num.toCharArray())
		{
			if (!Character.isDigit(c))
				return false;
		}
		
		return true;
	}
	
	
	private void splitQrInfo(String qrInfo)
	{
		if (qrInfo.equals(INTENT_INVALID_DATA))
		{
			ENTER_TYPE = EnterType.FROM_ADD_BUTTON;
		}
		else if (isNumberic(qrInfo))
		{
			DatabaseHandler db = new DatabaseHandler(this);
			Contact contact = db.getContact(Integer.parseInt(qrInfo));
			
			firstName.setText(contact.getFirstName());
			lastName.setText(contact.getLastName());
			company.setText(contact.getCompany());
			addPhoneView(contact.getMobileNumber(), 0);
			addPhoneView(contact.getHomeNumber(), 1);
			addPhoneView(contact.getWrokNumber(), 2);
			addOthersView(contact.getEmails(),0);
			addOthersView(contact.getHomeAddress(),1);
			addOthersView(contact.getNickName(),2);
			portrait.setImageDrawable(contact.getPortrait());			
			
			ENTER_TYPE = EnterType.FROM_EDIT_BUTTON;
		}
		else
		{
			JSONObject json = new JSONObject(qrInfo);
			
			firstName.setText(json.getString(DatabaseHandler.KEY_FIRST_NAME));
			lastName.setText(json.getString(DatabaseHandler.KEY_LAST_NAME));
			company.setText(json.getString(DatabaseHandler.KEY_COMPANY));
			addPhoneView(json.getString(DatabaseHandler.KEY_MOBILE_NO), 0);
			addPhoneView(json.getString(DatabaseHandler.KEY_HOME_NO), 1);
			addPhoneView(json.getString(DatabaseHandler.KEY_WORK_NO), 2);
			addOthersView(json.getString(DatabaseHandler.KEY_EMAILS),0);
			addOthersView(json.getString(DatabaseHandler.KEY_HOME_ADDRESS),1);
			addOthersView(json.getString(DatabaseHandler.KEY_NICK_NAME),2);
			
			ENTER_TYPE = EnterType.FROM_SCAN_BUTTON;
		}
	}
	
	/**
	 * Save the given contact to database.
	 * 
	 * @param contact the contact to be saved.
	 * */
	private void saveToDatabase(Contact contact)
	{
		DatabaseHandler db = new DatabaseHandler(this);
		
		if (ENTER_TYPE == EnterType.FROM_ADD_BUTTON || ENTER_TYPE == EnterType.FROM_SCAN_BUTTON)
		{
			db.addContact(contact);
			Toast.makeText(AddNewContactActivity.this, "Add successful!", Toast.LENGTH_LONG).show();
		}
		else if (ENTER_TYPE == EnterType.FROM_EDIT_BUTTON)
		{
			contact.setID(Integer.parseInt(qrInfo));
			db.updateContact(contact);
			Toast.makeText(AddNewContactActivity.this, "Update successful!", Toast.LENGTH_LONG).show();
		}
	}
	
	private OnClickListener deleteClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			final LinearLayout addPhoneOutterLayout = (LinearLayout)findViewById(R.id.add_phone_outter_layout);
			final View v = (View) arg0.getParent();
			
			AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(1000);
			v.startAnimation(alpha);

			alpha.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation arg0) {}
				
				@Override
				public void onAnimationRepeat(Animation arg0) {}
				
				@Override
				public void onAnimationEnd(Animation arg0) 
				{
					addPhoneOutterLayout.removeView(v);
				}
			});
		}
	};
	
	private OnClickListener deleteOthersClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			final LinearLayout addOthersOutterLayout = (LinearLayout)findViewById(R.id.add_others_outter_layout);
			final View v = (View) arg0.getParent();
			
			AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(1000);
			v.startAnimation(alpha);
			
			alpha.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation arg0) {}
				
				@Override
				public void onAnimationRepeat(Animation arg0) {}
				
				@Override
				public void onAnimationEnd(Animation arg0) 
				{
					addOthersOutterLayout.removeView(v);
				}
			});
		}
	};
	
	/**
	 * Select contact's portrait from gallery.
	 * */
	private void selectFromMedia()
	{
		Intent pickPhoto = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto , 1);
	}
	
	/**
	 * Select contact's portrait from camera.
	 * */
	private void selectFromCamera()
	{
		Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePicture, 0);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
	{ 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
		switch(requestCode)
		{
			case 0:
			    if(resultCode == RESULT_OK)
			    {  
			    	Log.i("onActivityResult_________", "takePicture");
			        Uri selectedImage = imageReturnedIntent.getData();
					Bitmap bmp = ImageUtils.decodeUri(this,selectedImage,100,100);
					portrait.setImageBitmap(bmp);
					//portrait.setBackground(new BitmapDrawable(bmp));
			    }
			    break; 
			case 1:
			    if(resultCode == RESULT_OK)
			    {  
			    	Log.i("onActivityResult_________", "pickPhoto");
			        Uri selectedImage = imageReturnedIntent.getData();
					Bitmap bmp = ImageUtils.decodeUri(this,selectedImage,100,100);
					portrait.setImageBitmap(bmp);
					//portrait.setBackground(new BitmapDrawable(bmp));
			    }
			break;
			default:
				Log.i("onActivityResult_________", "default");
				break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.add_new_contact, menu);
		
		MenuItem item = menu.findItem(R.id.action_delete);
		if (ENTER_TYPE == EnterType.FROM_EDIT_BUTTON)
			item.setVisible(true);
		else
			item.setVisible(false);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			Intent it = new Intent(AddNewContactActivity.this, MainActivity.class);
			startActivity(it);
			
			int version = Integer.valueOf(android.os.Build.VERSION.SDK);  
			if(version >= 5)
				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			
			finish();
		}
		else if (item.getItemId() == R.id.action_delete)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(AddNewContactActivity.this);
			builder.setTitle("Are you sure to delete contact?");
			builder.setMessage("Click Confirm to delete!");
			builder.setCancelable(true);
			builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					DatabaseHandler db = new DatabaseHandler(AddNewContactActivity.this);
					Contact contact = db.getContact(Integer.parseInt(qrInfo));
					db.deleteContact(contact);
					Toast.makeText(AddNewContactActivity.this, "Delete successful!", Toast.LENGTH_SHORT).show();
					AddNewContactActivity.this.finish();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					arg0.cancel();
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		return true;
	}
}