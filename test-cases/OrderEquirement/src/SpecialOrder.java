package com.customerorder;

public class SpecialOrder extends Order {

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		super.confirm();
		System.out.println("Confirm Special Order");
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		System.out.println("Close Special Order");
	}

	public void dispose() {
		System.out.println("Special order is dispatched");
	}

}
