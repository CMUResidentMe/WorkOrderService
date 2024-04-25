package org.residentme.workorder.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import org.residentme.workorder.data.WorkStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Entity for the detailed work order.
 */
@Data
@TypeAlias("workorder")
public class DetailedWorkOrder {

	/**
	 * The UUID of the work order.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String uuid;
	/**
	 * The semanticId of the work order.
	 */
	private String semanticId;
	/**
	 * The owner of the work order.
	 */
	private String owner; // user UUID
	/**
	 * The status of the work order.
	 */
	private String status;
	/**
	 * The type of work order.

	 */
	private String workType;
	/**
	 * The priority of the work order.
	 */
	private String priority;
	/**
	 * The preferred time of the work order.
	 */
	private String preferredTime;
	/**
	 * The entry permission of the work order.
	 */
	private String entryPermission;
	/**
	 * The access instruction of the work order.
	 */
	private String accessInstruction;
	/**
	 * The detail of the work order.
	 */
	private String detail;
	/**
	 * The images of the work order.
	 */
	private List<String> images;
	/**
	 * The assigned staff of the work order.
	 */
	private String assignedStaff;
	/**
	 * The create time of the work order.
	 */
	private String createTime;

	public DetailedWorkOrder() {
    }

	/**
	 * Constructor for the DetailedWorkOrder object.
	 * @param workorder
	 */
	public DetailedWorkOrder(DetailedWorkOrder workorder) {
		this.semanticId = workorder.getSemanticId();
		this.owner = workorder.getOwner();
		this.workType = workorder.getWorkType();
		this.priority = workorder.getPriority();
		this.preferredTime = workorder.getPreferredTime();
		this.entryPermission = workorder.getEntryPermission();
		this.accessInstruction = workorder.getAccessInstruction();
		this.detail = workorder.getDetail();
		this.images = workorder.getImages();
		this.assignedStaff = workorder.getAssignedStaff();
		this.status = workorder.getStatus();
		this.createTime = workorder.getCreateTime();
    }

	/**
	 * Constructor for the DetailedWorkOrder object.
	 * @param semanticId The semanticId of the work order.
	 * @param owner The owner of the work order.
	 * @param workType The type of work order.
	 * @param priority The priority of the work order.
	 * @param preferredTime The preferred time of the work order.
	 * @param entryPermission The entry permission of the work order.
	 * @param accessInstruction The access instruction of the work order.
	 * @param detail The detail of the work order.
	 * @param images The images of the work order.
	 */
	public DetailedWorkOrder(String semanticId, String owner, String workType, String priority, String preferredTime,
			String entryPermission, String accessInstruction, String detail, List<String> images) {
		this.semanticId = semanticId;
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

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		this.createTime = sdf.format(new Date());
	}

	public String getSemanticId() {
        return semanticId;
    }

	public String getAssignedStaff() {
		return assignedStaff;
	}

	public void setAssignedStaff(String assignedStaff) {
		this.assignedStaff = assignedStaff;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public void setSemanticId(String semanticId) {
		this.semanticId = semanticId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
