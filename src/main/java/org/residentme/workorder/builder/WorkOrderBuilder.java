package org.residentme.workorder.builder;

import java.util.List;
import java.util.Optional;

import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.Priority;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.DetailedWorkOrder;

public interface WorkOrderBuilder {
	
	void reset();
	void setOwner(String owner);
	void setWorkType(String workType);
	void setPriority(Priority priority);
	void setDetail(String detail);
	void setStatus(WorkStatus status);
	void setPreferredTime(String preferredTime);
	void setEntryPermission(EntryPermission entryPermission);
	void setAccessInstruction(String accessInstruction);
	void setImages(List<String> images);
	void setAssignedStaff(Optional<String> assignedStaff);
	DetailedWorkOrder build();
}
