package study.loginstudy.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.loginstudy.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String password;
    @Column(unique = true)
    private String nickname;
    private String phoneNumber;  // 전화번호를 문자열로 변경
    private LocalDate birthDate;
    private String gender;
    private String job;
    private String home;
    private String school;
    private String username;
    private String profilePicture; // 프로필 사진 경로
    private String introduction; // 한줄 소개

    private UserRole role;

    // OAuth 로그인에 사용
    private String provider;
    private String providerId;

    @OneToMany(mappedBy = "sender")
    @JsonBackReference
    private List<FriendRequest> sentRequests;

    @OneToMany(mappedBy = "receiver")
    @JsonBackReference
    private List<FriendRequest> receivedRequests;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordResetToken passwordResetToken;

    // Getters and setters

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<FriendRequest> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(List<FriendRequest> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public List<FriendRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(List<FriendRequest> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }
}
