package com.appacts.plugin.Models;

public enum EventType {
	ApplicationOpen(1),
	ApplicationClose(2),
	Error(3),
	Event(4),
	Feedback(5),
	ScreenClosed(6),
	ContentLoaded(7),
	ContentLoading(8),
	ScreenOpen(9);
	
	private int code;
	
	private EventType(int code) {
		this.code = code;
	}
	
	public int GetCode() {
		return this.code;
	}
}
