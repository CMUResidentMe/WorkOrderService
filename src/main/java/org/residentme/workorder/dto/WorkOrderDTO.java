package org.residentme.workorder.dto;

import java.util.List;

import org.residentme.workorder.entity.DetailedWorkOrder;

public class WorkOrderDTO {

	private String uuid;
	private String semanticId;
	private String owner;
	private String workType;
	private String priority;
	private String detail;
	private String status;
	private String preferredTime;
	private String entryPermission;
	private String accessInstruction;
	private List<String> images;
	private String assignedStaff;
	private String changeDescription;

	public WorkOrderDTO(DetailedWorkOrder wk) {
		this.uuid = wk.getUuid();
		this.semanticId = wk.getSemanticId();
		this.owner = wk.getOwner();
		this.workType = wk.getWorkType();
		this.priority = wk.getPriority();
		this.detail = wk.getDetail();
		this.status = wk.getStatus();
		this.preferredTime = wk.getPreferredTime();
		this.entryPermission = wk.getEntryPermission();
		this.accessInstruction = wk.getAccessInstruction();
		this.images = wk.getImages();
		this.assignedStaff = wk.getAssignedStaff();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSemanticId() {
        return semanticId;
    }

	public void setSemanticId(String semanticId) {
        this.semanticId = semanticId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPreferredTime() {
		return preferredTime;
	}

	public void setPreferredTime(String preferredTime) {
		this.preferredTime = preferredTime;
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

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getAssignedStaff() {
		return assignedStaff;
	}

	public void setAssignedStaff(String assignedStaff) {
		this.assignedStaff = assignedStaff;
	}

	public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

}
