package ch.buenzli.m306.electronicanalyticsbackend.model;

import java.time.Instant;

public class Messwert {
    private String sensorId;
    private Instant timestamp;
    private double relativeValue;
    private double absoluteValue;

    public Messwert(Instant timestamp, double relativeValue, double absoluteValue) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.relativeValue = relativeValue;
        this.absoluteValue = absoluteValue;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getRelativeValue() {
        return relativeValue;
    }

    public void setRelativeValue(double relativeValue) {
        this.relativeValue = relativeValue;
    }

    public double getAbsoluteValue() {
        return absoluteValue;
    }

    public void setAbsoluteValue(double absoluteValue) {
        this.absoluteValue = absoluteValue;
    }
}