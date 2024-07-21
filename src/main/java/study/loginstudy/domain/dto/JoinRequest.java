package study.loginstudy.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.loginstudy.domain.UserRole;
import study.loginstudy.domain.entity.User;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "닉네임이 비어있습니다.")
    private String nickname;

    //@NotBlank(message = "전화번호가 비어있습니다.")
    private int phoneNumber;

    private int birthDate;
    private String gender;
    private String job;
    private String home;
    private String school;




    // 비밀번호 암호화 X
    public User toEntity() {
        return User.builder()
                .loginId(this.loginId)
                .password(this.password)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .birthDate(this.birthDate)
                .gender(this.gender)
                .job(this.job)
                .home(this.home)
                .school(this.school)
                .role(UserRole.USER)
                .build();
    }

    // 비밀번호 암호화
    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .birthDate(this.birthDate)
                .gender(this.gender)
                .job(this.job)
                .home(this.home)
                .school(this.school)
                .role(UserRole.USER)
                .build();
    }
}
