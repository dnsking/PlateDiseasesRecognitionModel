package com.app.afrifarm;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.azure.data.AzureData;
import com.azure.data.model.PermissionMode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class App extends Application {
    public static boolean IsDebug = true;

    private static final String TAG = "Afrifarm";

    public static String ModelURL = "";
    public static void Log(String msg){
        if(App.IsDebug){
            int maxLogSize = 1000;
            if(msg.length()>maxLogSize)
                for(int i = 0; i <= msg.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > msg.length() ? msg.length() : end;
                    Log.i(TAG, msg.substring(start, end));
                }
            else
                Log.i(TAG, msg);
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

        super.onCreate();



        DisplayImageOptions defaultOptions =  new DisplayImageOptions.Builder()

                .imageScaleType(ImageScaleType.EXACTLY).resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheSizePercentage(10).threadPriority(1).denyCacheImageMultipleSizesInMemory()
                .memoryCacheExtraOptions(64, 64)
                .memoryCacheSizePercentage(3).tasksProcessingOrder(QueueProcessingType.LIFO).denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

    }
}
