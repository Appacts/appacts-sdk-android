package com.appacts.plugin.Models;

public enum SexType {
	Male(1),
	Female(2);
	
	private int code;
	
	private SexType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
