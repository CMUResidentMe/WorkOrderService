package org.residentme.workorder.builder.imp;

import java.util.List;
import java.util.Optional;

import org.residentme.workorder.builder.WorkOrderBuilder;
import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.Priority;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.residentme.workorder.repository.DetailedWorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ConcreteWorkOrderBuilder implements WorkOrderBuilder{

    private String owner;  //user UUID
    private String workType;
    private Priority priority;
    private String detail;
    private String status;
    private String preferredTime;
    private List<String> images;
    private String entryPermission;
    private String accessInstruction;
    private Optional<String> assignedStaff;

	@Override
	public void reset() {
		this.owner = null;
		this.workType = null;
		this.priority = null;
		this.detail = null;
		this.status = WorkStatus.OPEN.value();
		this.preferredTime = "";
		this.entryPermission = null;
		this.accessInstruction = null;
		this.images = null;
		this.assignedStaff = null;
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
	public void setPriority(Priority priority) {
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
	public void setEntryPermission(EntryPermission entryPermission) {
		this.entryPermission = entryPermission.value();
	}

	@Override
	public void setAccessInstruction(String accessInstruction) {
		this.accessInstruction = accessInstruction;
	}

	public void setAssignedStaff(Optional<String> assignedStaff) {
		this.assignedStaff = assignedStaff;
	}

	public void setPreferredTime(String preferredTime) {
		this.preferredTime = preferredTime;
	}
	

	@Override
	public void setImages(List<String> images) {
		this.images = images;
	}

	@Override
	public DetailedWorkOrder build() {
		DetailedWorkOrder workOrder;
		workOrder = new DetailedWorkOrder(this.owner, this.workType, this.priority.value(), this.preferredTime, this.entryPermission, 
					this.accessInstruction, this.detail, this.images);
		workOrder.setAssignedStaff(assignedStaff.get());
		workOrder.setStatus(this.status);
		return workOrder;
	}


}
