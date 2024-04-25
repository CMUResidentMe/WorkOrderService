package org.residentme.workorder.data;

/**
 * Enum for the entry permission of a work order.
 */
public enum EntryPermission {
	
	CALLCONFIRM("CALLCONFIRM", "CALLCONFIRM"),
	KNOCKDOOR("KNOCKDOOR", "KNOCKDOOR"),
	ALL_PERMISSIONS("ALL_PERMISSIONS", "ALL_PERMISSIONS");
	
	private final String value;
	private final String name;
	
	EntryPermission(String value, String name) {
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