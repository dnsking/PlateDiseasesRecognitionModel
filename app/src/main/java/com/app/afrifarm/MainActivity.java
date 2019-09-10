package com.app.afrifarm;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class MainActivity extends AppCompatActivity {

    private final String[] MainPermissions = new String[]{
            Manifest.permission.CAMERA
    , Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private View mGrantBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGrantBtn= findViewById(R.id.grantBtn);
        mGrantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
            }
        });
       if(Nammu.hasPermission(this,MainPermissions)){

           startCameraActivity();
       }

    }
    private void askPermission(){
        Nammu.askForPermission(this, MainPermissions, new PermissionCallback() {

            @Override
            public void permissionGranted() {
                startCameraActivity();
            }

            @Override
            public void permissionRefused() {
            }

        });
    }
    private void startCameraActivity(){
        Intent intent = new Intent(this,CameraActivity.class);
        startActivity(intent);
        finish();
    }
}
