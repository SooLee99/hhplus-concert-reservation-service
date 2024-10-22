package org.example.hhplusconcertreservationservice.users.application.common;

import org.example.hhplusconcertreservationservice.users.application.exception.QueueEntryNotFoundException;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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
        List<Queue> finishedUsers = queueRepository.findAllByStatusOrderByIssuedTimeAsc(QueueStatus.FINISHED);

        if (finishedUsers.isEmpty()) {
            return OptionalDouble.empty();  // 처리된 사용자가 없을 경우
        }

        // 각 사용자의 처리 시간을 계산 (입장 완료 시점 - 대기 시작 시점)
        return finishedUsers.stream()
                .mapToLong(queue -> Duration.between(queue.getActivationTime(), queue.getExpirationTime()).toMinutes())
                .average();  // 평균 처리 시간 계산
    }

    // 사용자의 예상 대기 시간을 계산하는 메서드
    @Transactional(readOnly = true)
    public Duration calculateEstimatedWaitTime(Long userId) {
        List<Queue> waitingUsers = queueRepository.findAllByStatusInOrderByIssuedTimeAsc(List.of(QueueStatus.WAITING, QueueStatus.PROCESSING));
        OptionalDouble averageProcessingTime = calculateAverageProcessingTime();
        long averageTime = averageProcessingTime.isPresent() ? (long) averageProcessingTime.getAsDouble() : 2;  // 기본값은 2분

        // 유저의 큐 정보를 찾음 (없을 수 있음)
        Optional<Queue> userQueueOpt = queueRepository.findByUserId(userId);

        if (userQueueOpt.isPresent()) {
            Queue userQueue = userQueueOpt.get();
            if (userQueue.getStatus() == QueueStatus.FINISHED) {
                // 입장 완료 이후 5분 후 만료 처리
                LocalDateTime now = LocalDateTime.now();
                if (userQueue.getExpirationTime() != null && userQueue.getExpirationTime().isAfter(now)) {
                    return Duration.between(now, userQueue.getExpirationTime());
                } else {
                    return Duration.ZERO;
                }
            }

            // 유저의 위치를 계산하고, 대기 시간을 추정
            for (int i = 0; i < waitingUsers.size(); i++) {
                if (waitingUsers.get(i).getUserId().equals(userId)) {
                    int position = i + 1;
                    return Duration.ofMinutes(position * averageTime);  // 동적으로 계산된 평균 처리 시간 사용
                }
            }
        }

        // 유저가 대기열에 없을 경우 예상 대기 시간 반환 (예: 신규 사용자)
        return Duration.ofMinutes(averageTime * (waitingUsers.size() + 1));
    }
}
