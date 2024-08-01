package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.entity.Timer;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.TimerRepository;
import study.loginstudy.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/timer")
public class TimerController {

    @Autowired
    private TimerRepository timerRepository;

    @Autowired
    private UserRepository userRepository;

    private Timer currentTimer;

    @GetMapping
    public String showTimerPage() {
        return "timer";  // 타이머 페이지의 파일명 반환 (확장자 제외)
    }

    @PostMapping("/start")
    @ResponseBody
    public String startTimer() {
        LocalDateTime startTime = LocalDateTime.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Optional<User> optionalUser = userRepository.findByLoginId(loginId);

        if (!optionalUser.isPresent()) {
            return "User not found";
        }

        User user = optionalUser.get();

        if (currentTimer != null && currentTimer.isPaused()) {
            long pauseDurationSeconds = calculateElapsedTime(currentTimer.getPauseTime(), startTime);
            String formattedPauseDuration = formatElapsedTime(pauseDurationSeconds);
            currentTimer.setPauseDuration(formattedPauseDuration);
            currentTimer.setPaused(false);

            timerRepository.save(currentTimer);

            Timer newTimer = new Timer();
            newTimer.setStartTime(startTime);
            newTimer.setPaused(false);
            newTimer.setElapsedTime("00:00:00");
            newTimer.setPauseDuration("00:00:00");
            newTimer.setUser(user);

            timerRepository.save(newTimer);
            currentTimer = newTimer;

        } else {
            currentTimer = new Timer();
            currentTimer.setStartTime(startTime);
            currentTimer.setPaused(false);
            currentTimer.setElapsedTime("00:00:00");
            currentTimer.setPauseDuration("00:00:00");
            currentTimer.setUser(user);

            timerRepository.save(currentTimer);
        }

        return "Timer started successfully";
    }

    @PostMapping("/pause")
    @ResponseBody
    public String pauseTimer(@RequestBody Map<String, String> payload) {
        if (currentTimer != null && !currentTimer.isPaused()) {
            LocalDateTime pauseTime = LocalDateTime.now();
            currentTimer.setPaused(true);
            currentTimer.setPauseTime(pauseTime);

            long elapsedSeconds = calculateElapsedTime(currentTimer.getStartTime(), pauseTime);
            String formattedElapsedTime = formatElapsedTime(elapsedSeconds);

            currentTimer.setElapsedTime(formattedElapsedTime);
            currentTimer.setActivityDescription(payload.get("activityDescription"));
            timerRepository.save(currentTimer);

            return "Timer paused successfully";
        } else {
            return "Timer is already paused or not started";
        }
    }

    @PostMapping("/stop")
    @ResponseBody
    public String stopTimer() {
        if (currentTimer != null) {
            currentTimer.setCompleted(true);
            currentTimer.setEndTime(LocalDateTime.now());
            currentTimer.setStopTime(currentTimer.getEndTime());

            timerRepository.save(currentTimer);
            currentTimer = null;
            return "Timer stopped successfully";
        } else {
            return "Timer is not started";
        }
    }

    private String formatElapsedTime(long elapsedTimeSeconds) {
        long hours = elapsedTimeSeconds / 3600;
        long minutes = (elapsedTimeSeconds % 3600) / 60;
        long seconds = elapsedTimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private long calculateElapsedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime != null && endTime != null ? java.time.Duration.between(startTime, endTime).getSeconds() : 0;
    }
    @PostMapping("/reset")
    @ResponseBody
    public String resetTimer(@RequestBody Map<String, Integer> requestBody) {
        if (currentTimer != null && !currentTimer.isPaused()) {
            int elapsedSeconds = requestBody.getOrDefault("elapsedTime", 0);
            String formattedElapsedTime = formatElapsedTime(elapsedSeconds);

            // 경과 시간 업데이트
            currentTimer.setElapsedTime(formattedElapsedTime);
            currentTimer.setCompleted(true);
            currentTimer.setEndTime(LocalDateTime.now());

            // 타이머 객체 저장
            timerRepository.save(currentTimer);
            currentTimer = null; // currentTimer 초기화

            return "Timer reset and elapsed time saved successfully";
        } else {
            return "Timer is not running, no need to reset";
        }
    }
}
