package model;

public class Messwert {
    private String timestamp;
    private double value;
    private String obis;
    private String id;
    private String source;
    private double absoluteValue;



    // Konstruktor für kombinierten Export (Bezug/Einspeisung/Netto)
    public Messwert(String timestamp, double value, String obis, String id, String source, double absoluteValue) {
        this.timestamp = timestamp;
        this.value = value;
        this.obis = obis;
        this.id = id;
        this.source = source;
        this.absoluteValue = absoluteValue;
    }

    public Messwert(String timestamp, double value, String obis, String id, String source) {
        this(timestamp, value, obis, id, source, 0.0);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getObis() {
        return obis;
    }

    public void setObis(String obis) {
        this.obis = obis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getAbsoluteValue() {
        return absoluteValue;
    }

    public void setAbsoluteValue(double absoluteValue) {
        this.absoluteValue = absoluteValue;
    }

    public String getKey() {
        return timestamp + "_" + obis + "_" + id;
    }
}
