package com.appacts.plugin.Models;

public enum OptStatusType {
	None(0),
	OptIn(1),
	OptOut(2);
	
	private int code;
	
	private OptStatusType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
