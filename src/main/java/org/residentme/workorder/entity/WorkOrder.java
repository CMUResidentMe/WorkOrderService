package org.residentme.workorder.entity;


import lombok.Data;

import java.util.List;

import org.residentme.workorder.data.WorkStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.graphql.data.method.annotation.Argument;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Document(value = "workorders")
public class WorkOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    private String owner;  //user UUID
    private String worktype;
    private int priority;
    private String detail;
    private String assignedstaff;
    private String status;
    private String preferredtime;
    private List<String> images;
    private String entryPermission;
    private String accessInstruction;
    
	public WorkOrder(String owner, String worktype, int priority, String detail, String preferredtime, String entryPermission, String accessInstruction) {
		super();
		this.owner = owner;
		this.worktype = worktype;
		this.priority = priority;
		this.detail = detail;
		this.entryPermission = entryPermission;
		this.preferredtime = preferredtime;
		this.accessInstruction = accessInstruction;
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

	public String getWorktype() {
		return worktype;
	}

	public void setWorktype(String worktype) {
		this.worktype = worktype;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAssignedstaff() {
		return assignedstaff;
	}

	public void setAssignedstaff(String assignedstaff) {
		this.assignedstaff = assignedstaff;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPreferredtime() {
		return preferredtime;
	}

	public void setPreferredtime(String preferredtime) {
		this.preferredtime = preferredtime;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getEntryPermission() {
		return entryPermission;
	}

	public void setEntryPermission(String entryPermission) {
		this.entryPermission = entryPermission;
	}

	public String getAccessInstruction() {
		return accessInstruction;
	}

	public void setAccessInstruction(String accessInstruction) {
		this.accessInstruction = accessInstruction;
	}
}
