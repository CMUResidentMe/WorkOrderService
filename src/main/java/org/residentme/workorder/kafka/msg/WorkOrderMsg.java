package org.residentme.workorder.kafka.msg;

import org.residentme.workorder.dto.WorkOrderDTO;

public class WorkOrderMsg {
	
	private String msgType;
    private WorkOrderDTO workOrder;
	
	public WorkOrderMsg(String msgType, WorkOrderDTO wk) {
		this.msgType = msgType;
		this.workOrder = wk;
	}
}
