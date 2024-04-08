package org.residentme.workorder.exception;

public class NoneWorkOrderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoneWorkOrderException(String message) {
		super(message);
	}

}
