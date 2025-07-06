package ch.buenzli.m306.electronicanalyticsbackend.model;

public enum ObisCode {

    BEZUG_HOCH("1-1:1.8.1", "ID742"),
    BEZUG_NIEDER("1-1:1.8.2", "ID742"),
    EINSPEISUNG_HOCH("1-1:2.8.1", "ID735"),
    EINSPEISUNG_NIEDER("1-1:2.8.2", "ID735");

    private final String code;
    private final String sensorId;
    ObisCode(String code, String sensorId){ this.code=code; this.sensorId=sensorId; }


    public String getCode() {
        return code;
    }

    public String getSensorId() {
        return sensorId;
    }
}
