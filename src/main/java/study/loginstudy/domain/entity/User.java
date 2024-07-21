package study.loginstudy.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.loginstudy.domain.UserRole;

import javax.persistence.*;


@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String password;
    @Column(unique = true)
    private String nickname;
    private int phoneNumber;
    private int birthDate;
    private String gender;
    private String job;
    private String home;
    private String school;

    private UserRole role;

    // OAuth 로그인에 사용
    private String provider;
    private String providerId;

}

