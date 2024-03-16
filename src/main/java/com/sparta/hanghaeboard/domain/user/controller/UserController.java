package com.sparta.hanghaeboard.domain.user.controller;

import com.sparta.hanghaeboard.domain.user.dto.CheckResponseDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupRequestDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupResponseDto;
import com.sparta.hanghaeboard.domain.user.service.UserService;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping ("/api")
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/user/signup")
    // ResponseEntity : HTTP 응답을 나타내는 클래스입니다. HTTP 응답의 상태 코드, 헤더 및 본문(body)을 포함합니다.
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body("비밀번호 요청이 잘못되었습니다.");
        }
//        userService.signup(signupRequestDto);
        SignupResponseDto signupResponseDto = userService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(signupResponseDto);
    }

    // 회원 정보 조회
    @GetMapping ("/auth")
    public ResponseEntity<Object> checkInfo (@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest httpServletRequest, @RequestHeader("Authorization") String adminToken) {

        try {
            // userService의 checkInfo 메서드 호출
            CheckResponseDto checkResponseDto = userService.checkInfo(userDetails, adminToken);

            // 정상적인 응답 반환
            return ResponseEntity.status(HttpStatus.OK).body(checkResponseDto);
        } catch (CustomException e) {
            // CustomException이 발생한 경우
            if (e.getErrorCode() == ErrorCode.ADMIN_TOKEN) {
                // 관리자 토큰이 일치하지 않는 경우 UNAUTHORIZED 상태 코드 반환
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 필요합니다.");
            } else {
                // 그 외의 경우에는 INTERNAL_SERVER_ERROR 상태 코드 반환
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
            }
        }


//        CheckResponseDto checkResponseDto = userService.checkInfo(userDetails, adminToken);
//        httpServletRequest.getHeaderNames();
//
//        if (checkResponseDto != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(checkResponseDto);
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원정보가 틀렸습니다.");
    }
//
}
