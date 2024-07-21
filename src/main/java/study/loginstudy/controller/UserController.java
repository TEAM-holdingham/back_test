package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import study.loginstudy.domain.dto.UserProfile;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.UserService;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String getProfile(Model model) {
        User user = userService.getCurrentUser(); // 현재 로그인된 사용자 정보 가져오기
        UserProfile userProfile = new UserProfile();
        userProfile.setNickname(user.getNickname());
        userProfile.setLoginId(user.getLoginId());
        userProfile.setIntroduction(user.getIntroduction());
        userProfile.setPhoneNumber(String.valueOf(user.getPhoneNumber()));
        userProfile.setBirthDate(user.getBirthDate());
        userProfile.setGender(user.getGender());
        model.addAttribute("userProfile", userProfile);
        return "my_page";
    }

    @PostMapping("/update")
    public String updateProfile(UserProfile userProfile, @RequestParam("profilePicture") MultipartFile profilePicture) {
        try {
            userService.updateUserProfile(userProfile, profilePicture);
        } catch (IOException e) {
            // 에러 처리 로직 추가
            e.printStackTrace();
        }
        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteProfile() {
        userService.deleteAccount(userService.getCurrentUser().getLoginId());
        return "redirect:/logout";
    }
}
