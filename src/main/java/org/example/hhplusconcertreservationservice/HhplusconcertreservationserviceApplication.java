package org.example.hhplusconcertreservationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling  // 스케줄러 활성화
@EnableAsync       // 비동기 처리 활성화
public class HhplusconcertreservationserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HhplusconcertreservationserviceApplication.class, args);
    }

}
