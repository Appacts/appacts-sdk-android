package com.appacts.plugin.Models;

public enum RatingType {
	One(1),
	Two(2),
	Three(3),
	Four(4),
	Five(5);
	
	private int code;
	
	private RatingType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
