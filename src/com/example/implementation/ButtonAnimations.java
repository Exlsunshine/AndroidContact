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
	private ImageView imgGallery;
	private boolean expand = false;
	private int animationMiliSeconds = 450;
	private int cameraDistance = 700;
	private int galleryDistance = 400;
	
	private AnimationSet setCameraAnimation(final ImageView img, final int distance)
	{
		AnimationSet as = new AnimationSet(true);
		if (!expand)
		{
			img.setVisibility(View.VISIBLE);
			img.setClickable(true);
			img.setX(imgCenter.getX());
			img.setY(imgCenter.getY());
			
			AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
			alpha.setDuration(animationMiliSeconds);
			as.addAnimation(alpha);
			
			TranslateAnimation taCamera = new TranslateAnimation(-distance, 0, 0, 0);
			taCamera.setDuration(animationMiliSeconds);
			taCamera.setFillEnabled(true);
			taCamera.setFillAfter(true);
			as.addAnimation(taCamera);
			
			taCamera.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation arg0) 
				{
					img.setVisibility(View.GONE);
					img.setX(imgCenter.getX() + distance);
					img.setY(imgCenter.getY());
				}
				
				@Override
				public void onAnimationRepeat(Animation arg0)
				{
				}
				
				@Override
				public void onAnimationEnd(Animation arg0) 
				{
					img.setVisibility(View.VISIBLE);
				}
			});
		}
		else
		{
			AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
			alpha.setDuration(animationMiliSeconds);
			as.addAnimation(alpha);
			TranslateAnimation taCamera = new TranslateAnimation(0, -distance, 0, 0);
			taCamera.setDuration(animationMiliSeconds);
			taCamera.setFillEnabled(true);
			taCamera.setFillAfter(true);
			as.addAnimation(taCamera);
			
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
					img.setX(imgCenter.getX());
					img.setY(imgCenter.getY());
					img.setVisibility(View.INVISIBLE);
					img.setClickable(false);
					img.clearAnimation();
				}
			});
		}
		
		return as;
	}
	
	public ButtonAnimations(ImageView center, ImageView camera, ImageView gallery)
	{
		this.imgCamera = camera;
		this.imgCenter = center;
		this.imgGallery = gallery;
		
		imgCenter.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (!expand)
				{
					Animation raCenter = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					raCenter.setDuration(animationMiliSeconds);
					imgCenter.startAnimation(raCenter);
					imgCamera.startAnimation(setCameraAnimation(imgCamera, cameraDistance));
					imgGallery.startAnimation(setCameraAnimation(imgGallery, galleryDistance));
				}
				else
				{
					Animation raCenter = new RotateAnimation(0, -360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					raCenter.setDuration(animationMiliSeconds);
					imgCenter.startAnimation(raCenter);
					imgCamera.startAnimation(setCameraAnimation(imgCamera, cameraDistance));
					imgGallery.startAnimation(setCameraAnimation(imgGallery, galleryDistance));
				}
				expand = !expand;
			}
		});
	}
}