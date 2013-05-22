package com.appacts.plugin.Models;

public enum StatusType {
	All(0),
	Pending(1),
	Processed(2);
	
	private int code;
	
	private StatusType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
