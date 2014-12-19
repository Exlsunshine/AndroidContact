package com.zijunlin.Zxing.Demo;

import java.io.IOException;
import java.util.Vector;
import cn.domob.android.ads.DomobAdView;

import com.example.activity.AddNewContactActivity;
import com.example.activity.MainActivity;
import com.example.contact.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zijunlin.Zxing.Demo.camera.CameraManager;
import com.zijunlin.Zxing.Demo.decoding.CaptureActivityHandler;
import com.zijunlin.Zxing.Demo.decoding.InactivityTimer;
import com.zijunlin.Zxing.Demo.view.ViewfinderView;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

public class CaptureActivity extends Activity implements Callback 
{
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;//
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	

	RelativeLayout mAdContainer;
	DomobAdView mAdview320x50;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner);//����scanner.xml����
		//��ʼ�� CameraManager
		CameraManager.init(getApplication());//����CameraManager���� 
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);//����ǰ��Ľ��̣�����ȷ����ǰ������300s�����
	}
	

	@Override
	/**�����ر�activityʱ���˷����ͱ�ϵͳ����*/
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();//ͨ��SurfaceView�����ʵ����SurfaceHolder���� 
		if (hasSurface) {//��һ�μ��ر�activityʱ��hasSurface��Ϊfalse��
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		//characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);//��ȡϵͳ����Ƶ������
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {//����ֻ�������ģʽ�����������ֻ�����  ��������Ϊ���� ���񶯣�
			playBeep = false;//�����initBeepSound�����������ᱻִ����
		}
		initBeepSound();//����ֻ�����ģʽ������ģʽ����ô
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * ���ܣ�������ͷ�����Ӳ�����������û��ɨ��Ĵ��������ô������½�һ�������ｫ�ַ�����Ϊ��GBK
	 * */

	private void initCamera(SurfaceHolder surfaceHolder) {
		//SurfaceHolder��  ����������֮������SurfaceView�ϻ�ͼ������SurfaceView.getHolder()����ʼ��SurfaceHolder����ͨ��SurfaceHolder��SurfaceView�ϻ�ͼ
		try {
			CameraManager.get().openDriver(surfaceHolder);//���CameraManager����ʵ���󣬴�����ͷ�������ҳ�ʼ��Ӳ������   surfaceHolder�������Ԥ��֡����ĵط�
			
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					"GBK");
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode)
	{
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();	
		playBeepSoundAndVibrate();	
		
		Intent intent1=new Intent();
		intent1.putExtra("info",obj.getText().toString());
		
		intent1.setClass(CaptureActivity.this,AddNewContactActivity.class);
		startActivity(intent1);
		finish();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	 
	/*
	public void showbaner()
	 {
		 mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
			// Create ad view
			mAdview320x50 = new DomobAdView(this,contact.PUBLISHER_ID,contact.InlinePPID, DomobAdView.INLINE_SIZE_320X50);
			mAdview320x50.setKeyword("game");
			mAdview320x50.setUserGender("male");
			mAdview320x50.setUserBirthdayStr("2000-08-08");
			mAdview320x50.setUserPostcode("123456");

			mAdview320x50.setAdEventListener(new DomobAdEventListener() {
							
				public void onDomobAdReturned(DomobAdView adView) {
					Log.i("DomobSDKDemo", "onDomobAdReturned");				
				}

				public void onDomobAdOverlayPresented(DomobAdView adView) {
					Log.i("DomobSDKDemo", "overlayPresented");
				}

				public void onDomobAdOverlayDismissed(DomobAdView adView) {
					Log.i("DomobSDKDemo", "Overrided be dismissed");				
				}

				public void onDomobAdClicked(DomobAdView arg0) {
					Log.i("DomobSDKDemo", "onDomobAdClicked");				
				}

				public void onDomobAdFailed(DomobAdView arg0, ErrorCode arg1) {
					Log.i("DomobSDKDemo", "onDomobAdFailed");				
				}

				public void onDomobLeaveApplication(DomobAdView arg0) {
					Log.i("DomobSDKDemo", "onDomobLeaveApplication");				
				}

				public Context onDomobAdRequiresCurrentContext() {
					return CaptureActivity.this;
				}
			});
			
			mAdContainer.addView(mAdview320x50);
	 }*/
}