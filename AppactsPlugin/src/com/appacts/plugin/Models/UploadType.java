package com.appacts.plugin.Models;

public enum UploadType {
	WhileUsingAsync(1),
	Manual(2);
	
	private int code;
	
	private UploadType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
