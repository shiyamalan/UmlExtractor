package com.customerorder;

public class Customer {
	private String name;
	private String address;
	private Order order;
	
	public void sendOrder(){
		System.out.println("Customer sends an order");
	}
	
	public void reciveOrder(){
		System.out.println("Customer recieves an order");
	}

}
