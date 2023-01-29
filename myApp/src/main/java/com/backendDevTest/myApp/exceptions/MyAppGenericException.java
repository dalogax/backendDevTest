package com.backendDevTest.myApp.exceptions;

import lombok.Getter;

@Getter
public class MyAppGenericException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6441909820936410545L;
	
	private String message;
	
	public MyAppGenericException(String message) {
		this.message = message;
	}

}
