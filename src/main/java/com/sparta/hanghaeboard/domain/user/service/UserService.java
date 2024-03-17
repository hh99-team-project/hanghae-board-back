package com.sparta.hanghaeboard.domain.user.service;

import com.sparta.hanghaeboard.domain.user.dto.CheckResponseDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupRequestDto;
import com.sparta.hanghaeboard.domain.user.dto.SignupResponseDto;
import com.sparta.hanghaeboard.domain.user.entity.User;
import com.sparta.hanghaeboard.domain.user.entity.UserRoleEnum;
import com.sparta.hanghaeboard.domain.user.repository.UserRepository;
import com.sparta.hanghaeboard.global.common.exception.CustomException;
import com.sparta.hanghaeboard.global.common.exception.ErrorCode;
import com.sparta.hanghaeboard.global.user.jwt.JwtUtil;
import com.sparta.hanghaeboard.global.user.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 회원 가입
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();  // requestDto 에서 getUsername 가져와 변수 username 에 담음.
        String password = passwordEncoder.encode(signupRequestDto.getPassword());  // 평문을 암호화 해서 password 에 담음.

        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        // 사용자 ROLE 확인 (권한확인)
        UserRoleEnum role;

        // adminToken에 입력된 내용이 있다면 관리자 암호와 일치 여부 확인
        if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
            role = UserRoleEnum.USER;
        } else {
            role = UserRoleEnum.REPORTER;
        }

        // 사용자 등록
        User user = new User(signupRequestDto, password, role);
        return new SignupResponseDto(userRepository.save(user));

    }

    @Transactional
    public CheckResponseDto checkInfo(UserDetailsImpl userDetails, HttpServletResponse response) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXIST_USER));

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user.getEmail(), user.getRole());

        // HTTP 응답 헤더에 JWT 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        CheckResponseDto checkResponseDto = null;
        if (user.getRole() == UserRoleEnum.REPORTER || user.getRole() == UserRoleEnum.USER) {
            checkResponseDto = new CheckResponseDto(user.getId(), user.getNickname(), user.getRole());
        }

        return checkResponseDto;

    }
}
