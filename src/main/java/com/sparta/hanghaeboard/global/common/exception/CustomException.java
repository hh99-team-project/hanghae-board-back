package com.sparta.hanghaeboard.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
    public String getKey(){
        return errorCode.getKey();
    }
    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
    public HttpStatus getHttpStatus(){
        return errorCode.getHttpStatus();
    }
}