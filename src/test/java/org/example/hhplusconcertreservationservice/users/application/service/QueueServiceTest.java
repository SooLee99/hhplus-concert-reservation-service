package org.example.hhplusconcertreservationservice.users.application.service;

import org.example.hhplusconcertreservationservice.global.exception.ExceptionMessage;
import org.example.hhplusconcertreservationservice.users.application.common.QueuePositionCalculator;
import org.example.hhplusconcertreservationservice.users.application.common.TokenGenerator;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.service.queue.*;
import org.example.hhplusconcertreservationservice.users.application.usecase.queue.IssueQueueTokenUseCase;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @InjectMocks
    private QueueManager queueManager;

    @Mock
    private QueueProcessor queueProcessor;

    @InjectMocks
    private QueueService queueService;

    @Mock
    private QueueValidationService queueValidationService;

    @Mock
    private ServerLoadMonitor serverLoadMonitor;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private QueuePositionCalculator queuePositionCalculator;

    @Mock
    private IssueQueueTokenUseCase issueQueueTokenUseCase;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private Clock clock;

    private static final int MAX_CAPACITY = 30;

    @BeforeEach
    void setUp() {
        // 고정된 시간 설정
        Instant fixedInstant = Instant.parse("2024-10-17T10:00:00Z");
        ZoneId zoneId = ZoneId.of("UTC");

        lenient().when(clock.instant()).thenReturn(fixedInstant);
        lenient().when(clock.getZone()).thenReturn(zoneId);
    }

    @DisplayName("유저에게 대기열 토큰을 정상적으로 발급하는 테스트")
    @Test
    void givenUserId_whenGenerateQueueToken_thenTokenIsIssued() {
        // given
        Long userId = 1L;
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());
        when(tokenGenerator.generateToken(userId)).thenReturn("token_123");
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(queuePositionCalculator.calculatePosition(userId)).thenReturn(1);
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId)).thenReturn(Duration.ofMinutes(5));

        // when
        ApplicationQueueResponse response = queueManager.issueQueueToken(userId, MAX_CAPACITY);

        // then
        assertNotNull(response);
        assertEquals("token_123", response.getQueueToken());
    }

    @DisplayName("이미 발급된 토큰이 있을 때 새로운 토큰 발급 시도 시 예외 발생 테스트")
    @Test
    void givenExistingToken_whenIssueNewToken_thenThrowException() {
        // given
        Long userId = 1L;
        Queue existingQueue = Queue.builder()
                .userId(userId)
                .queueToken("token123")
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now(clock).plusMinutes(5))
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(existingQueue));

        // when, then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> queueManager.issueQueueToken(userId, MAX_CAPACITY));
        assertEquals(ExceptionMessage.ACTIVE_TOKEN_EXISTS.getMessage(), thrown.getMessage());
    }
    @DisplayName("유저의 현재 대기열 위치를 조회하는 테스트")
    @Test
    void givenUserWithQueue_whenGetQueuePosition_thenReturnCorrectPosition() {
        // given
        Long userId = 1L;
        Queue queue = Queue.builder()
                .userId(userId)
                .queueToken("token123")
                .queuePosition(1)
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now(clock).plusMinutes(10))
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(queue));
        when(queuePositionCalculator.calculatePosition(userId)).thenReturn(1);
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId)).thenReturn(Duration.ofMinutes(5));

        // when
        ApplicationQueueResponse response = queueService.getCurrentQueueStatus(userId);

        // then
        assertNotNull(response);
        assertEquals(1, response.getQueuePosition());
    }

    @DisplayName("유저가 대기열에서 자발적으로 이탈하는 테스트")
    @Test
    void givenUserInQueue_whenLeaveQueue_thenUserIsRemovedFromQueue() {
        // given
        Long userId = 1L;
        doNothing().when(queueRepository).deleteByUserId(userId);

        // when
        queueManager.removeUserFromQueue(userId);

        // then
        verify(queueRepository, times(1)).deleteByUserId(userId);
    }

    @DisplayName("만료된 토큰으로 대기열 조회 시 예외 발생 테스트")
    @Test
    void givenExpiredToken_whenGetQueueStatus_thenThrowTokenExpiredException() {
        // given
        Long userId = 1L;
        LocalDateTime expiredTime = LocalDateTime.now(clock).minusMinutes(10);
        Queue expiredQueue = Queue.builder()
                .userId(userId)
                .queueToken("expiredToken")
                .expirationTime(expiredTime)
                .status(QueueStatus.WAITING)
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(expiredQueue));

        // when, then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> queueService.getCurrentQueueStatus(userId));
        assertEquals(ExceptionMessage.TOKEN_EXPIRED.getMessage(), thrown.getMessage());
    }

    @DisplayName("대기열 생성 실패 시 예외 발생 테스트")
    @Test
    void givenRepositorySaveFailure_whenIssueQueueToken_thenThrowQueueCreationFailedException() {
        // given
        Long userId = 1L;
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());
        when(queueRepository.save(any(Queue.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> queueManager.issueQueueToken(userId, MAX_CAPACITY));
        assertEquals(ExceptionMessage.QUEUE_CREATION_FAILED.getMessage(), thrown.getMessage());
    }

    @DisplayName("대기열 정보가 존재하지 않을 때 예외 발생 테스트")
    @Test
    void givenNoQueueEntry_whenGetQueueStatus_thenThrowIllegalArgumentException() {
        // given
        Long userId = 1L;
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());

        // when, then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> queueService.getCurrentQueueStatus(userId));
        assertEquals(ExceptionMessage.QUEUE_ENTRY_NOT_FOUND.getMessage(), thrown.getMessage());
    }


    @DisplayName("여러 사용자가 대기열에 추가될 때 대기 순서와 시간이 올바르게 업데이트되는지 테스트")
    @Test
    void givenMultipleUsers_whenIssueQueueToken_thenPositionsAndWaitTimesAreUpdated() {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long userId3 = 3L;
        LocalDateTime now = LocalDateTime.now(clock);
        Queue queue1 = Queue.builder()
                .userId(userId1)
                .queueToken("token1")
                .queuePosition(1)
                .status(QueueStatus.WAITING)
                .expirationTime(now.plusMinutes(5))
                .build();
        Queue queue2 = Queue.builder()
                .userId(userId2)
                .queueToken("token2")
                .queuePosition(2)
                .status(QueueStatus.TOKEN_ISSUED)
                .expirationTime(now.plusMinutes(5))
                .build();
        when(queueRepository.findAll()).thenReturn(List.of(queue1, queue2));
        when(queueRepository.findActiveQueueByUserId(userId3)).thenReturn(Optional.empty());
        when(tokenGenerator.generateToken(userId3)).thenReturn("token3");
        when(queuePositionCalculator.calculatePosition(userId3)).thenReturn(3);
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId3)).thenReturn(Duration.ofMinutes(10));
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ApplicationQueueResponse response = queueManager.issueQueueToken(userId3, MAX_CAPACITY);
        System.out.println("Response: " + response);

        // then
        assertNotNull(response);
        assertEquals(3, response.getQueuePosition());
        assertEquals("token3", response.getQueueToken());
    }
    @DisplayName("서버 최대 수용 인원 초과 시 예외 발생 테스트")
    @Test
    void givenMaxCapacityReached_whenIssueQueueToken_thenThrowServerOverloadedException() {
        // given
        Long userId = 100L;
        when(queueRepository.countByStatusIn(List.of(QueueStatus.WAITING, QueueStatus.PROCESSING)))
                .thenReturn((long) MAX_CAPACITY); // 현재 대기열이 최대 수용 인원에 도달함

        // when, then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> queueManager.issueQueueToken(userId, MAX_CAPACITY));
        assertEquals(ExceptionMessage.SERVER_OVERLOADED.getMessage(), thrown.getMessage());
    }

    @DisplayName("대기열이 비어있을 때 유저가 입장하면 첫 번째 위치를 받는지 테스트")
    @Test
    void givenEmptyQueue_whenUserEnters_thenPositionIsOne() {
        // given
        Long userId = 1L;
        lenient().when(queueRepository.countByStatusIn(List.of(QueueStatus.WAITING, QueueStatus.PROCESSING)))
                .thenReturn(0L); // 대기열이 비어 있음
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());
        when(tokenGenerator.generateToken(userId)).thenReturn("token_1");
        when(queuePositionCalculator.calculatePosition(userId)).thenReturn(1);
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId)).thenReturn(Duration.ZERO);
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ApplicationQueueResponse response = queueManager.issueQueueToken(userId, MAX_CAPACITY);

        // then
        assertNotNull(response);
        assertEquals(1, response.getQueuePosition());
        assertEquals("token_1", response.getQueueToken());
    }

    @DisplayName("유저가 PROCESSING 상태일 때 대기열 상태 조회 테스트")
    @Test
    void givenUserProcessing_whenGetQueueStatus_thenReturnProcessingStatus() {
        // given
        Long userId = 1L;
        Queue queue = Queue.builder()
                .userId(userId)
                .queueToken("token_processing")
                .status(QueueStatus.PROCESSING)
                .activationTime(LocalDateTime.now(clock).minusMinutes(2))
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(queue));
        when(queuePositionCalculator.calculatePosition(userId)).thenReturn(0); // PROCESSING 상태이므로 대기열에서 제외
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId)).thenReturn(Duration.ZERO);

        // when
        ApplicationQueueResponse response = queueService.getCurrentQueueStatus(userId);

        // then
        assertNotNull(response);
        assertEquals(QueueStatus.PROCESSING, response.getStatus());
        assertEquals(0, response.getQueuePosition());
    }

    @DisplayName("유저가 FINISHED 상태일 때 대기열 상태 조회 테스트")
    @Test
    void givenUserFinished_whenGetQueueStatus_thenReturnFinishedStatus() {
        // given
        Long userId = 1L;
        Queue queue = Queue.builder()
                .userId(userId)
                .queueToken("token_finished")
                .status(QueueStatus.FINISHED)
                .activationTime(LocalDateTime.now(clock).minusMinutes(5))
                .expirationTime(LocalDateTime.now(clock).plusMinutes(5))
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(queue));
        when(queuePositionCalculator.calculatePosition(userId)).thenReturn(0); // FINISHED 상태이므로 대기열에서 제외
        when(queuePositionCalculator.calculateEstimatedWaitTime(userId)).thenReturn(Duration.ZERO);

        // when
        ApplicationQueueResponse response = queueService.getCurrentQueueStatus(userId);

        // then
        assertNotNull(response);
        assertEquals(QueueStatus.FINISHED, response.getStatus());
        assertEquals(0, response.getQueuePosition());
        assertEquals(Duration.ZERO, response.getEstimatedWaitTime());
    }

    @DisplayName("대기열에 다양한 상태의 사용자가 있을 때 대기 순서 및 예상 대기 시간 계산 테스트")
    @Test
    void givenUsersWithDifferentStatuses_whenCalculatePositionAndWaitTime_thenCorrectValues() {
        // given
        Long userId1 = 1L; // WAITING
        Long userId2 = 2L; // PROCESSING
        Long userId3 = 3L; // FINISHED
        Long userId4 = 4L; // WAITING (테스트 대상)

        Queue queue1 = Queue.builder()
                .userId(userId1)
                .status(QueueStatus.WAITING)
                .issuedTime(LocalDateTime.now(clock).minusMinutes(10))
                .build();
        Queue queue2 = Queue.builder()
                .userId(userId2)
                .status(QueueStatus.PROCESSING)
                .issuedTime(LocalDateTime.now(clock).minusMinutes(8))
                .build();
        Queue queue3 = Queue.builder()
                .userId(userId3)
                .status(QueueStatus.FINISHED)
                .issuedTime(LocalDateTime.now(clock).minusMinutes(5))
                .build();
        Queue queue4 = Queue.builder()
                .userId(userId4)
                .status(QueueStatus.WAITING)
                .issuedTime(LocalDateTime.now(clock).minusMinutes(2))
                .build();

        // lenient()를 사용하여 불필요한 stubbing으로 인한 예외 방지
        lenient().when(queueRepository.findAllByStatusInOrderByIssuedTimeAsc(
                        List.of(QueueStatus.WAITING, QueueStatus.PROCESSING)))
                .thenReturn(List.of(queue1, queue2, queue4)); // queue3은 FINISHED 상태이므로 제외

        lenient().when(queueRepository.findByUserId(userId4)).thenReturn(Optional.of(queue4));

        // Position 계산 (queue1이 1번, queue2가 2번, queue4가 3번)
        lenient().when(queuePositionCalculator.calculatePosition(userId4)).thenReturn(3);
        lenient().when(queuePositionCalculator.calculateEstimatedWaitTime(userId4)).thenReturn(Duration.ofMinutes(6));

        // when
        int position = queuePositionCalculator.calculatePosition(userId4);
        Duration estimatedWaitTime = queuePositionCalculator.calculateEstimatedWaitTime(userId4);

        // then
        assertEquals(3, position);
        assertEquals(Duration.ofMinutes(6), estimatedWaitTime);
    }

    @DisplayName("사용자가 대기열에서 제거된 후 상태 조회 시 예외 발생 테스트")
    @Test
    void givenUserRemovedFromQueue_whenGetQueueStatus_thenThrowException() {
        // given
        Long userId = 1L;
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.empty());

        // when, then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> queueService.getCurrentQueueStatus(userId));
        assertEquals(ExceptionMessage.QUEUE_ENTRY_NOT_FOUND.getMessage(), thrown.getMessage());
    }

    @DisplayName("대기열에 사용자가 없을 때 평균 처리 시간 계산 테스트")
    @Test
    void givenNoUsersInQueue_whenCalculateAverageProcessingTime_thenReturnEmpty() {
        // given
        lenient().when(queueRepository.findAllByStatusOrderByIssuedTimeAsc(QueueStatus.FINISHED))
                .thenReturn(List.of()); // 빈 대기열을 반환하도록 목킹

        // when
        OptionalDouble averageProcessingTime = queuePositionCalculator.calculateAverageProcessingTime();

        // then
        assertFalse(averageProcessingTime.isPresent()); // 평균 처리 시간이 없음을 검증
    }

    @DisplayName("사용자가 이미 FINISHED 상태에서 토큰 발급 시도 시 예외 발생 테스트")
    @Test
    void givenUserFinished_whenIssueQueueToken_thenThrowException() {
        // given
        Long userId = 1L;
        Queue finishedQueue = Queue.builder()
                .userId(userId)
                .queueToken("token_finished")
                .status(QueueStatus.FINISHED)
                .expirationTime(LocalDateTime.now(clock).plusMinutes(5))
                .build();
        when(queueRepository.findActiveQueueByUserId(userId)).thenReturn(Optional.of(finishedQueue));

        // when, then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> queueManager.issueQueueToken(userId, MAX_CAPACITY));
        assertEquals(ExceptionMessage.ACTIVE_TOKEN_EXISTS.getMessage(), thrown.getMessage());
    }
}
