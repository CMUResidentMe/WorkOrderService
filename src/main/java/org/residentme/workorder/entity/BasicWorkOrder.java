package org.residentme.workorder.entity;


import lombok.Data;

import org.residentme.workorder.data.WorkStatus;
import org.springframework.data.annotation.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "workorders")
@TypeAlias("basic")
public class BasicWorkOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    private String owner;  //user UUID
    private String workType;
    private String priority;
    private String detail;
    private String assignedStaff;
    private String status;



	public BasicWorkOrder() {
		super();
	}

	public BasicWorkOrder(String owner, String workType, String priority, String detail, String assignedStaff) {
		super();
		this.owner = owner;
		this.workType = workType;
		this.priority = priority;
		this.detail = detail;
		this.assignedStaff = assignedStaff;
		this.status = WorkStatus.OPEN.value();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAssignedStaff() {
		return assignedStaff;
	}

	public void setAssignedStaff(String assignedStaff) {
		this.assignedStaff = assignedStaff;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
