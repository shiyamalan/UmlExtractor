package com.example.shiyam.dummy.tourcoach;

/**
 * Created by shiyam on 2/2/16.
 */
public class HotelClerk {

    private Bill bill;


    HotelClerk(Bill bill){
        this.bill = bill;
    }
    public void payBill(Bill bill){

    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
