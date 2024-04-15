package org.residentme.workorder.builder.imp;

import java.util.List;

import org.residentme.workorder.builder.WorkOrderBuilder;
import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.BasicWorkOrder;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.residentme.workorder.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ConcreteWorkOrderBuilder implements WorkOrderBuilder{
	
	@Autowired
	private WorkOrderRepository woRepository;
	
    private String owner;  //user UUID
    private String workType;
    private String priority;
    private String detail;
    private String assignedStaff;
    private String status;
    private String preferredTime;
    private List<String> images;
    private String entryPermission;
    private String accessInstruction;

	@Override
	public void reset() {
		this.owner = null;
		this.workType = null;
		this.priority = null;
		this.detail = null;
		this.assignedStaff = null;
		this.status = WorkStatus.OPEN.value();
		this.preferredTime = "";
		this.entryPermission = null;
		this.accessInstruction = null;
	}

	@Override
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public void setWorkType(String workType) {
		this.workType = workType;
	}

	@Override
	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public void setStatus(WorkStatus status) {
		this.status = status.value();
	}

	@Override
	public void uploadImages(List<String> images) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEntryPermission(EntryPermission entryPermission) {
		this.entryPermission = entryPermission.value();
	}

	@Override
	public void setAccessInstruction(String accessInstruction) {
		this.accessInstruction = accessInstruction;
	}

	public void setAssignedStaff(String assignedStaff) {
		this.assignedStaff = assignedStaff;
	}

	public void setPreferredTime(String preferredTime) {
		this.preferredTime = preferredTime;
	}

	@Override
	public BasicWorkOrder build() {
		BasicWorkOrder workOrder;
		if (this.preferredTime != null || this.entryPermission != null || this.accessInstruction != null) {
			workOrder = new DetailedWorkOrder(this.owner, this.workType, this.priority, this.detail, this.assignedStaff);
			((DetailedWorkOrder)workOrder).setPreferredTime(this.preferredTime);
			((DetailedWorkOrder)workOrder).setEntryPermission(this.entryPermission);
			((DetailedWorkOrder)workOrder).setAccessInstruction(this.accessInstruction);
			// Additional logic for images and other detailed work order fields
		} else {
			workOrder = new BasicWorkOrder(this.owner, this.workType, this.priority, this.detail, this.assignedStaff);
		}
		workOrder.setStatus(this.status);
		return woRepository.save(workOrder).block();
	}


}
