package common;


public interface Metrics {
    
    void startTiming();

   
    void stopTiming();

   
    long getElapsedTimeNanos();

    
    double getElapsedTimeMillis();

   
    void incrementCounter(String counterName);

   
    long getCounter(String counterName);

    
    void reset();

    
    String getSummary();
}
