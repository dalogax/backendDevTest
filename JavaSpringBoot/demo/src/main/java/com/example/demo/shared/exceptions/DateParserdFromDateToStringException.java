package com.example.demo.shared.exceptions;

import java.util.Date;

public class DateParserdFromDateToStringException extends GenericException {
    public DateParserdFromDateToStringException(Date date, String pattern){
        super("Cannot parse " + date + ". format this date with this pattern: " + pattern);
    }
}
