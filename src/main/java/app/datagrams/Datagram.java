package app.datagrams;


public class Datagram {
    private final long timestamp;
    private final int lineId;
    private final int busId;
    private final String tripId;
    private final double latitude;
    private final double longitude;
    private final int stopId;
    private final int orientation;

    public Datagram(long timestamp, int lineId, int busId, String tripId, 
                   double latitude, double longitude, int stopId, int orientation) {
        this.timestamp = timestamp;
        this.lineId = lineId;
        this.busId = busId;
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stopId = stopId;
        this.orientation = orientation;
    }

    public long getTimestamp() { return timestamp; }
    public int getLineId() { return lineId; }
    public int getBusId() { return busId; }
    public String getTripId() { return tripId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getStopId() { return stopId; }
    public int getOrientation() { return orientation; }
}

