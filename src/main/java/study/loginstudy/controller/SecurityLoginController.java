package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.UserNotFoundException;
import study.loginstudy.domain.UserRole;
import study.loginstudy.domain.dto.JoinRequest;
import study.loginstudy.domain.dto.LoginRequest;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/security-login")
public class SecurityLoginController {

    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public String loginhome(Model model, Authentication auth) {

        if(auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }

        return "loginhome";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {

        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult) {

        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }
        // 닉네임 중복 체크
        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }
        // password와 passwordCheck가 같은지 체크
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "바밀번호가 일치하지 않습니다."));
        }

        if(bindingResult.hasErrors()) {
            return "join";
        }

        userService.join2(joinRequest);
        return "redirect:/security-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/info")
    //@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public String userInfo(Model model, Authentication auth) {

        User loginUser = userService.getLoginUserByLoginId(auth.getName());
        model.addAttribute("user", loginUser);

        return "info";
    }

    @GetMapping("/admin")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/authentication-fail")
    public String authenticationFail() {
        return "errorPage/authenticationFail";
    }

    @GetMapping("/authorization-fail")
    public String authorizationFail() {
        return "errorPage/authorizationFail";
    }
    @GetMapping("/nickname")

    public String getNicknameByLoginId(@RequestParam("loginId") String loginId) {
        try {
            return userService.findNicknameByLoginId(loginId);
        } catch (UserNotFoundException e) {
            return e.getMessage();
        }
    }


    @GetMapping("/my_page")
    public String myPage(Model model, Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
        User user = userService.findByLoginId(loginId); // 유저 서비스에서 사용자 정보 조회
        model.addAttribute("user", user);
        return "my_page"; // my_page.html 템플릿 반환
    }

    @GetMapping("/confirm-delete")
    public String confirmDelete(Model model, Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
        model.addAttribute("loginId", loginId);
        return "confirm_delete";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(HttpSession session, Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
        userService.deleteAccount(loginId);
        session.invalidate();  // 세션 무효화
        return "redirect:/"; // 첫 화면으로 리다이렉트
    }
}
