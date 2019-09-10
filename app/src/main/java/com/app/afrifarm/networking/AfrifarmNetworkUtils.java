package com.app.afrifarm.networking;

import android.graphics.Bitmap;
import android.util.Base64;

import com.app.afrifarm.App;
import com.app.afrifarm.db.Disease;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AfrifarmNetworkUtils {
    public static Disease RunModel(Bitmap img){
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG,100,out);
            byte[] data= Base64.encode(out.toByteArray(),Base64.DEFAULT);
            OkHttpClient client =  new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    // .addHeader("Authorization",token)
                    .url(App.ModelURL).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data))
                    .build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();

            App.Log("RunModel response "+result);

            ModelResult mModelResult = new Gson().fromJson(result,ModelResult.class);
            if(Double.parseDouble(mModelResult.getProbability())>0.7&&!mModelResult.getLabel().contains("healthy")){
                Calendar c = Calendar.getInstance();

                String[] plateDisease = mModelResult.getLabel().split("___");

                Disease mDisease = new Disease();
                mDisease.setName(plateDisease[1].replace("_"," "));
                mDisease.setPlant(plateDisease[0].replace("_"," "));
                mDisease.setDateReported(c.getTimeInMillis());

                return mDisease;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            App.Log("RunModel response failed ");}

        return null;

    }
}
