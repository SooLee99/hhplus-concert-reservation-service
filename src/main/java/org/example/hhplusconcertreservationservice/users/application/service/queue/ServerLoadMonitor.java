package org.example.hhplusconcertreservationservice.users.application.service.queue;

import com.sun.management.OperatingSystemMXBean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;

/**
 * 서버 상태를 모니터링하고 최대 수용 가능 인원 수를 조정하는 클래스
 */
@Getter
@Slf4j
@Service
public class ServerLoadMonitor {

    private int maxCapacity = 30;  // 최대 수용 가능 인원 수

    /**
     * 서버 상태에 따라 최대 예약 인원을 주기적으로 조정하는 메서드
     * 1분마다 서버 상태를 확인하여 최대 수용 인원을 조정
     */
    @Scheduled(cron = "0 * * * * *")  // 매 분마다 실행
    public void monitorServerLoad() {
        adjustMaxCapacityBasedOnServerLoad();
    }

    /**
     * 서버 상태에 따라 최대 예약 인원을 결정하는 메서드
     */
    public void adjustMaxCapacityBasedOnServerLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double systemCpuLoad = osBean.getCpuLoad(); // 0.0 ~ 1.0 (0% ~ 100%)

        if (systemCpuLoad > 0.7) {  // CPU 사용률이 70% 이상일 경우 최대 인원을 줄임
            maxCapacity = Math.max(10, maxCapacity - 5);
        } else if (systemCpuLoad < 0.4) {  // CPU 사용률이 40% 이하일 경우 최대 인원을 늘림
            maxCapacity = Math.min(50, maxCapacity + 5);
        }

        log.info("서버 상태에 따른 최대 인원 수 조정: {}", maxCapacity);
    }
}
