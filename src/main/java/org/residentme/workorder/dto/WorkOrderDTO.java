package org.residentme.workorder.dto;

import java.util.List;
import java.util.Optional;

import org.residentme.workorder.entity.BasicWorkOrder;
import org.residentme.workorder.entity.DetailedWorkOrder;

public class WorkOrderDTO {

	private String uuid;
	private String owner;
	private String workType;
	private String priority;

	private String detail;
	private String assignedStaff;
	private String status;

	private Optional<String> preferredTime = Optional.empty();
	private Optional<String> entryPermission = Optional.empty();
	private Optional<String> accessInstruction = Optional.empty();
	private Optional<List<String>> images = Optional.empty();

	public WorkOrderDTO(BasicWorkOrder wk) {
		this.uuid = wk.getUuid();
		this.owner = wk.getOwner();
		this.workType = wk.getWorkType();
		this.priority = wk.getPriority();
		this.detail = wk.getDetail();
		this.assignedStaff = wk.getAssignedStaff();
		this.status = wk.getStatus();

		if (wk instanceof DetailedWorkOrder) {
			DetailedWorkOrder detailed = (DetailedWorkOrder) wk;
			this.preferredTime = Optional.ofNullable(detailed.getPreferredTime());
			this.entryPermission = Optional.ofNullable(detailed.getEntryPermission());
			this.accessInstruction = Optional.ofNullable(detailed.getAccessInstruction());
			this.images = Optional.ofNullable(detailed.getImages());
		}
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
		return priority;
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

	public Optional<String> getPreferredTime() {
		return preferredTime;
	}

	public void setPreferredTime(Optional<String> preferredTime) {
		this.preferredTime = preferredTime;
	}

	public Optional<String> getEntryPermission() {
		return entryPermission;
	}

	public void setEntryPermission(Optional<String> entryPermission) {
		this.entryPermission = entryPermission;
	}

	public Optional<String> getAccessInstruction() {
		return accessInstruction;
	}

	public void setAccessInstruction(Optional<String> accessInstruction) {
		this.accessInstruction = accessInstruction;
	}

	public Optional<List<String>> getImages() {
		return images;
	}

	public void setImages(Optional<List<String>> images) {
		this.images = images;
	}



}
