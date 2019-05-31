package com.ef.dto;

import org.kohsuke.args4j.Option;

public class MyOptions {

    @Option(name = "--accesslog", usage = "Path to log file")
    private String accessLog;
    @Option(name = "--startDate", usage = "Query start date")
    private String startDate;
    @Option(name = "--duration", usage = "Date duration either Daily/Hourly")
    private String duration;
    @Option(name = "--threshold", usage = "The amount of request made")
    private int threshold;

    public String getAccessLog() {
        return accessLog;
    }

    public void setAccessLog(String accessLog) {
        this.accessLog = accessLog;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "MyOptions{" +
                "accessLog='" + accessLog + '\'' +
                ", startDate='" + startDate + '\'' +
                ", duration='" + duration + '\'' +
                ", threshold=" + threshold +
                '}';
    }
}