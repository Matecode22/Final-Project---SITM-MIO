package model;

public class LineStop {
    private final int lineStopId;
    private final int stopSequence;
    private final int orientation; // 0 = ida, 1 = vuelta
    private final int lineId;
    private final int stopId;
    private final int planVersionId;
    private final int lineVariant;
    private final int lineVariantType;

    public LineStop(int lineStopId, int stopSequence, int orientation, 
                   int lineId, int stopId, int planVersionId, 
                   int lineVariant, int lineVariantType) {
        this.lineStopId = lineStopId;
        this.stopSequence = stopSequence;
        this.orientation = orientation;
        this.lineId = lineId;
        this.stopId = stopId;
        this.planVersionId = planVersionId;
        this.lineVariant = lineVariant;
        this.lineVariantType = lineVariantType;
    }

    public int getLineStopId() { return lineStopId; }
    public int getLineId() { return lineId; }
    public int getStopId() { return stopId; }
    public int getSequence() { return stopSequence; }
    public int getOrientation() { return orientation; }
    public int getPlanVersionId() { return planVersionId; }
    public int getLineVariant() { return lineVariant; }
    public int getLineVariantType() { return lineVariantType; }

    @Override
    public String toString() {
        return "LineStop{lineId=" + lineId + ", stopId=" + stopId + 
               ", sequence=" + stopSequence + ", orientation=" + orientation + "}";
    }
}

