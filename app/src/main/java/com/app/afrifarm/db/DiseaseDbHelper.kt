package com.app.afrifarm.db

import com.app.afrifarm.App
import com.azure.data.AzureData
import com.azure.data.model.Database
import com.azure.data.model.service.ListResponse
import com.azure.data.model.service.Response

class DiseaseDbHelper {

    private val databaseId = "afrifarmdb"
    private val containerId = "afrifarmcontainer"
    private val collectionName = containerId

    interface OnAzureActionListener {
        fun query(results: List<Disease>?)
    }
    fun createCollection():Unit{
        AzureData.createCollection (collectionName, databaseId) {
            // collection = it.resource
        }
    }
    fun delete(disease:Disease):Unit{
        AzureData.deleteDocument (disease, collectionName, databaseId) {
            // successfully deleted == it.isSuccessful
        }
    }
    fun add(disease:Disease):Unit{
        AzureData.createDocument (disease, collectionName, databaseId) {
            // created document = it.resource
            App.Log("Azure added doc "+it.isSuccessful+" code "+it.error.toString())
        }
    }
    fun query(onAzureActionListener:OnAzureActionListener):Unit{
        AzureData.getDocuments (collectionName, databaseId, Disease::class.java) {
            // documents = it.resource?.items
            onAzureActionListener.query(it.resource?.items)
        }
    }
}