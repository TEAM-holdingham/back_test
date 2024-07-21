package study.loginstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.Timer;

public interface TimerRepository extends JpaRepository<Timer, Long> {
    // 추가적인 쿼리 메소드가 필요하면 여기에 정의할 수 있습니다.
}