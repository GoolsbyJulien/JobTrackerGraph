package com.example.jobtracker;

public class Profiler {
    private final String name;
    public long[] times = new long[100];
    public int nextTime = 0;
    boolean allFilled = false;
    private long startTime;
    private long endTime;


    public Profiler(String name) {
        start();
        this.name = name;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end() {
        endTime = System.currentTimeMillis() - startTime;
        times[nextTime] = endTime;

        nextTime++;
        if (nextTime == times.length) {
            nextTime = 0;
        }

    }

    public void printTimeMill() {
        end();
        System.out.println(name + ": " + (endTime) + "ms");
    }

    public long getAverage() {

        int max = allFilled ? times.length : nextTime;
        long sum = 0;
        if (max == 0) {
            return times[0];
        }
        for (int i = 0; i < max; i++) {
            sum += times[i];
        }


        return sum / max;
    }

}

