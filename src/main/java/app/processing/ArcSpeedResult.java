package app.processing;

/**
 * Acumula distancia (metros) y tiempo (segundos) para un arco.
 */
public class ArcSpeedResult {
    private double totalDistanceMeters = 0.0;
    private double totalTimeSeconds = 0.0;
    private long samples = 0;

    public synchronized void add(double distanceMeters, double dtSeconds) {
        this.totalDistanceMeters += distanceMeters;
        this.totalTimeSeconds += dtSeconds;
        this.samples++;
    }

    public synchronized double getAverageSpeedKmh() {
        if (totalTimeSeconds <= 0) return 0.0;
        double hours = totalTimeSeconds / 3600.0;
        double km = totalDistanceMeters / 1000.0;
        return km / hours;
    }

    public synchronized long getSamples() {
        return samples;
    }

    public synchronized double getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public synchronized double getTotalTimeSeconds() {
        return totalTimeSeconds;
    }
}

