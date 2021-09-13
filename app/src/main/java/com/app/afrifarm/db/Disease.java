package com.app.afrifarm.db;

import com.azure.data.model.Document;
import com.azure.data.model.partition.PartitionKey;

import java.util.Calendar;

public class Disease extends NetworkAction {
    private String name;
    private String plant;

    @PartitionKey
    public String items;
    private long time;
    private String location;

    private String action="addresult";


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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



    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
