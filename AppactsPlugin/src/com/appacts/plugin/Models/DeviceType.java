package com.appacts.plugin.Models;

public enum DeviceType {
	Android(5);
	
	private int code;
	
	private DeviceType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
