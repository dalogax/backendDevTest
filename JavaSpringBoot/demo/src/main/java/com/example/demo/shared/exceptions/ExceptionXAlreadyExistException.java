package com.example.demo.shared.exceptions;

import com.example.demo.shared.exceptions.GenericException;

public class ExceptionXAlreadyExistException extends GenericException {
    public ExceptionXAlreadyExistException(String msg){
        super(msg + " already exist");
    }
}

