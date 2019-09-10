package com.app.afrifarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.afrifarm.db.AzureCosmosHelper;
import com.app.afrifarm.db.Disease;
import com.app.afrifarm.db.DiseaseDbHelper;
import com.app.afrifarm.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.wisnu.datetimerangepickerandroid.CalendarPickerView;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity  implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    private Disease mDiseases[];
    private RecyclerView mBreakoutListRecyclerView;
    private ArrayList<DiseaseItem> mDiseaseItems;
    private View mDateBtn;
    private TextView mFirstDateTxtView,mLastDateTxtView;
    private Calendar mFistDate,mLastDate;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
        //  FontAwesome.Icon.faw_calendar
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_YEAR, -7);

        Calendar now = Calendar.getInstance();

         mFistDate = nextYear;
         mLastDate = now;

        mBreakoutListRecyclerView = findViewById(R.id.breakoutListRecyclerView);
        mFirstDateTxtView = findViewById(R.id.firstDateTxtView);
        mLastDateTxtView = findViewById(R.id.lastDateTxtView);

        mFirstDateTxtView.setText(mFistDate.get(Calendar.DAY_OF_MONTH)+" "+mFistDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));
        mLastDateTxtView.setText(mLastDate.get(Calendar.DAY_OF_MONTH)+" "+mLastDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));

        mDateBtn = findViewById(R.id.dateBtn);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDateBtn.setOnClickListener(this);
        //runTest();
    }
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getCountryCode()+" - "+obj.getAdminArea()+", "+obj.getSubAdminArea();
          //  add = add + " - " + obj.getCountryName();
          //  add = add + " - " + obj.getCountryCode();
          //  add = add + " - " + obj.getAdminArea();
          //  add = add + " - " + obj.getPostalCode();
           // add = add + " - " + obj.getSubAdminArea();
           // add = add + " - " + obj.getLocality();
           // add = add + " - " + obj.getSubThoroughfare();

           // Log.v("IGA", "Address" + add);
            App.Log("Azure Address "+add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
            return add;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          //  Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    private void runTest(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    Calendar c = Calendar.getInstance();
                    Disease mDisease = new Disease();
                    mDisease.setPlant("Corn");
                    mDisease.setName("Corn Rust");
                    mDisease.setLocationReported(new double[]{212.12,-121});
                    mDisease.setDateReported(c.getTimeInMillis());

                  //  new DiseaseDbHelper().add( mDisease);

                    new DiseaseDbHelper().query(new DiseaseDbHelper.OnAzureActionListener() {
                        @Override
                        public void query(@Nullable List<? extends Disease> results) {

                            App.Log("Azure success query "+new Gson().toJson(results.toArray(new Disease[]{})));
                        }
                    });
                    App.Log("Azure success");
                }
                catch (Exception e){
                    App.Log("Azure failed "+e.getMessage());}
            }
        }).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        queryMap();
    }
    private void loadMarkers(Disease mDiseases[]){
        mDiseaseItems = new ArrayList<DiseaseItem>();
        for(int i=0;i<mDiseases.length;i++){
            Disease mDisease = mDiseases[i];
            if(mDisease.getDateReported()>=mFistDate.getTimeInMillis() &&mDisease.getDateReported()<=mLastDate.getTimeInMillis()){

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDisease.getLocationReported()[0], mDisease.getLocationReported()[1])));
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(mDisease.getLocationReported()[0], mDisease.getLocationReported()[1]))
                        .radius(50).fillColor(
                                Color.parseColor(getColorFromDisease(mDisease).replace("#","#61"))).strokeWidth(0); // In meters
                String address= getAddress(mDisease.getLocationReported()[0], mDisease.getLocationReported()[1]);
// Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);
                if(address!=null){
                    DiseaseItem mDiseaseItem = new DiseaseItem(address,mDisease);
                    if(!mDiseaseItems.contains(mDiseaseItem))
                        mDiseaseItems.add(mDiseaseItem);
                    else
                        mDiseaseItems.get(mDiseaseItems.indexOf(mDiseaseItem)).addDisease(mDisease);

                }
            }
        }



        mBreakoutListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager listManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mBreakoutListRecyclerView.setLayoutManager(listManager);

        mBreakoutListRecyclerView.setAdapter(new ContentAdapter(mDiseaseItems));
    }
    private String[] supportedDiseases = new String[]{"Apple","Blueberry","Cherry (including_sour)"
    ,"Corn (maize)","Grape","Orange","Peach","Pepper, bell","Potato","Raspberry"
            ,"Soybean","Squash","Strawberry","Tomato"};

    private String[] supportedDiseasesColors = new String[]{"#f44336","#e91e63","#9c27b0"
            ,"#673ab7","#2196f3","#00bcd4","#009688","#4caf50","#8bc34a","#cddc39"
            ,"#ffc107","#ff9800","#ff5722","#795548"};
    private String getColorFromDisease(Disease mDisease){
        for(int i=0;i<supportedDiseases.length;i++) {
            String supportedDisease =supportedDiseases[i];
            if(mDisease.getPlant().equals(supportedDisease))
                return supportedDiseasesColors[i];

        }
        return"#607d8b";
    }
    private void queryMap(){

        new DiseaseDbHelper().query(new DiseaseDbHelper.OnAzureActionListener() {
            @Override
            public void query(@Nullable List<? extends Disease> results) {
                mDiseases = results.toArray(new Disease[]{});
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadMarkers(mDiseases);
                    }
                });
            //  App.Log("Azure success query "+new Gson().toJson(results.toArray(new Disease[]{})));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==mDateBtn.getId()){
            showDatePicker();
        }
    }
    private void showDatePicker(){
        final CalendarPickerView mCalendarPickerView = new CalendarPickerView(this,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Date Range");
        builder.setView(mCalendarPickerView);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, -4);

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, -1);
        Calendar next = Calendar.getInstance();


        mCalendarPickerView.init( nextYear.getTime(),next.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE)
        .withSelectedDate(now.getTime());


        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<Date>  mSelectedDates=  mCalendarPickerView.getSelectedDates();

                Calendar now = Calendar.getInstance();
                Calendar next = Calendar.getInstance();
                now.setTime(mSelectedDates.get(0));
                next.setTime( mSelectedDates.get(mSelectedDates.size()-1));
                mFistDate =now ;
                mLastDate =next;

                mFirstDateTxtView.setText(mFistDate.get(Calendar.DAY_OF_MONTH)+" "+mFistDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));
                mFirstDateTxtView.setText(mLastDate.get(Calendar.DAY_OF_MONTH)+" "+mLastDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));


                loadMarkers(mDiseases);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public class DiseaseItem{
        private Disease mDisease;
        private String mLocationName;
        private ArrayList<Disease> mOutBreaks;
        private int mColor;
        public DiseaseItem(String mLocationName,Disease mDisease){
            this.mLocationName = mLocationName;
            this.mDisease =mDisease;
            mOutBreaks = new ArrayList<Disease>();
            mOutBreaks.add(mDisease);
            mColor = Color.parseColor(getColorFromDisease(mDisease));

        }
        public void addDisease(Disease mDisease){

            mOutBreaks.add(mDisease);
        }
        public ArrayList<Disease> getOutBreaks(){
            return mOutBreaks;

        }

        @NonNull
        @Override
        public String toString() {
            return mLocationName+" Name: "+mDisease.getName()+" Plant:"+mDisease.getPlant();
        }

        @Override
        public boolean equals(@androidx.annotation.Nullable Object obj) {
            if(obj!=null && (obj) instanceof DiseaseItem)
            return toString().equals(((DiseaseItem)obj).toString());
            else
                return false;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mPlantNameTxView,
                mDiseaseNameTxView,mOutbreakOutTxView,mLocationtTxView;
        public View item,mBarView,mBgTxView;
        public ViewHolder(View itemView) {
            super(itemView);
            mPlantNameTxView = itemView.findViewById(R.id.plantNameTxView);
            mDiseaseNameTxView = itemView.findViewById(R.id.diseaseNameTxView);
            mOutbreakOutTxView = itemView.findViewById(R.id.outbreakOutTxView);
            mLocationtTxView = itemView.findViewById(R.id.locationtTxView);

            mBarView = itemView.findViewById(R.id.barView);

            mBgTxView = itemView.findViewById(R.id.bgTxView);
            item = itemView;
        }
    }

    private class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<DiseaseItem> mDiseaseItem;
        private static final int HEADER = 0;
        private static final int CONTENT = 1;
        public ContentAdapter(ArrayList<DiseaseItem> mDiseaseItem){
            this.mDiseaseItem = mDiseaseItem;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
            return new ViewHolder(LayoutInflater.from(container.getContext()).inflate(R.layout.diasease_item_layout, container, false));
        }

        @Override
        public int getItemViewType(int position) {
            return CONTENT;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {
            final DiseaseItem mDiseaseItem = mDiseaseItems.get(position);
            holder.mPlantNameTxView.setText(mDiseaseItem.mDisease.getPlant());
            holder.mDiseaseNameTxView.setText(mDiseaseItem.mDisease.getName());
            holder.mOutbreakOutTxView.setText(Integer.toString(mDiseaseItem.mOutBreaks.size()));
            holder.mLocationtTxView.setText(mDiseaseItem.mLocationName);

            Utils.AddGradientBackground(holder.mBgTxView,new int[]{mDiseaseItem.mColor, Color.TRANSPARENT}
            );
            holder.mBarView.setBackgroundColor(mDiseaseItem.mColor);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDiseaseItem.mDisease.getLocationReported()[0], mDiseaseItem.mDisease.getLocationReported()[1])));

                }
            });
        }
        @Override
        public int getItemCount() {
            return mDiseaseItem.size();
        }
    }

}
