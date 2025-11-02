package common;

import java.util.HashMap;
import java.util.Map;


public class MetricsImpl implements Metrics {
    private long startTime;
    private long endTime;
    private final Map<String, Long> counters;

    public MetricsImpl() {
        this.counters = new HashMap<>();
    }

    @Override
    public void startTiming() {
        startTime = System.nanoTime();
    }

    @Override
    public void stopTiming() {
        endTime = System.nanoTime();
    }

    @Override
    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    @Override
    public double getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    @Override
    public void incrementCounter(String counterName) {
        counters.put(counterName, counters.getOrDefault(counterName, 0L) + 1);
    }

    @Override
    public long getCounter(String counterName) {
        return counters.getOrDefault(counterName, 0L);
    }

    @Override
    public void reset() {
        startTime = 0;
        endTime = 0;
        counters.clear();
    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Time: %.3f ms%n", getElapsedTimeMillis()));
        for (Map.Entry<String, Long> entry : counters.entrySet()) {
            sb.append(String.format("%s: %d%n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
