package app.metrics;

public class Timer {
    private long start;

    public void start() {
        start = System.currentTimeMillis();
    }

    public long stopMs() {
        return System.currentTimeMillis() - start;
    }
}

