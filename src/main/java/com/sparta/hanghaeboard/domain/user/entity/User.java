package com.sparta.hanghaeboard.domain.user.entity;

import com.sparta.hanghaeboard.domain.user.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor // Lombok에서 가져옴. 파라미터가 없는 기본 생성자를 만들어준다.
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @Column (nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(SignupRequestDto signupRequestDto, String userPassword, UserRoleEnum role) {
        this.id = getId();
        this.email = signupRequestDto.getEmail();
        this.password = userPassword;
        this.nickname = signupRequestDto.getNickname();
        this.role = role;
    }
}
