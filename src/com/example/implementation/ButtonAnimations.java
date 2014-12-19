package com.example.implementation;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class ButtonAnimations
{
	private ImageView imgCenter;
	private ImageView imgCamera;
	private boolean expand = false;
	private int animationMiliSeconds = 500;
	private int cameraDistance = 600;
	
	public ButtonAnimations(ImageView Center, ImageView Camera)
	{
		this.imgCamera = Camera;
		this.imgCenter = Center;
		
		imgCenter.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				AnimationSet as = new AnimationSet(true);
				
				if (!expand)
				{
					imgCamera.setVisibility(View.VISIBLE);
					imgCamera.setClickable(true);
					imgCamera.setX(imgCenter.getX());
					imgCamera.setY(imgCenter.getY());
					
					AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
					alpha.setDuration(animationMiliSeconds);
					as.addAnimation(alpha);
					
					/*
					Animation raCamera = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					raCamera.setDuration(animationMiliSeconds);
					as.addAnimation(raCamera);
					*/
					
					TranslateAnimation taCamera = new TranslateAnimation(-cameraDistance, 0, 0, 0);
					taCamera.setDuration(animationMiliSeconds);
					taCamera.setFillEnabled(true);
					taCamera.setFillAfter(true);
					as.addAnimation(taCamera);
					
					Animation raCenter = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					raCenter.setDuration(animationMiliSeconds);
					
					imgCenter.startAnimation(raCenter);
					imgCamera.startAnimation(as);
					
					taCamera.setAnimationListener(new AnimationListener()
					{
						@Override
						public void onAnimationStart(Animation arg0) 
						{
							imgCamera.setVisibility(View.GONE);
							imgCamera.setX(imgCenter.getX() + cameraDistance);
							imgCamera.setY(imgCenter.getY());
						}
						
						@Override
						public void onAnimationRepeat(Animation arg0)
						{
						}
						
						@Override
						public void onAnimationEnd(Animation arg0) 
						{
							//imgCamera.clearAnimation();
							imgCamera.setVisibility(View.VISIBLE);
						}
					});
				}
				else
				{
					AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
					alpha.setDuration(animationMiliSeconds);
					as.addAnimation(alpha);
					TranslateAnimation taCamera = new TranslateAnimation(0, -cameraDistance, 0, 0);
					taCamera.setDuration(animationMiliSeconds);
					taCamera.setFillEnabled(true);
					taCamera.setFillAfter(true);
					as.addAnimation(taCamera);
					Animation raCenter = new RotateAnimation(0, -360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					raCenter.setDuration(animationMiliSeconds);
					
					imgCenter.startAnimation(raCenter);
					imgCamera.startAnimation(as);
					
					taCamera.setAnimationListener(new AnimationListener()
					{
						@Override
						public void onAnimationStart(Animation arg0) 
						{
						}
						
						@Override
						public void onAnimationRepeat(Animation arg0)
						{
						}
						
						@Override
						public void onAnimationEnd(Animation arg0) 
						{
							imgCamera.setX(imgCenter.getX());
							imgCamera.setY(imgCenter.getY());
							imgCamera.setVisibility(View.INVISIBLE);
							imgCamera.setClickable(false);
							imgCamera.clearAnimation();
						}
					});
				}
				expand = !expand;
			}
		});
	}
}