package com.app.afrifarm.db;

import com.azure.data.model.Document;
import com.azure.data.model.partition.PartitionKey;

import java.util.Calendar;

public class Disease extends Document {
    private String name;
    private String plant;

    @PartitionKey
    public String items;
    private long dateReported;
    private double[] locationReported;
    public Disease(){
      //  super(Long.toString(c.getTimeInMillis());
        Calendar c = Calendar.getInstance();
        this.items ="time-"+Long.toString(c.getTimeInMillis());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public long getDateReported() {
        return dateReported;
    }

    public void setDateReported(long dateReported) {
        this.dateReported = dateReported;
    }

    public double[] getLocationReported() {
        return locationReported;
    }

    public void setLocationReported(double[] locationReported) {
        this.locationReported = locationReported;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }








}
