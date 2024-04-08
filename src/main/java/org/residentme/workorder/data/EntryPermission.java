package org.residentme.workorder.data;

public enum EntryPermission {
	
	CALLCONFIRM("CALLCONFIRM", "CALLCONFIRM"),
	KNOCKDOOR("KNOCKDOOR", "KNOCKDOOR");
	
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
