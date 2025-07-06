package ch.buenzli.m306.electronicanalyticsbackend.model;

import ch.buenzli.m306.electronicanalyticsbackend.model.Messwert;

import java.util.List;

public class SensorData {
    private String sensorId;
    private List<Messwert> data;   // List mit Generics

    public SensorData(String sensorId, List<Messwert> data) {
        this.sensorId = sensorId;
        this.data = data;
    }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public List<Messwert> getData() {
        return data;
    }
    public void setData(List<Messwert> data) {
        this.data = data;
    }
}