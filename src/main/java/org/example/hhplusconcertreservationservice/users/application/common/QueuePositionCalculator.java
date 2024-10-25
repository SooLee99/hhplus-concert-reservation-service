package org.example.hhplusconcertreservationservice.users.application.common;

import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Component
public class QueuePositionCalculator {

    private final QueueRepository queueRepository;

    public QueuePositionCalculator(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    // 유저의 대기열 위치를 계산하는 메서드
    @Transactional(readOnly = true)
    public int calculatePosition(Long userId) {
        // FINISHED 상태를 제외하고 대기열에서 WAITING과 PROCESSING 상태의 사용자만 가져옴
        List<Queue> waitingUsers = queueRepository.findAllByStatusInOrderByIssuedTimeAsc(List.of(QueueStatus.WAITING, QueueStatus.PROCESSING));

        // 사용자가 이미 대기열에 있는지 확인
        for (int i = 0; i < waitingUsers.size(); i++) {
            if (waitingUsers.get(i).getUserId().equals(userId)) {
                return i + 1; // 1-based index
            }
        }

        // 사용자가 대기열에 없으면, 현재 대기열의 크기 + 1 반환
        return waitingUsers.size() + 1;
    }

    // 동적으로 평균 처리 시간을 계산하는 메서드
    @Transactional(readOnly = true)
    public OptionalDouble calculateAverageProcessingTime() {
        // 완료된 상태의 사용자 리스트를 가져옴
        List<Queue> finishedUsers = queueRepository.findAllByStatusOrderByIssuedTimeAsc(QueueStatus.FINISHED);

        if (finishedUsers.isEmpty()) {
            return OptionalDouble.empty();  // 처리 완료된 사용자가 없을 경우
        }

        // 각 사용자의 처리 시간을 계산 (만료 시간 - 활성화 시간)
        return finishedUsers.stream()
                .mapToLong(queue -> {
                    if (queue.getActivationTime() != null && queue.getExpirationTime() != null) {
                        // 처리 시간을 분으로 계산
                        return Duration.between(queue.getActivationTime(), queue.getExpirationTime()).toMinutes();
                    } else {
                        return 0;  // 활성화 또는 만료 시간이 없는 경우
                    }
                })
                .filter(time -> time > 0)  // 유효한 처리 시간만 필터링
                .average();  // 평균 처리 시간 계산
    }

    // 사용자의 예상 대기 시간을 계산하는 메서드
    @Transactional(readOnly = true)
    public Duration calculateEstimatedWaitTime(Long userId) {
        // FINISHED 상태를 제외하고 WAITING 또는 PROCESSING 상태의 사용자 목록을 시간 순으로 가져옴
        List<Queue> waitingUsers = queueRepository.findAllByStatusInOrderByIssuedTimeAsc(
                List.of(QueueStatus.WAITING, QueueStatus.PROCESSING));

        // 평균 처리 시간을 계산합니다.
        OptionalDouble averageProcessingTime = calculateAverageProcessingTime();
        long averageTime = averageProcessingTime.isPresent() ? (long) averageProcessingTime.getAsDouble() : 2;  // 기본값은 2분

        // 유저의 큐 정보를 가져옵니다.
        Optional<Queue> userQueueOpt = queueRepository.findByUserId(userId);

        if (userQueueOpt.isPresent()) {
            Queue userQueue = userQueueOpt.get();

            // 대기열에서 유저의 위치를 계산합니다.
            for (int i = 0; i < waitingUsers.size(); i++) {
                if (waitingUsers.get(i).getUserId().equals(userId)) {
                    int position = i + 1;  // 1-based index (대기열 내 위치)
                    // 대기 인원에 따른 예상 대기 시간을 반환합니다.
                    return Duration.ofMinutes(position * averageTime);
                }
            }
        }

        // 유저가 대기열에 없을 경우 기본 대기 시간을 반환합니다.
        return Duration.ofMinutes(averageTime * (waitingUsers.size() + 1));
    }
}