package com.appacts.plugin.Models;

public enum WebServiceResponseType {
	Ok(100),
	InactiveAccount(101),
	InactiveApplication(102),
	NoDevice(103),
	GeneralError(104);
	
	private int code;
	
	private WebServiceResponseType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
