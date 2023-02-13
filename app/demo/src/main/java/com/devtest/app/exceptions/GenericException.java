package com.devtest.app.exceptions;

import lombok.Getter;

@Getter
public class GenericException extends Exception {
	
	private String message;
	
	public GenericException(String message) {
		this.message = message;
	}

}