package com.example.demo.shared.exceptions;

public class DateParserdFromStringToDateException extends GenericException {
    public DateParserdFromStringToDateException(String dateString, String pattern){
        super("Cannot parse " + dateString + ". format this date with this pattern: " + pattern);
    }
}
