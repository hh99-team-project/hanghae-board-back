package com.sparta.hanghaeboard.domain.user.service;

import com.sparta.hanghaeboard.domain.user.dto.CheckResponseDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupRequestDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupResponseDto;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.domain.user.entity.UserRoleEnum;
import com.sparta.hanghaeboard.domain.user.repository.UserRepository;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    // 이메일 패턴
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+$";
    // 비밀번호 패턴
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";

    // 회원 가입
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();  // requestDto 에서 getUsername 가져와 변수 username 에 담음.
        String password = passwordEncoder.encode(signupRequestDto.getPassword());  // 평문을 암호화 해서 password 에 담음.

        // 이메일 주소 패턴 확인
        if (!validateEmailPattern(email)) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_PATTERN);
        }

        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 비밀번호 패턴 확인
        if (!validatePasswordPattern(signupRequestDto.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_PATTERN);
        }

        // 사용자 ROLE 확인 (권한확인)
//        UserRoleEnum role = UserRoleEnum.USER;
        UserRoleEnum role;

        // adminToken에 입력된 내용이 있다면 관리자 암호와 일치 여부 확인
        if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
            role = UserRoleEnum.USER;
            throw new CustomException(ErrorCode.ADMIN_TOKEN);
        } else {
            role = UserRoleEnum.REPORTER;
        }


        // 사용자 등록
        User user = new User(signupRequestDto, password, role);
        return new SignupResponseDto(userRepository.save(user));

    }

    // 이메일 패턴 검증 메소드
    private boolean validateEmailPattern(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 비밀번호 패턴 검증 메소드
    private boolean validatePasswordPattern(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // 새로고침
    @Transactional
    public CheckResponseDto checkInfo(UserDetailsImpl userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXIST_USER));

        CheckResponseDto checkResponseDto = null;
        if (user.getRole() == UserRoleEnum.REPORTER || user.getRole() == UserRoleEnum.USER) {
            checkResponseDto = new CheckResponseDto(user.getId(), user.getNickname(), user.getRole());
        }

        return checkResponseDto;

    }
}
