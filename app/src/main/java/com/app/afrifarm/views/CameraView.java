package com.app.afrifarm.views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView  implements SurfaceHolder.Callback, PreviewCallback {


    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;
    private int mDisplayOrientation;

    private static final int CAMERA_ID = 0;
    public CameraView(Context context, AttributeSet attrs,
                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context);
        // TODO Auto-generated constructor stub
    }
    public CameraView(Context context, AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurface();
        init(context);
        // TODO Auto-generated constructor stub
    }
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurface();
        init(context);
        // TODO Auto-generated constructor stub
    }
    public CameraView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initSurface();
        init(context);
    }
    private void initSurface(){
        mHolder = getHolder();
        mHolder.addCallback(this);}
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
         //   Log.d(TAG, "Camera " + cameraId + " is not available: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }
    private void init(Context context) {
        // Do not initialise if no camera has been set
        mCamera = getCameraInstance(CAMERA_ID);

        mCameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(CAMERA_ID, mCameraInfo);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(parameters);
        mCamera.autoFocus(new AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {

            }
        });
        //mDisplayOrientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        mDisplayOrientation = 0;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.

    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            Log.d(TAG, "Camera preview started.");
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
      //  mCamera.stopPreview();
      //  mCamera.release();
    }
    public void pause(){
        if(mCamera!=null){
            mCamera.setPreviewCallback(null);
            getHolder().removeCallback(this);
            mCamera.release();
        //    mCamera.release();
        }
    }

    public void resume(){
        initSurface();
        init(getContext());

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            Log.d(TAG, "Preview surface does not exist");
            return;
        }

        // stop preview before making changes
        try {
       //     mCamera.stopPreview();
            Log.d(TAG, "Preview stopped.");
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

        int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);
        mCamera.setDisplayOrientation(orientation);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
            Log.d(TAG, "Camera preview started.");
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * Calculate the correct orientation for a {@link Camera} preview that is displayed on screen.
     *
     * Implementation is based on the sample code provided in
     * {@link Camera#setDisplayOrientation(int)}.
     */
    public static int calculatePreviewOrientation(Camera.CameraInfo info, int rotation) {
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }
    private byte[] data;
    @Override
    public void onPreviewFrame(byte[] data, Camera cam) {
        this. data = data;
    }
    public Bitmap getBitmap(){

        Size previewSize = mCamera.getParameters().getPreviewSize();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] rawImage = null;
        YuvImage yuv = new YuvImage(data,ImageFormat.NV21,previewSize.width,previewSize.height,null);
        yuv.compressToJpeg(new Rect(0,0,previewSize.width,previewSize.height), 100, baos);
        rawImage = baos.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
        ByteArrayOutputStream rotatedStream = new ByteArrayOutputStream();
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,previewSize.width,previewSize.height,matrix,false);
        bitmap.compress(CompressFormat.JPEG, 100, rotatedStream);
        return bitmap;
    }



}
