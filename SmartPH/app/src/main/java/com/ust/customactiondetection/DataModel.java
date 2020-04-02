package com.ust.customactiondetection;

import com.google.gson.Gson;

import androidx.annotation.NonNull;

public class DataModel {

    String sensorName;
    String sensorType;
    String SensorNumber;
    String feature;

    public DataModel(String name, String version_number, String feature ) {
        this.sensorName =name;
        this.SensorNumber =version_number;
        this.feature=feature;

    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public String getSensorNumber() {
        return SensorNumber;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public void setSensorNumber(String sensorNumber) {
        SensorNumber = sensorNumber;
    }

}