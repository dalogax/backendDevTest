package com.abracho.app.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.abracho.app.exception.InputProductFailException;

public class UtilsError {

    public static Map<String, Object> bodyMsg4XX(String errorDetails, HttpStatus code) {
        Map<String, Object> body = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime nowTime = LocalDateTime.now();
        body.put("Status", HttpStatus.NOT_FOUND.value());
        body.put("Error Cause", errorDetails);
        body.put("message", "Product not found");
        body.put("Date_Time", nowTime.format(formatter));
        return body;
    }

    public static InputProductFailException bodyMsg5XX(String errorDetails, HttpStatus code) {
        InputProductFailException inputFail = new InputProductFailException();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime nowTime = LocalDateTime.now();
        inputFail.setStatusError(HttpStatus.INTERNAL_SERVER_ERROR.value());
        inputFail.setError_Cause(errorDetails);
        inputFail.setDate_Time(nowTime.format(formatter));
        inputFail.setMessage("Server Error");
       
       
        return inputFail;
    }

    public static Map<String, Object> bodyMsg(String errorDetails, int status) {
        InputProductFailException inputFail = new InputProductFailException();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime nowTime = LocalDateTime.now();
        inputFail.setError_Cause(errorDetails);
        inputFail.setStatusError(status);
        inputFail.setDate_Time(nowTime.format(formatter));
        return Collections.singletonMap("data", inputFail);
    }
}
