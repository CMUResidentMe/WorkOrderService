package org.residentme.workorder.exception;

/**
 * Exception for when no work order is found.
 */
public class NoneWorkOrderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param message The exception message.
	 */
	public NoneWorkOrderException(String message) {
		super(message);
	}

}
