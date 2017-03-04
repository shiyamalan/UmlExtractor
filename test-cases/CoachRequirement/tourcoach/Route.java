package com.example.shiyam.dummy.tourcoach;

import java.util.List;

/**
 * Created by shiyam on 2/2/16.
 */
public class Route {

    private double length;
    private String duration;
    private List<Town> towns;
    private boolean isServiceRequire;


    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Town> getTowns() {
        return towns;
    }

    public void setTowns(List<Town> towns) {
        this.towns = towns;
    }

    public boolean isServiceRequire() {
        return isServiceRequire;
    }

    public void setIsServiceRequire(boolean isServiceRequire) {
        this.isServiceRequire = isServiceRequire;
    }
}
