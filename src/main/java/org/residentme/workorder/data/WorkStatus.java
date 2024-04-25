package org.residentme.workorder.data;

/**
 * Enum for the status of a work order.
 */
public enum WorkStatus {
	
    OPEN("OPEN", "OPEN"),
    ASSIGNED("ASSIGNED", "ASSIGNED"),
    ONGOING("ONGOING", "ONGOING"),
    FINISHED("FINISHED", "FINISHED");
	
	private final String value;
	private final String name;
	
	WorkStatus(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public String value() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}	
	
}
