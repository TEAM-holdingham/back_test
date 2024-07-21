package study.loginstudy.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.service.UserService;

@Controller
@RequestMapping("/password-reset")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @PostMapping("/request")
    @ResponseBody
    public String requestPasswordReset(@RequestParam String loginId) {
        userService.sendPasswordResetEmail(loginId);
        return "Password reset email sent.";
    }

    @GetMapping("/reset")
    public String showResetForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password-form";
    }

    @PostMapping("/reset")
    @ResponseBody
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        if (userService.resetPassword(token, newPassword)) {
            return "Password successfully reset.";
        } else {
            return "Invalid or expired token.";
        }
    }
}
