package com.example.implementation;

import com.example.activity.MainActivity;
import com.example.contact.R;
import com.example.contact.R.layout;
import com.example.contact.R.menu;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashScreen extends Activity
{
	private static int[] SPLASH_TIME = {600,1000,400};
	private ImageView splashImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// set to full screen.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);
		
		splashImg = (ImageView)findViewById(R.id.splashImg);
		
		// set background color to white.
		View view = this.getWindow().getDecorView();
	    view.setBackgroundColor(Color.parseColor("#ffffff"));
	    
	    // jump to mainActivity after the animation.
	    loadingAnimation();
	}
	
	private void loadingAnimation()
	{
		new Handler().postDelayed(new Runnable()
	    {
			@Override
			public void run()
			{
				splashImg.setImageResource(R.drawable.splash2);
				new Handler().postDelayed(new Runnable()
			    {
					@Override
					public void run()
					{
						splashImg.setImageResource(R.drawable.splash);
						new Handler().postDelayed(new Runnable()
					    {
							@Override
							public void run()
							{
								Intent intent = new Intent(SplashScreen.this, MainActivity.class);
								startActivity(intent);
								SplashScreen.this.finish();
							}
						}, SPLASH_TIME[2]);
					}
				}, SPLASH_TIME[1]);
			}
		}, SPLASH_TIME[0]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}
}