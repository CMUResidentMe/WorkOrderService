package org.residentme.workorder.dto;

import org.residentme.workorder.entity.WorkOrder;

public class WorkOrderDTO {
	
    private String uuid;
    private String owner;
    private String worktype;
    private int priority;
    private String detail;
    private String assignedstaff;
    private String status;
    private String preferredtime;
    private String entryPermission;
    private String accessInstruction;
    
	public WorkOrderDTO(WorkOrder wk) {
		super();
		this.uuid = wk.getUuid();
		this.owner = wk.getOwner();
		this.worktype = wk.getWorktype();
		this.priority = wk.getPriority();
		this.detail = wk.getDetail();
		this.assignedstaff = wk.getAssignedstaff();
		this.status = wk.getStatus();
		this.preferredtime = wk.getPreferredtime();
		this.entryPermission = wk.getEntryPermission();
		this.accessInstruction = wk.getAccessInstruction();
	}
    
	
}
