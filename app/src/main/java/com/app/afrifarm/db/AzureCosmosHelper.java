package com.app.afrifarm.db;

import com.azure.data.AzureData;
import com.azure.data.model.Database;
import com.azure.data.model.service.ListResponse;
import com.azure.data.model.service.Response;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static com.azure.data.util.FunctionalUtils.onCallback;

public class AzureCosmosHelper {
    private static final String EndpointUri = "https://afrifarm.documents.azure.com:443/";

    private static final String PrimaryKey = "elPWLUzA4AqhNjrSNBFrYy6DsiwlRiOX1wneu8EGrT805qjiUVOiyW49rLXFqIAbgTLhakAu06qx241aVP61kQ==";

//    private CosmosClient cosmosClient;

    private Database database;

 //   private Container container;

    private String databaseId = "afrifarmdb";
    private String containerId = "afrifarmcontainer";
    private String collectionName = containerId;
    public interface OnAzureActionListener{
        void query(Disease[] results);
    }
    public AzureCosmosHelper(){
    //    AzureData.crea
    }
    private void init(){

      //  AzureData.createDatabase(databaseId,null);
    }
    public void add(Disease mDisease){
        /*AzureData.createDocument(mDisease,collectionName,databaseId,
                onCallback( response->{
                }));*/
    }
    public void delete(Disease mDisease){
       /* AzureData.deleteDocument(mDisease,collectionName,databaseId,
                onCallback( response->{
                }));*/
    }
    public void query(final OnAzureActionListener mOnAzureActionListener){
        /*AzureData.getDocuments(collectionName,databaseId,Disease.class,onCallback( response->{
            mOnAzureActionListener.query( response.getResource().items.toArray(new Disease[]{}));
        }));*/
    //    AzureData.getDocuments(collectionName,databaseId,Disease.class,Disease::class.java){};
    /*  AzureData.getDatabase(databaseId,onCallback( response->{
          database = response.getResource();
      }));*/
      //  AzureData.cr
    }

}
