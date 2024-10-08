package study.loginstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class LoginStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginStudyApplication.class, args);
    }

}
