package com.example.implementation;

import java.io.FileNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

public class ImageUtils
{
	private static String DEBUG_TAG = "ImageUtils________";
	
	public static Bitmap decodeUri(Context c, Uri uri, int newHeight, int newWidth)
	{
        BitmapFactory.Options o = new BitmapFactory.Options();
        Bitmap bmp;
		try
		{
			bmp = BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);
			int width = bmp.getWidth();
		    int height = bmp.getHeight();
		    
		    Log.i(DEBUG_TAG,String.format("%d %d", width,height));
		    float scaleWidth = ((float) newWidth) / width;
		    float scaleHeight = ((float) newHeight) / height;

		    Matrix matrix = new Matrix();
		    matrix.postScale(scaleWidth, scaleHeight);
		    bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);

		    return bmp;
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}