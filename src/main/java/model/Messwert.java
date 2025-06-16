package model;

public class Messwert {
    private String id;
    private String obis;
    private String timestamp;
    private double value;
    private String status;
    private String source;

    private double absoluteValue;

    public Messwert(String id, String obis, String timestamp, double value, String status, String source) {
        this.id = id;
        this.obis = obis;
        this.timestamp = timestamp;
        this.value = value;
        this.status = status;
        this.source = source;
        this.absoluteValue = 0; // default
    }

    public String getKey() {
        return id + "-" + obis + "-" + (timestamp != null ? timestamp : "no-time");
    }

    public String getId() {
        return id;
    }

    public String getObis() {
        return obis;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public double getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public String getSource() {
        return source;
    }

    public double getAbsoluteValue() {
        return absoluteValue;
    }

    public void setAbsoluteValue(double absoluteValue) {
        this.absoluteValue = absoluteValue;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%.4f\t%s\tAbs: %.4f\t%s\t%s",
                id, obis, value,
                timestamp != null ? timestamp : "-",
                absoluteValue, status, source);
    }
}
