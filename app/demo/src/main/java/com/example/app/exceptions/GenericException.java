package com.example.app.exceptions;

import lombok.Getter;

@Getter
public class GenericException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6441909820936410545L;
	
	private String message;
	
	public GenericException(String message) {
		this.message = message;
	}

}