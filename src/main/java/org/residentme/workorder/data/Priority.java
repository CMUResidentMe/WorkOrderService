package org.residentme.workorder.data;

/**
 * Enum for the priority of a work order.
 */
public enum Priority {

	High("High", "High"), Medium("Medium", "Medium"), Low("Low", "Low");

	private final String value;
	private final String name;

	Priority(String value, String name) {
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
