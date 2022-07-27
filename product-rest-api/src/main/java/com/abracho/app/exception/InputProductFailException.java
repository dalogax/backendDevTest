package com.abracho.app.exception;

public class InputProductFailException {

    private String date_Time;
    private int statusError;
    private String error_Cause;
    private String message;

    public String getError_Cause() {
        return error_Cause;
    }

    public void setError_Cause(String error_Cause) {
        this.error_Cause = error_Cause;
    }

    public String getDate_Time() {
        return date_Time;
    }

    public void setDate_Time(String date_Time) {
        this.date_Time = date_Time;
    }

    public int getStatusError() {
        return statusError;
    }

    public void setStatusError(int statusError) {
        this.statusError = statusError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
