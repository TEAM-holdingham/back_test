package study.loginstudy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.loginstudy.UserNotFoundException;
import study.loginstudy.config.ProfileProperties;
import study.loginstudy.domain.dto.EmailMessage;
import study.loginstudy.domain.dto.JoinRequest;
import study.loginstudy.domain.dto.LoginRequest;
import study.loginstudy.domain.dto.UserProfile;
import study.loginstudy.domain.entity.PasswordResetToken;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.FriendRequestRepository;
import study.loginstudy.repository.PasswordResetTokenRepository;
import study.loginstudy.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder encoder;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final ProfileProperties profileProperties;

    public User findByNickname(String nickname) {
        Optional<User> userOpt = userRepository.findByNickname(nickname);
        return userOpt.orElse(null);
    }

    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void join(JoinRequest req) {
        userRepository.save(req.toEntity());
    }

    public void join2(JoinRequest req) {
        userRepository.save(req.toEntity(encoder.encode(req.getPassword())));
    }

    public User login(LoginRequest req) {
        Optional<User> optionalUser = userRepository.findByLoginId(req.getLoginId());

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return null;
        }

        return user;
    }

    public User getLoginUserById(Long userId) {
        if (userId == null) return null;

        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElse(null);
    }

    public User getLoginUserByLoginId(String loginId) {
        if(loginId == null) return null;

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    public String findNicknameByLoginId(String loginId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByLoginId(loginId);
        if (userOptional.isPresent()) {
            return userOptional.get().getNickname();
        } else {
            throw new UserNotFoundException("User not found with login ID: " + loginId);
        }
    }

    public void deleteAccount(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        friendRequestRepository.deleteBySenderId(user.getId());
        friendRequestRepository.deleteByReceiverId(user.getId());

        userRepository.delete(user);
    }

    public User findByLoginId(String username) {
        return null;
    }

    public void sendPasswordResetEmail(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("No user found with loginId: " + loginId));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetToken.setUser(user);

        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/password-reset/reset?token=" + token;
        String emailText = "비밀번호 재설정을 위한 이메일입니다. 아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" + resetLink;

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getLoginId())
                .subject("비밀번호 재설정")
                .message(emailText)
                .build();
        emailService.sendMail(emailMessage);
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        return true;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("User not found");
        }

        String loginId = authentication.getName();
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void updateUserProfile(UserProfile userProfile, MultipartFile profilePicture) throws IOException {
        User user = getCurrentUser();

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String filename = UUID.randomUUID().toString() + "_" + profilePicture.getOriginalFilename();
            File destFile = new File(profileProperties.getPath() + filename);
            profilePicture.transferTo(destFile);
            user.setProfilePicture(filename);
        }

        user.setIntroduction(userProfile.getIntroduction());
        if (!userProfile.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(userProfile.getPassword()));
        }

        userRepository.save(user);
    }
}
