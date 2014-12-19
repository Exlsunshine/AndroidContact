package com.example.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.contact.R;
import com.example.contact.R.drawable;
import com.example.contact.R.id;
import com.example.contact.R.layout;
import com.example.contact.R.menu;
import com.example.implementation.ButtonAnimations;
import com.example.implementation.Contact;
import com.example.implementation.DatabaseHandler;
import com.example.implementation.ImageUtils;

public class AddNewContactActivity extends Activity
{
	private Button cancel;
	private Button done;
	private Button addPhone;
	private Button addOthers;
	private ImageView imageCenter;
	private ImageView imageCamera;
	private ImageView portrait;
	
	private String [] phoneType = {"Mobile","Home","Work"};
	private String [] othersType = {"E-mails","Home addres","Nick name"};
	//private String [] groupType = {"College","Family","Friends","ICE"};
	private int cnt = 0;
	private int othersId = 0;
	private boolean hasInit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_contact);
		
		cancel = (Button)findViewById(R.id.cancel);
		done = (Button)findViewById(R.id.done);
		addPhone = (Button)findViewById(R.id.add_phone);
		addOthers = (Button)findViewById(R.id.add_others);
		imageCenter = (ImageView)findViewById(R.id.imgCenter);
		imageCamera = (ImageView)findViewById(R.id.imgCamera);
		portrait = (ImageView)findViewById(R.id.portrait);
		
		imageCamera.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				selectFromMedia();
			}
		});
		
		imageCenter.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (hasInit)
					return ;
				else
				{
					hasInit = true;
					ButtonAnimations btn = new ButtonAnimations(imageCenter,imageCamera);
				}
			}
		});
		
		addOthers.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				LinearLayout addOthersOuterLayout = (LinearLayout)findViewById(R.id.add_others_outter_layout);
				
				LinearLayout layout = new LinearLayout(AddNewContactActivity.this);
				layout.setId(othersId++);
				layout.setOrientation(LinearLayout.HORIZONTAL);
								
				Spinner sp = new Spinner(AddNewContactActivity.this);
				sp.setId(110);
				ArrayAdapter<String> arrAdp = new ArrayAdapter<String>(AddNewContactActivity.this, android.R.layout.simple_spinner_item, othersType);
				sp.setAdapter(arrAdp);
				sp.setOnItemSelectedListener(new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3)
					{
						int index = arg0.getSelectedItemPosition();
						Toast.makeText(AddNewContactActivity.this, othersType[index], Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0)
					{
						Toast.makeText(AddNewContactActivity.this,"Nothing", Toast.LENGTH_SHORT).show();
					}
				});
				
				EditText et = new EditText(AddNewContactActivity.this);
				et.setId(10);
				et.setText("add number here.");
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
		});
		
		done.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("firstName", ((EditText)findViewById(R.id.first_name)).getText().toString());
				map.put("lastName", ((EditText)findViewById(R.id.last_name)).getText().toString());
				map.put("company", ((EditText)findViewById(R.id.company)).getText().toString());
				
				LinearLayout addPhoneOutterLayout = (LinearLayout)findViewById(R.id.add_phone_outter_layout);
				for (int i = 1; i < addPhoneOutterLayout.getChildCount(); i++)
				{
					View view = addPhoneOutterLayout.getChildAt(i);
					Spinner sp = (Spinner)view.findViewById(110);
					EditText et = (EditText)view.findViewById(10);
					map.put(phoneType[sp.getSelectedItemPosition()], et.getText().toString());
				}
				
				LinearLayout addOthersOutterLayout = (LinearLayout)findViewById(R.id.add_others_outter_layout);
				for (int i = 1; i < addOthersOutterLayout.getChildCount(); i++)
				{
					View view = addOthersOutterLayout.getChildAt(i);
					Spinner sp = (Spinner)view.findViewById(110);
					EditText et = (EditText)view.findViewById(10);
					map.put(othersType[sp.getSelectedItemPosition()], et.getText().toString());
				}
				
				map.put("portrait", portrait.getBackground());
				
				saveToDatabase(map);
				
				Toast.makeText(AddNewContactActivity.this, "done", Toast.LENGTH_LONG).show();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent it = new Intent(AddNewContactActivity.this, MainActivity.class);
				startActivity(it);
				AddNewContactActivity.this.finish();
			}
		});
		
		addPhone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				LinearLayout addPhoneOutterLayout = (LinearLayout)findViewById(R.id.add_phone_outter_layout);
				LinearLayout layout = new LinearLayout(AddNewContactActivity.this);
				layout.setId(cnt++);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				
				Spinner sp = new Spinner(AddNewContactActivity.this);
				sp.setId(110);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddNewContactActivity.this, android.R.layout.simple_spinner_item, phoneType);
				sp.setAdapter(adapter);
				sp.setOnItemSelectedListener(new OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3)
					{
						int index = arg0.getSelectedItemPosition();
						Toast.makeText(AddNewContactActivity.this, phoneType[index], Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0)
					{
						Toast.makeText(AddNewContactActivity.this,"Nothing", Toast.LENGTH_SHORT).show();
					}
				});
				
				EditText et = new EditText(AddNewContactActivity.this);
				et.setText("add number here.");
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
		});
	}
	
	private int saveToDatabase(Map<String,Object> map)
	{
		DatabaseHandler db = new DatabaseHandler(this);
		Contact contact = new Contact((Drawable)map.get("portrait"), (String)map.get("firstName"), (String)map.get("lastName"), (String)map.get("company"), (String)map.get("Mobile"), (String)map.get("Home"), (String)map.get("Work"), (String)map.get("E-mails"), (String)map.get("Home addres"), (String)map.get("Nick name"));
		db.addContact(contact);
		
		return 0;
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
	
	private void selectFromMedia()
	{
		Intent pickPhoto = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto , 1);
	}
	
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
					portrait.setBackground(new BitmapDrawable(bmp));
			    }
			    break; 
			case 1:
			    if(resultCode == RESULT_OK)
			    {  
			    	Log.i("onActivityResult_________", "pickPhoto");
			        Uri selectedImage = imageReturnedIntent.getData();
					Bitmap bmp = ImageUtils.decodeUri(this,selectedImage,100,100);
					portrait.setBackground(new BitmapDrawable(bmp));
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
		return true;
	}
}