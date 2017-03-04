package com.example.shiyam.dummy.tourcoach;

/**
 * Created by shiyam on 2/2/16.
 */
public class BookingManager {

    private Individual individual;
    private String uniqueCode;
    private String startDate;
    private Route aRoute;
    private Driver aDriver;
    private Coach coach;

    public void book(){

    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Route getaRoute() {
        return aRoute;
    }

    public void setaRoute(Route aRoute) {
        this.aRoute = aRoute;
    }

    public Driver getaDriver() {
        return aDriver;
    }

    public void setaDriver(Driver aDriver) {
        this.aDriver = aDriver;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }
}
