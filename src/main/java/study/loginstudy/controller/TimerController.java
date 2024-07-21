package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import study.loginstudy.domain.entity.Timer;
import study.loginstudy.repository.TimerRepository;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/timer")
public class TimerController {

    @Autowired
    private TimerRepository timerRepository;

    private Timer currentTimer;

    @GetMapping
    public String showTimerPage() {
        return "timer";  // 타이머 페이지의 파일명 반환 (확장자 제외)
    }

    @PostMapping("/start")
    @ResponseBody
    public String startTimer() {
        LocalDateTime startTime = LocalDateTime.now();

        if (currentTimer != null && currentTimer.isPaused()) {
            // pause_duration 계산
            long pauseDurationSeconds = calculateElapsedTime(currentTimer.getPauseTime(), startTime);
            String formattedPauseDuration = formatElapsedTime(pauseDurationSeconds);
            currentTimer.setPauseDuration(formattedPauseDuration);
            currentTimer.setPaused(false);

            // 현재 타이머 객체 저장
            timerRepository.save(currentTimer);

            // 새로운 타이머 객체 생성 및 저장
            Timer newTimer = new Timer();
            newTimer.setStartTime(startTime);
            newTimer.setPaused(false);
            newTimer.setElapsedTime("00:00:00");
            newTimer.setPauseDuration("00:00:00");

            timerRepository.save(newTimer);
            currentTimer = newTimer; // 새로운 타이머 객체로 갱신

        } else {
            currentTimer = new Timer();
            currentTimer.setStartTime(startTime);
            currentTimer.setPaused(false);

            // elapsed_time 필드 기본값 설정
            currentTimer.setElapsedTime("00:00:00");
            // pause_duration 필드 기본값 설정
            currentTimer.setPauseDuration("00:00:00");

            timerRepository.save(currentTimer);
        }

        return "Timer started successfully";
    }

    @PostMapping("/pause")
    @ResponseBody
    public String pauseTimer() {
        if (currentTimer != null && !currentTimer.isPaused()) {
            LocalDateTime pauseTime = LocalDateTime.now();
            currentTimer.setPaused(true);
            currentTimer.setPauseTime(pauseTime);

            // 경과 시간 계산
            long elapsedSeconds = calculateElapsedTime(currentTimer.getStartTime(), pauseTime);
            String formattedElapsedTime = formatElapsedTime(elapsedSeconds);

            // elapsed_time 업데이트
            currentTimer.setElapsedTime(formattedElapsedTime);

            // 타이머 객체 저장
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

            // 정지 시간만 저장
            currentTimer.setStopTime(currentTimer.getEndTime());

            timerRepository.save(currentTimer);
            currentTimer = null; // currentTimer 초기화
            return "Timer stopped successfully";
        } else {
            return "Timer is not started";
        }
    }

    // 초를 HH:MM:SS 형식의 문자열로 포맷하는 메서드
    private String formatElapsedTime(long elapsedTimeSeconds) {
        long hours = elapsedTimeSeconds / 3600;
        long minutes = (elapsedTimeSeconds % 3600) / 60;
        long seconds = elapsedTimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // 시작 시간과 종료 시간 사이의 경과 시간을 초 단위로 계산하는 메서드
    private long calculateElapsedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime != null && endTime != null ? java.time.Duration.between(startTime, endTime).getSeconds() : 0;
    }
}
