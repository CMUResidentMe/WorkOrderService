package org.residentme.workorder.kafka.msg;

import org.residentme.workorder.entity.WorkOrder;

public class WorkOrderMsg {
	
	private String msgType;
    private WorkOrder workOrder;
	
	public WorkOrderMsg(String msgType, WorkOrder wk) {
		this.msgType = msgType;
		this.workOrder = wk;
	}
}
