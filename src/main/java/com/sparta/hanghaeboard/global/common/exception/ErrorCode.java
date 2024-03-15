package com.sparta.hanghaeboard.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NOT_EXIST_POST("NOT_EXIST_POST", "해당 글은 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_IMG("NOT_EXIST_IMG", "해당 사진이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_EMAIL("DUPLICATED_EMAIL", "중복된 이메일입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_USER("NOT_EXIST_USER", "해당 유저는 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCH_PWD("NOT_MATCH_PWD", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    AUTHORITY_ACCESS("AUTHORITY_ACCESS", "접근 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    VALIDATION_ERROR("VALIDATION_ERROR", "잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다. ADMIN에게 문의하세요.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("UNAUTHORIZED", "로그인 후 이용할 수 있습니다. 계정이 없다면 회원 가입을 진행해주세요.", HttpStatus.UNAUTHORIZED),
    NOT_EXIST_COMMENT("NOT_EXIST_COMMENT", "해당 댓글은 존재하지 않습니다.", HttpStatus.BAD_REQUEST);


    private final String key;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String key, String message, HttpStatus httpStatus) {
        this.key = key;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

