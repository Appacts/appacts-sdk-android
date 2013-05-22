package com.appacts.plugin.Models;

public enum ApplicationStateType {
	Open(1),
	Closed(2);
	
	private int code;
	
	private ApplicationStateType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
