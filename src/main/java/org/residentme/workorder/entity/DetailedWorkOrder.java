package org.residentme.workorder.entity;

import lombok.Data;

import java.util.List;

import org.residentme.workorder.builder.WorkOrderBuilder;
import org.residentme.workorder.data.WorkStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@TypeAlias("detailed")
public class DetailedWorkOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String uuid;
	private String owner; // user UUID
	private String status;
	private String workType;
	private String priority;
	private String preferredTime;
	private String entryPermission;
	private String accessInstruction;
	private String detail;
	private List<String> images;
	private String assignedStaff;

	public DetailedWorkOrder() {
    }

	public DetailedWorkOrder(DetailedWorkOrder workorder) {
		
    }

	public DetailedWorkOrder(String owner, String workType, String priority, String preferredTime,
			String entryPermission, String accessInstruction, String detail, List<String> images) {
		this.owner = owner;
		this.workType = workType;
		this.priority = priority;
		this.preferredTime = preferredTime;
		this.entryPermission = entryPermission;
		this.accessInstruction = accessInstruction;
		this.detail = detail;
		this.images = images;
		this.assignedStaff = null;
		this.status = WorkStatus.OPEN.value();
	}

	public DetailedWorkOrder(String owner, String workType, String priority, String preferredTime,
			String entryPermission, String accessInstruction, String detail, List<String> images,
			String assignedStaff) {
		this.owner = owner;
		this.workType = workType;
		this.priority = priority;
		this.preferredTime = preferredTime;
		this.entryPermission = entryPermission;
		this.accessInstruction = accessInstruction;
		this.detail = detail;
		this.images = images;
		this.assignedStaff = null;
		this.status = WorkStatus.OPEN.value();
		;
		this.assignedStaff = assignedStaff;
	}

	public String getAssignedStaff() {
		return assignedStaff;
	}

	public void setAssignedStaff(String assignedStaff) {
		this.assignedStaff = assignedStaff;
	}
}
