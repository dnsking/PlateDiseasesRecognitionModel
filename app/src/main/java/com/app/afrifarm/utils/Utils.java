package com.app.afrifarm.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Utils {
	public static long minsTomillis(long mins){
		return mins*60*1000;
	}
	public static long millisTomins(long millis){
		return millis/(1000*60);
	}
	public static long secsTomillis(long secs){
		return secs*1000;
	}
	public static int convertDpToPixelInt(Context context, float dp) {

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int px = (int) (dp * (metrics.densityDpi / 160f));
		return px;
	}
	public static boolean hasLollipop(){
		return Build.VERSION.SDK_INT>=21;
	}
	public static void setStatusBarColor(final Activity target,int color){
		if(hasLollipop()){
				target.getWindow().setStatusBarColor(color);
		}

	}
	public static float spToPixels(Context context,float sp){
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float px = sp * (metrics.scaledDensity );
		return px;
	}
	
	public static float convertDpToPixel(Context context, float dp) {

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float px = (float) (dp * (metrics.densityDpi / 160f));
		return px;
	}

	public static void saveBytes(byte[] bytes, String destinationFile) throws IOException {
		OutputStream os = new FileOutputStream(destinationFile);
		os.write(bytes, 0, bytes.length);
		os.close();
	}
	public static void SaveBitmap(Bitmap bm,String path) throws IOException {
        OutputStream os = new FileOutputStream(path);
        bm.compress(Bitmap.CompressFormat.JPEG,100,os);

        os.close();
    }
    public static int KeepNumbers(String str){
		return Integer.parseInt(str.replaceAll("[^\\d.]", ""));
	}

    public static String getCacheThumPath(Context activity){
        File parent = new File(activity.getExternalCacheDir()+"/"+"thumbs");
        if(!parent.exists()){
            parent.mkdirs();
        }
        return parent.getAbsolutePath();
    }

    public static String getTempPath(Context activity){
        File parent = new File(activity.getExternalCacheDir()+"/"+"temp");
        if(!parent.exists()){
            parent.mkdirs();
        }
        return parent.getAbsolutePath();
    }

	public static String getCachePath(Context activity){
		File parent = activity.getExternalCacheDir();
		if(!parent.exists()){
			parent.mkdirs();
		}
		return parent.getAbsolutePath();
	}
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
	public static void AddGradientBackground(View layout, int... colors){
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,colors);
		gd.setCornerRadius(0);
	//	gd.setGradientType(GradientDrawable.SWEEP_GRADIENT);
		layout.setBackground(gd);
	}
}
