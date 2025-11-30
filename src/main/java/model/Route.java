package model;

public class Route {
    private final int lineId;
    private final int planVersionId;
    private final String shortName;
    private final String description;
    private final String activationDate;

    public Route(int lineId, int planVersionId, String shortName,
                 String description, String activationDate) {
        this.lineId = lineId;
        this.planVersionId = planVersionId;
        this.shortName = shortName;
        this.description = description;
        this.activationDate = activationDate;
    }

    public int getLineId() { return lineId; }
    public int getPlanVersionId() { return planVersionId; }
    public String getShortName() { return shortName; }
    public String getDescription() { return description; }
    public String getActivationDate() { return activationDate; }

    @Override
    public String toString() {
        return shortName + " (" + lineId + ") - " + description;
    }
}

