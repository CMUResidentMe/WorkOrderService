package org.residentme.workorder.builder;

import java.util.List;

import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.BasicWorkOrder;

public interface WorkOrderBuilder {
	
	void reset();
	void setOwner(String owner);
	void setWorkType(String workType);
	void setPriority(String priority);
	void setDetail(String detail);
	void setStatus(WorkStatus status);
	void setPreferredTime(String preferredTime);
	void uploadImages(List<String> images);
	void setEntryPermission(EntryPermission entryPermission);
	void setAccessInstruction(String accessInstruction);
	BasicWorkOrder build();
}
