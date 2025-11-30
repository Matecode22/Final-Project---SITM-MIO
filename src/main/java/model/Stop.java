package model;

public class Stop {
    private final int stopId;
    private final int planVersionId;
    private final String shortName;
    private final String longName;
    private final double decimalLong;
    private final double decimalLat;

    public Stop(int stopId, int planVersionId, String shortName, String longName,
                double decimalLong, double decimalLat) {
        this.stopId = stopId;
        this.planVersionId = planVersionId;
        this.shortName = shortName;
        this.longName = longName;
        this.decimalLong = decimalLong;
        this.decimalLat = decimalLat;
    }

    public int getStopId() { return stopId; }
    public int getPlanVersionId() { return planVersionId; }
    public String getShortName() { return shortName; }
    public String getLongName() { return longName; }
    public double getDecimalLong() { return decimalLong; }
    public double getDecimalLat() { return decimalLat; }

    @Override
    public String toString() {
        return stopId + " - " + longName;
    }
}

