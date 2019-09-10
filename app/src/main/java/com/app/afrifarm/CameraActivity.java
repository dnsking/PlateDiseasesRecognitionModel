package com.app.afrifarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.afrifarm.db.Disease;
import com.app.afrifarm.db.DiseaseDbHelper;
import com.app.afrifarm.views.CameraControlLayout;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import java.util.Calendar;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class CameraActivity extends AppCompatActivity implements CameraControlLayout.OnReportDiseaseListener {

    private CameraControlLayout mCameraControlLayout;
    private final String[] MainPermissions = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if(mCameraControlLayout.Showing==mCameraControlLayout.Displaying)
            mCameraControlLayout.closeCircularImageView();
        else
            finish();
     //   super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCameraControlLayout = findViewById(R.id.cameraControlLayout);
        mCameraControlLayout.setOnReportDiseaseListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mCameraControlLayout.pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCameraControlLayout.resume();
    }

    @Override
    public void report(final Disease mDisease) {

        Nammu.askForPermission(this, MainPermissions, new PermissionCallback() {

            @Override
            public void permissionGranted() {
                // TODO Auto-generated method stub
                sendReport(mDisease);

            }

            @Override
            public void permissionRefused() {
                Toast.makeText(CameraActivity.this,"Location Permission Required",Toast.LENGTH_SHORT);
            }

        });
    }

    private void completeSending(ProgressDialog dialog ){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                Intent intent = new Intent(CameraActivity.this, MapActivity.class);
               startActivity(intent);
            }
        });
    }
    private void sendReport(final Disease mDisease){

        Calendar c = Calendar.getInstance();
        mDisease.setDateReported(c.getTimeInMillis());
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Report");
        dialog.setCancelable(false);
        dialog.show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(loc!=null){

            double lon = loc.getLongitude();
            double lat = loc.getLatitude();


             lon = 28.2235839;
             lat = -15.5890855;
            mDisease.setLocationReported(new double[]{lat,lon});
            new Thread(new Runnable() {
                @Override
                public void run() {

                    new DiseaseDbHelper().add( mDisease);
                    completeSending( dialog );
                }
            }).start();
        }
        else{
            LocationListener myLocationListener = new LocationListener() {
                public void onLocationChanged(Location loc) {

                    double lon = loc.getLongitude();
                    double lat = loc.getLatitude();


                    lon = 28.2235839;
                    lat = -15.5890855;
                    mDisease.setLocationReported(new double[]{lat,lon});
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            new DiseaseDbHelper().add( mDisease);
                            completeSending( dialog );
                        }
                    }).start();
                }
                public void onProviderDisabled(String provider){
                }
                public void onProviderEnabled(String provider){}
                public void onStatusChanged(String provider, int status,
                                            Bundle extras){
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5,
                    myLocationListener);}

    }
}
