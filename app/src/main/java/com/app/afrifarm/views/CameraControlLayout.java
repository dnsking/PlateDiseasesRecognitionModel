package com.app.afrifarm.views;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.afrifarm.MapActivity;
import com.app.afrifarm.R;
import com.app.afrifarm.db.Disease;
import com.app.afrifarm.networking.AfrifarmNetworkUtils;
import com.app.afrifarm.utils.AnimationUtils;
import com.app.afrifarm.utils.Utils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class CameraControlLayout extends FrameLayout implements View.OnClickListener,  TextureView.SurfaceTextureListener {
    private CameraView mCameraView;
    private View mShutterBtn,mHisBtn,mMapBtn,mReportButton;
    private CircularImageView mCircularImageView;
    private View mCircularImageViewHolder;
    private RecyclerView mImagesRecyclerView;
    private LottieAnimationView mProgressAnimation;
    private TextView mPlantNameTxView,mDiseaseNameTxView,mCancleBtn,mPleaseWaitTxView,mFindingTxView;
    private Button mReportBtnTxView;

    private Handler mainHandler;
    public int Showing;
    public int Normal =0;
    public int Displaying =1;

    private MediaPlayer mMediaPlayer;
    private TextureView mPreview;

    private OnReportDiseaseListener mOnReportDiseaseListener;

    private Disease mDisease;

    public CameraControlLayout(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    public CameraControlLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CameraControlLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==mShutterBtn.getId()){
            openCircularImageView(  mCameraView.getBitmap());
        }
        else if(view.getId()==mMapBtn.getId()){
            Intent intent = new Intent(getContext(), MapActivity.class);
            getContext().startActivity(intent);
        }
        else if(view.getId()==mReportButton.getId()){
            mOnReportDiseaseListener.report(mDisease);
        }
    }
    public interface OnReportDiseaseListener{
        void report(Disease mDisease);
    }
    public void setOnReportDiseaseListener(OnReportDiseaseListener mOnReportDiseaseListener){
        this.mOnReportDiseaseListener = mOnReportDiseaseListener;
    }
    public void pause(){
        mCameraView.pause();
    }

    public void resume(){
        mCameraView.resume();

    }
    private int mCircularImageViewWitdh,mCircularImageViewHeight;
    public void closeCircularImageView(){



        AnimationUtils.createTogetherSet(
                AnimationUtils.getObjectAniOfInt(mCircularImageView,"width",400,new DecelerateInterpolator()
                        ,mCircularImageView.getWidth(),mCameraView.getWidth() ),
                AnimationUtils.getObjectAniOfInt(mCircularImageView,"height",400,new DecelerateInterpolator()
                        ,mCircularImageView.getHeight(), mCameraView.getHeight() ),
                AnimationUtils.getObjectAni(mCircularImageView,"radius",400
                        ,null,0),
                AnimationUtils.getObjectAni(mProgressAnimation,"alpha",400
                        ,null,0),
                AnimationUtils.getObjectAni(mCircularImageView,"translationY",400,new DecelerateInterpolator()
                        ,0 )
                ,
                AnimationUtils.getObjectAni(mCircularImageView,"alpha",300
                        ,null,0),
                AnimationUtils.getObjectAni(mPlantNameTxView,"alpha",300
                        ,null,0),
                AnimationUtils.getObjectAni(mDiseaseNameTxView,"alpha",300
                        ,null,0),
                AnimationUtils.getObjectAni(mReportBtnTxView,"alpha",300
                        ,null,0)
        ).start();

      new  android.os.CountDownTimer(400,400){
          @Override
          public void onTick(long l) {

          }

          @Override
          public void onFinish() {

              mShutterBtn.setVisibility(VISIBLE);
          //    mHisBtn.setVisibility(VISIBLE);
              mMapBtn.setVisibility(VISIBLE);


              mCircularImageViewHolder.setVisibility(GONE);
              mCancleBtn.setVisibility(GONE);
              mPleaseWaitTxView.setVisibility(GONE);
              mFindingTxView.setVisibility(GONE);
              resume();
              Showing = Normal;
          }
      }.start();

    }
    private void openCircularImageView(Bitmap bm){
        mCameraView.pause();
        Showing = Displaying;
       // FontAwesome.Icon.faw_history
     //  Bitmap bm = mCameraView.getBitmap();
        //Bitmap bm = mPreview.getBitmap();
        mShutterBtn.setVisibility(GONE);
        mHisBtn.setVisibility(GONE);
        mMapBtn.setVisibility(GONE);


        mCancleBtn.setVisibility(VISIBLE);
        mPleaseWaitTxView.setVisibility(VISIBLE);
        mFindingTxView.setVisibility(VISIBLE);

        mCircularImageView.setTranslationY(0);
        mCircularImageView.setAlpha(1f);
        mCircularImageViewHolder.setVisibility(VISIBLE);
        mCircularImageView.setImageBitmap(bm);
        mCircularImageView.invalidate();


        AnimationUtils.createTogetherSet(
                AnimationUtils.getObjectAniOfInt(mCircularImageView,"width",700,new DecelerateInterpolator()
                        ,mCameraView.getWidth(),Utils. convertDpToPixelInt(getContext(),134) ),
                AnimationUtils.getObjectAniOfInt(mCircularImageView,"height",700,new DecelerateInterpolator()
                        ,mCameraView.getHeight(), Utils. convertDpToPixelInt(getContext(),134) ),
                AnimationUtils.getObjectAni(mCircularImageView,"radius",300
                        ,null,0,1000),
                AnimationUtils.getObjectAni(mProgressAnimation,"alpha",200
                        ,null,1)
        ).start();

        runModel(bm);
    }
    private void animateResult(Disease mDisease){
      if(mDisease!=null){
          Showing=Displaying;
          this.mDisease = mDisease;
        mCancleBtn.setVisibility(GONE);
        mPleaseWaitTxView.setVisibility(GONE);
        mFindingTxView.setVisibility(GONE);

       mPlantNameTxView.setText(mDisease.getPlant());
        mDiseaseNameTxView.setText(mDisease.getName());

        AnimationUtils.createTogetherSet(
                AnimationUtils.getObjectAniOfInt(mCircularImageView,"width",550,new DecelerateInterpolator()
                        ,mCircularImageView.getWidth(),getWidth()-Utils. convertDpToPixelInt(getContext(),32) ),
                AnimationUtils.getObjectAniOfInt(mCircularImageView,"height",550,new DecelerateInterpolator()
                        ,mCircularImageView.getHeight(), Utils. convertDpToPixelInt(getContext(),364) ),
                AnimationUtils.getObjectAni(mCircularImageView,"radius",500
                        ,null,mCircularImageView.getRadius(),32),
                AnimationUtils.getObjectAni(mProgressAnimation,"alpha",200
                        ,null,0),
                AnimationUtils.getObjectAni(mCircularImageView,"translationY",550,new DecelerateInterpolator()
                        ,-mCircularImageView.getY()+Utils. convertDpToPixelInt(getContext(),96) )
                ,
                AnimationUtils.getObjectAni(mPlantNameTxView,"alpha",300
                        ,null,1),
                AnimationUtils.getObjectAni(mDiseaseNameTxView,"alpha",300
                        ,null,1),
                AnimationUtils.getObjectAni(mReportBtnTxView,"alpha",300
                        ,null,1)

        ).start();

      }
      else{
          Toast.makeText(getContext(),"No results found",Toast.LENGTH_SHORT).show();
          closeCircularImageView();
      }

    }
    private void runModel(final Bitmap bm){
        new Thread(new Runnable() {
            @Override
            public void run() {
              final Disease mDisease = AfrifarmNetworkUtils.RunModel(bm);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        animateResult( mDisease);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);

        try {
           // mMediaPlayer = new MediaPlayer();
            // mMediaPlayer .setDataSource(getContext(), Uri.parse("/storage/emulated/0/DCIM/100ANDRO/MOV_0096.mp4"));
            // mMediaPlayer.setSurface(surface);
            // mMediaPlayer.setLooping(true);

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            // mMediaPlayer.prepareAsync();
            // Play video when the media source is ready for playback.
            /*mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });*/

        } catch (Exception e) {
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public interface OnPictureTakenListener{
        void OnPictureTaken(String path);
    }
    private void init(){
         mainHandler=new Handler(getContext().getMainLooper());

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.camera_control_layout, this, true);

        mPreview = (TextureView) findViewById(R.id.videoView);
      //  mPreview.setSurfaceTextureListener(this);

        mReportButton =findViewById(R.id.reportBtnTxView);
        mProgressAnimation = findViewById(R.id.progressAnimation);
        mCameraView = findViewById(R.id.cameraView);
        mShutterBtn = findViewById(R.id.shutterBtn);
        mHisBtn = findViewById(R.id.hisBtn);
        mMapBtn = findViewById(R.id.mapBtn);
        mCircularImageView = findViewById(R.id.circularImageView);
        mCircularImageViewHolder = findViewById(R.id.circularImageViewHolder);
        mImagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        mPlantNameTxView = findViewById(R.id.plantNameTxView);
        mDiseaseNameTxView = findViewById(R.id.diseaseNameTxView);
        mCancleBtn = findViewById(R.id.cancleBtn);
        mPleaseWaitTxView = findViewById(R.id.pleaseWaitTxView);
        mFindingTxView = findViewById(R.id.findingTxView);
        mReportBtnTxView = findViewById(R.id.reportBtnTxView);

        drawCircle(mShutterBtn, Color.parseColor("#9c27b0"), new ShapeDrawable(new OvalShape()));
        drawCircle(mHisBtn, Color.parseColor("#fafafa"), new ShapeDrawable(new OvalShape()));
        drawCircle(mMapBtn, Color.parseColor("#fafafa"), new ShapeDrawable(new OvalShape()));

        mShutterBtn.setOnClickListener(this);
        mMapBtn.setOnClickListener(this);
        mReportButton.setOnClickListener(this);
        initImageRecyclerView();
    }

    private  void drawCircle(View view, int color, ShapeDrawable circle ){

        circle.clearColorFilter();
        circle.getPaint().setAntiAlias(true);
        circle.getPaint().setColor(color);
            circle.getPaint().setStyle(Paint.Style.FILL);

        view.setBackground(circle);
    }


    private void initImageRecyclerView(){
        ArrayList<ImageItem> mImageItems = new ArrayList<>();
        String[] projection = { "_data", MediaStore.Images.Media._ID

        };

        CursorLoader cLoader = new CursorLoader(getContext(), MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Thumbnails.IMAGE_ID+" DESC");
        Cursor mCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null,MediaStore.Images.Media._ID+" DESC");
        while (mCursor.moveToNext()) {
            mImageItems.add(new ImageItem(mCursor.getString(0),mCursor.getString(1)));
        }
        mCursor.close();
        mImagesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager listManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mImagesRecyclerView.setLayoutManager(listManager);

        mImagesRecyclerView.setAdapter(new ContentAdapter(mImageItems));
    }

    private class ImageItem{
        public String url;
        public String id;
        public ImageItem(String url,String id){
            this.url = url;
            this.id = id;
        }
    }
    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImg;
        public View item;
        public ViewHolder(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.imgview);
            item = itemView;
        }
    }

    private class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<ImageItem> mImageItems;
        private static final int HEADER = 0;
        private static final int CONTENT = 1;
        public ContentAdapter(ArrayList<ImageItem> mImageItems){
            this.mImageItems = mImageItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
           return new ViewHolder(LayoutInflater.from(container.getContext()).inflate(R.layout.img_item_layout, container, false));
        }

        @Override
        public int getItemViewType(int position) {
           return CONTENT;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {

                ImageLoader.getInstance().displayImage("file://"+mImageItems.get(position).url,holder.mImg);
                //  holder.mImg.setImageURI(Uri.parse( mImageItems.get(position).url));
                holder.mImg.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        openCircularImageView(BitmapFactory.decodeFile(mImageItems.get(position).url));
                     //   addImage(mImageItems.get(position-1).url,mImageItems.get(position-1).id);
                    }
                });

        }
        @Override
        public int getItemCount() {
            return mImageItems.size();
        }
    }
}
