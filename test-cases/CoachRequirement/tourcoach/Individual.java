package com.example.shiyam.dummy.tourcoach;

/**
 * Created by shiyam on 2/2/16.
 */
public class Individual {

    private String name;

    Individual(String name){
        this.setName(name);
    }
    Individual(){
        this("");
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
