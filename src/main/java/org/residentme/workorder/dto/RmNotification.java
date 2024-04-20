package org.residentme.workorder.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RmNotification {
	
	private String notificationType;
	private String eventTime;
    private String owner;
    private String message;
    private String sourceID;

	public RmNotification(String notificationType) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		this.eventTime = sdf.format(new Date());
		this.notificationType = notificationType;
	}
	
	public RmNotification(RmNotification other) {
		this.eventTime = other.getEventTime();
		this.notificationType = other.getNotificationType();
		this.owner = other.getOwner();
		this.message = other.getMessage();
		this.sourceID = other.getSourceID();
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

}
