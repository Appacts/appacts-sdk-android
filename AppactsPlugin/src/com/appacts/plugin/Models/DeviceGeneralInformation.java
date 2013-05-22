package com.appacts.plugin.Models;

public class DeviceGeneralInformation {
	public final long AvailableFlashDriveSize;
	public final long AvailableMemorySize;
	public final int Battery;
	public final int NetworkCoverage;
	
	public DeviceGeneralInformation(long availableFlashDriveSize,
			long availableMemorySize, int battery, int networkCoverage) {
		this.AvailableFlashDriveSize = availableFlashDriveSize;
		this.AvailableMemorySize = availableMemorySize;
		this.Battery = battery;
		this.NetworkCoverage = networkCoverage;
	}
}
