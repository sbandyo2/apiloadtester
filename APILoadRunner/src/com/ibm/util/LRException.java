package com.ibm.util;

public class LRException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int SESSION_TIMEDOUT = 100;
	public static final int DATA_VALIDATION_ERROR = 999;
	
	private int errorCode = 0;
	private String errorDisplayCode = null;

	/**
	 * @param e
	 */
	public LRException(Exception e)
	{
		super(e.getMessage());
	}
	/**
	 * Constructor
	 * @param message
	 */
	public LRException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructor
	 * @param code
	 * @param message
	 */
	public LRException(int code, String message)
	{
		super(message);
		setErrorCode(code);
	}

	/**
	 * Constructor
	 * @param code
	 * @param message
	 */
	public LRException(String displayCode, String message)
	{
		super(message);
		setErrorDisplayCode(displayCode);
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorDisplayCode() {
		return errorDisplayCode;
	}

	public void setErrorDisplayCode(String errorDisplayCode) {
		this.errorDisplayCode = errorDisplayCode;
	}
}
