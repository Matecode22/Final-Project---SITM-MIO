package model;

public class Arc {
    private final Stop from;
    private final Stop to;

    public Arc(Stop from, Stop to) {
        this.from = from;
        this.to = to;
    }

    public Stop getFrom() { return from; }
    public Stop getTo() { return to; }

    @Override
    public String toString() {
        return "Arco: " + from.getStopId() + " (" + from.getLongName() + 
               ") -> " + to.getStopId() + " (" + to.getLongName() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Arc arc = (Arc) obj;
        return from.getStopId() == arc.from.getStopId() && 
               to.getStopId() == arc.to.getStopId();
    }

    @Override
    public int hashCode() {
        return from.getStopId() * 31 + to.getStopId();
    }
}

