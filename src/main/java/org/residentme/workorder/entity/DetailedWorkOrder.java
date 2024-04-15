package org.residentme.workorder.entity;


import lombok.Data;

import java.util.List;

import org.residentme.workorder.data.WorkStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@TypeAlias("detailed")
public class DetailedWorkOrder extends BasicWorkOrder {
	
    private String preferredTime;
    private List<String> images;
    private String entryPermission;
    private String accessInstruction;
    
    public DetailedWorkOrder() {
		super();
	}
    
	public DetailedWorkOrder(String owner, String workType, String priority, String detail, String assignedStaff) {
		super(owner, workType, priority, detail, assignedStaff);
	}

	public String getPreferredTime() {
		return preferredTime;
	}

	public void setPreferredTime(String preferredTime) {
		this.preferredTime = preferredTime;
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
