package org.residentme.workorder.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Notification class for the ResidentMe system.
 */
public class RmNotification {

	/**
	 * Notification types.
	 */
	private String notificationType;
	/**
	 * Time of the event.
	 */
	private String eventTime;
	/**
	 * Receiver of the notification.
	 */
    private String owner;
	/**
	 * Message of the notification.
	 */
    private String message;
	/**
	 * ID of object of concern that triggered the notification.
	 */
    private String sourceID;

	/**
	 * Constructor for the RmNotification class.
	 * @param notificationType Type of the notification.
	 */
	public RmNotification(String notificationType) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		this.eventTime = sdf.format(new Date());
		this.notificationType = notificationType;
	}

	/**
	 * Constructor for the RmNotification class.
	 * @param other RmNotification object to copy.
	 */
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
