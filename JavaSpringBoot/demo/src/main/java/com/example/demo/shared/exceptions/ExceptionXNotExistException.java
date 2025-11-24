package com.example.demo.shared.exceptions;

import com.example.demo.shared.exceptions.GenericException;

public class ExceptionXNotExistException extends GenericException {
    public ExceptionXNotExistException(String msg){
        super(msg+" not exist");
    }
}

