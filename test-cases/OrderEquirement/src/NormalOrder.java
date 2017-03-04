package com.customerorder;

public class NormalOrder extends Order{
	
	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		super.confirm();
		System.out.println("Confirm Normal Order");
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		System.out.println("Close Normal Order");
	}

	public void dispose(){
		System.out.println("Normal order is dispatched");
	}
	
	public void recieve(){
		System.out.println("Normal order is recieved");
	}
	
	
}
