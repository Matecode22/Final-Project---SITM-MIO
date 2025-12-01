package app.metrics;

public class MetricsReport {
    public static void print(String name, long lines, int workers, long timeMs) {
        double secs = timeMs / 1000.0;
        double throughput = lines / Math.max(1.0, secs);
        System.out.println("RESULT " + name + " lines=" + lines + " workers=" + workers + 
                          " timeMs=" + timeMs + " throughput(d/s)=" + String.format("%.2f", throughput));
    }
}

