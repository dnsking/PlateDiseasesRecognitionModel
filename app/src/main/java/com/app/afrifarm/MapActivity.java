package com.app.afrifarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.app.afrifarm.db.Disease;
import com.app.afrifarm.db.DiseaseDbHelper;
import com.app.afrifarm.db.FetchAll;
import com.app.afrifarm.networking.AfrifarmNetworkUtils;
import com.app.afrifarm.utils.Utils;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.wisnu.datetimerangepickerandroid.CalendarPickerView;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity  implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    private Disease mDiseases[];
    private RecyclerView mBreakoutListRecyclerView;
    private ArrayList<DiseaseItem> mDiseaseItems;
    private View mDateBtn;
    private TextView mFirstDateTxtView,mLastDateTxtView;
    private Calendar mFistDate,mLastDate;
    private com.esri.arcgisruntime.mapping.view.MapView mapView;
    private Button dateRangeButton;
    private  MaterialSearchBar searchBar;
    private ChipGroup chipGroup;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
        //  FontAwesome.Icon.faw_calendar
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // edited here
            getWindow().setStatusBarColor(Color.parseColor("#212121"));
        }
        setContentView(R.layout.activity_map);
        chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                if(i==0){
                    loadAllPoints();
                }
                else
                selectPlant(i-2);
            }
        });

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, -1);

        Calendar now = Calendar.getInstance();

         mFistDate = nextYear;
         mLastDate = now;

        mBreakoutListRecyclerView = findViewById(R.id.breakoutListRecyclerView);
        mFirstDateTxtView = findViewById(R.id.firstDateTxtView);
        mLastDateTxtView = findViewById(R.id.lastDateTxtView);
        dateRangeButton = findViewById(R.id.dateRangeButton);

        mFirstDateTxtView.setText(mFistDate.get(Calendar.DAY_OF_MONTH)+" "+mFistDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));
        mLastDateTxtView.setText(mLastDate.get(Calendar.DAY_OF_MONTH)+" "+mLastDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));

        dateRangeButton.setText(mFistDate.get(Calendar.DAY_OF_MONTH)+" "+mFistDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault())
        +" - "+mLastDate.get(Calendar.DAY_OF_MONTH)+" "+mLastDate.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.getDefault()));

        dateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker dateRangePicker =
                        MaterialDatePicker.Builder.dateRangePicker()
                                .setTitleText("Select Dates")
                                .setSelection(
                                     new Pair(
                                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                                MaterialDatePicker.todayInUtcMilliseconds()
                                        )
                                )
                                .build();

                dateRangePicker.show(getSupportFragmentManager(),"t");
            }
        });

        mDateBtn = findViewById(R.id.dateBtn);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDateBtn.setOnClickListener(this);

        mapView = findViewById(R.id.mapView);
         searchBar = findViewById(R.id.searchBar);
        searchBar.setTextHintColor(Color.parseColor("#424242"));
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        initMaps();

        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mapView) {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                android.graphics.Point clickedLocation = new android.graphics.Point(Math.round(motionEvent.getX()),
                        Math.round(motionEvent.getY()));
                Point originalPoint = mMapView.screenToLocation(clickedLocation);
                Point projectedPoint = (Point) GeometryEngine.project(originalPoint, SpatialReferences.getWgs84());

                for (Disease disease :mDiseases) {

                    String[] latlonStr = disease.getLocation().split(",");
                    com.esri.arcgisruntime.geometry.Point point =
                            new com.esri.arcgisruntime.geometry.Point(Double.parseDouble(latlonStr[1]),Double.parseDouble(latlonStr[0]), SpatialReferences.getWgs84());


                    Location loc1 = new Location("");
                    loc1.setLatitude(projectedPoint.getY());
                    loc1.setLongitude(projectedPoint.getX());

                    Location loc2 = new Location("");
                    loc2.setLatitude(point.getY());
                    loc2.setLongitude(point.getX());

                    float distanceInMeters = loc1.distanceTo(loc2);
                    App.Log("setOnTouchListener projectedPoint "+projectedPoint+" point "+point+" distanceInMeters "+distanceInMeters);


                if(point.equals(projectedPoint)||distanceInMeters<5000){
                    showDialog(disease);
                }
                }

                return true;
            }
        });

        //runTest();
    }
    private void showDialog(Disease mDe){
        new MaterialAlertDialogBuilder(this).setTitle(mDe.getPlant())
                .setMessage(mDe.getName()).setNegativeButton("Call Farmer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("Watch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
        .show();
    }
    private void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Gson gson = new Gson();
                    String data =  AfrifarmNetworkUtils.RunOnAws(new FetchAll());
                    App.Log("data "+data);
                    mDiseases=new Gson().fromJson(data,Disease[].class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadAllPoints();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }
    private void selectPlant(int index){
        App.Log("selectPlant "+index);
        List<Disease> mDiseasesSelected = new ArrayList<>();
        for (Disease disease :mDiseases){
            if(disease.getPlant().equals(
                    supportedDiseases[index])||
                    supportedDiseases[index].startsWith(
                            disease.getPlant())
            ){
                mDiseasesSelected.add(disease);
            }
        }
        loadSelectedPoints(mDiseasesSelected);
    }
    private void loadSelectedPoints(List<Disease> mDiseases){
        mapView.getGraphicsOverlays().remove(0);
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        for (Disease disease :mDiseases){

            String[] latlonStr = disease.getLocation().split(",");
            com.esri.arcgisruntime.geometry.Point point =
                    new com.esri.arcgisruntime.geometry.Point(Double.parseDouble(latlonStr[1]), Double.parseDouble(latlonStr[0]), SpatialReferences.getWgs84());

            SimpleMarkerSymbol simpleMarkerSymbol =new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
                    Color.parseColor(getColorFromDisease(disease)), 30f);


            SimpleLineSymbol blueOutlineSymbol =new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#212121"), 2f);
            simpleMarkerSymbol.setOutline(blueOutlineSymbol);

            com.esri.arcgisruntime.mapping.view.Graphic pointGraphic =
                    new com.esri.arcgisruntime.mapping.view.Graphic(point, simpleMarkerSymbol);

            // add the point graphic to the graphics overlay
            graphicsOverlay.getGraphics().add(pointGraphic);
        }
    }
    private void loadAllPoints(){
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        for (Disease disease :mDiseases){

            String[] latlonStr = disease.getLocation().split(",");
            com.esri.arcgisruntime.geometry.Point point =
                    new com.esri.arcgisruntime.geometry.Point(Double.parseDouble(latlonStr[1]), Double.parseDouble(latlonStr[0]), SpatialReferences.getWgs84());

            SimpleMarkerSymbol simpleMarkerSymbol =new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
                    Color.parseColor(getColorFromDisease(disease)), 30f);


            SimpleLineSymbol blueOutlineSymbol =new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#212121"), 2f);
            simpleMarkerSymbol.setOutline(blueOutlineSymbol);

            com.esri.arcgisruntime.mapping.view.Graphic pointGraphic =
                    new com.esri.arcgisruntime.mapping.view.Graphic(point, simpleMarkerSymbol);

            // add the point graphic to the graphics overlay
            graphicsOverlay.getGraphics().add(pointGraphic);
        }
    }
    private   List<GeocodeResult> locations;
    private void initMaps(){
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY);

        // set the map to be displayed in the layout's MapView
        mapView.setMap(map);
        // set the viewpoint, Viewpoint(latitude, longitude, scale)
        mapView.setViewpoint(new Viewpoint(
                -15.364802, 28.339154, CountryScale));

        LocatorTask locatorTask =
             new LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer");
        // or set an API Key on the Locator Task with locatorTask.setApiKey("YOUR_API_KEY")

        //addPoints(new double[][]{new double[]{-15.364802, 28.339154}});
        searchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchConfirmed(CharSequence text) {
                super.onSearchConfirmed(text);
            }
        });
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(!charSequence.toString().isEmpty()){

                        ListenableFuture<List<GeocodeResult>>  result =locatorTask.geocodeAsync(charSequence.toString());
                        result.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    locations =  result.get();
                                    List<String> locString = new ArrayList<>();

                                    for(int i =0;i<locations.size();i++){
                                        if(!locString.contains(locations.get(i).getLabel())){

                                            App.Log("Suggestions for "+charSequence+" is "+locations.get(i).getLabel());
                                            locString.add(locations.get(i).getLabel());
                                        }

                                    }

                                    searchBar.updateLastSuggestions(locString);
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int i, View view) {
                searchBar.closeSearch();
                loadPoint(
                        locations.get(i));
            }

            @Override
            public void OnItemDeleteListener(int i, View view) {

            }
        });

        init();
    }
    private void loadPoint(GeocodeResult point){

        mapView.setViewpoint(new Viewpoint(  point.getDisplayLocation(), DistrictScale));
    }
    private double DistrictScale =90000*20;
    private double CountryScale =DistrictScale*20;
    private double ContinentScale =CountryScale*5;
    private void addPoints(double[][] points){
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        for (double[] pointValue :points){

            com.esri.arcgisruntime.geometry.Point point =
                    new com.esri.arcgisruntime.geometry.Point(pointValue[1], pointValue[0], SpatialReferences.getWgs84());

            SimpleMarkerSymbol simpleMarkerSymbol =new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLACK, 30f);

            SimpleLineSymbol blueOutlineSymbol =new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f);
            simpleMarkerSymbol.setOutline(blueOutlineSymbol);

            com.esri.arcgisruntime.mapping.view.Graphic pointGraphic =
                    new com.esri.arcgisruntime.mapping.view.Graphic(point, simpleMarkerSymbol);

            // add the point graphic to the graphics overlay
            graphicsOverlay.getGraphics().add(pointGraphic);
            App.Log("Point point "+pointValue);
        }

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
                    mDisease.setLocation(212.12+"."+-121);
                    mDisease.setTime(c.getTimeInMillis());

                  //  new DiseaseDbHelper().add( mDisease);

                    /*
                    new DiseaseDbHelper().query(new DiseaseDbHelper.OnAzureActionListener() {
                        @Override
                        public void query(@Nullable List<? extends Disease> results) {

                            App.Log("Azure success query "+new Gson().toJson(results.toArray(new Disease[]{})));
                        }
                    });*/
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
      /*  for(int i=0;i<mDiseases.length;i++){
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
        }*/



        mBreakoutListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager listManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        mBreakoutListRecyclerView.setLayoutManager(listManager);

        mBreakoutListRecyclerView.setAdapter(new ContentAdapter(mDiseaseItems));
    }
    private String[] supportedDiseases = new String[]{"Apple","Blueberry","Cherry (including_sour)"
    ,"Corn (maize)","Grape","Orange","Peach","Pepper, bell","Potato","Raspberry"
            ,"Soybean","Squash","Strawberry","Tomato"};

    private String[] supportedDiseasesColors = new String[]{"#FAF1DA","#D3EDFF","#D1D2FA"
            ,"#FFA3A7","#FFBCC8","#BEAAF6","#9292D4","#ADBEEB","#92B5D4","#AAE6F6"
            ,"#6096BA","#A3CDF1","#AFD9A9","#F2E9BB"};
    private String getColorFromDisease(Disease mDisease){
        for(int i=0;i<supportedDiseases.length;i++) {
            String supportedDisease =supportedDiseases[i];
            if(mDisease.getPlant().equals(supportedDisease))
                return supportedDiseasesColors[i];

        }
        return"#607d8b";
    }
    private void queryMap(){

        /*
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
        });*/
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
                //    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mDiseaseItem.mDisease.getLocationReported()[0], mDiseaseItem.mDisease.getLocationReported()[1])));

                }
            });
        }
        @Override
        public int getItemCount() {
            return mDiseaseItem.size();
        }
    }

}
