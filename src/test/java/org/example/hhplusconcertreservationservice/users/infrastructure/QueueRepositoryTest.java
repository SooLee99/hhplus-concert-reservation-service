package org.example.hhplusconcertreservationservice.users.infrastructure;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.domain.QueueStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QueueRepositoryTest {

    @Autowired
    private QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        queueRepository.deleteAll();  // 테스트 전 기존 데이터 삭제
    }

    @AfterEach
    void tearDown() {
        queueRepository.deleteAll();
    }

    @BeforeEach
    void cleanDatabaseBefore() {
        queueRepository.deleteAll();
    }

    @AfterEach
    void cleanDatabaseAfter() {
        queueRepository.deleteAll();
    }
    @DisplayName("여러 대기열 저장 시 대기열 정보가 정상적으로 저장되는지 테스트")
    @Test
    void givenMultipleQueues_whenSaveAll_thenQueuesArePersisted() {
        // given: Queue 엔티티 3개 생성
        Queue queue1 = Queue.builder()
                .userId(1L)
                .queueToken("token123")
                .queuePosition(1)
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        Queue queue2 = Queue.builder()
                .userId(2L)
                .queueToken("token456")
                .queuePosition(2)
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .build();

        Queue queue3 = Queue.builder()
                .userId(3L)
                .queueToken("token789")
                .queuePosition(3)
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now().plusMinutes(15))
                .build();

        // when: Queue 엔티티를 저장
        List<Queue> savedQueues = queueRepository.saveAll(List.of(queue1, queue2, queue3));

        // then: Queue 엔티티가 정상적으로 저장됨
        assertThat(savedQueues)
                .hasSize(3)
                .extracting(Queue::getQueueId, Queue::getUserId)
                .containsExactlyInAnyOrder(
                        tuple(savedQueues.get(0).getQueueId(), 1L),
                        tuple(savedQueues.get(1).getQueueId(), 2L),
                        tuple(savedQueues.get(2).getQueueId(), 3L)
                );
    }

    @DisplayName("큐 상태가 FINISHED나 TOKEN_ISSUED일 때 카운팅되지 않는지 확인하는 테스트")
    @Test
    @DirtiesContext
    void givenQueuesWithDifferentStatuses_whenCountByStatusWaiting_thenQueuesWithOtherStatusesAreNotCounted() {
        // given: Queue 엔티티 3개 생성, 각기 다른 상태로 설정 (WAITING, FINISHED, TOKEN_ISSUED)
        Queue queue1 = Queue.builder()
                .userId(1L)
                .queueToken("token123")
                .queuePosition(1)
                .status(QueueStatus.WAITING) // 카운팅 되어야 함
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        Queue queue2 = Queue.builder()
                .userId(2L)
                .queueToken("token456")
                .queuePosition(2)
                .status(QueueStatus.FINISHED) // 카운팅 되면 안됨
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .build();

        Queue queue3 = Queue.builder()
                .userId(3L)
                .queueToken("token789")
                .queuePosition(3)
                .status(QueueStatus.TOKEN_ISSUED) // 카운팅 되면 안됨
                .expirationTime(LocalDateTime.now().plusMinutes(15))
                .build();

        // when: Queue 엔티티를 저장한 후, 상태가 WAITING인 대기열을 카운팅
        queueRepository.saveAll(List.of(queue1, queue2, queue3));
        List<Queue> waitingQueues = queueRepository.findAllByStatusOrderByIssuedTimeAsc(QueueStatus.WAITING);

        // then: WAITING 상태인 대기열만 카운팅됨을 확인
        assertThat(waitingQueues)
                .hasSize(1) // WAITING 상태인 엔티티는 1개여야 함
                .extracting(Queue::getQueueId, Queue::getUserId)
                .containsExactlyInAnyOrder(
                        tuple(queue1.getQueueId(), 1L)
                );
    }

    @DisplayName("유저 ID로 활성화된 대기열을 조회할 수 있는지 테스트")
    @Test
    void givenUserId_whenFindActiveQueueByUserId_thenQueueIsReturned() {
        // given: Queue 엔티티를 생성하여 저장
        queueRepository.save(Queue.builder()
                .userId(1L)
                .queueToken("token123")
                .queuePosition(1)
                .status(QueueStatus.TOKEN_ISSUED)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build());

        // when: 유저 ID로 활성화된 대기열을 조회
        Optional<Queue> queueOptional = queueRepository.findActiveQueueByUserId(1L);

        // then: 해당 유저 ID로 대기열이 조회됨
        assertTrue(queueOptional.isPresent());
        assertEquals(1L, queueOptional.get().getUserId());
    }

    @DisplayName("큐가 비어있을 때 모든 큐 조회 시 비어있는 리스트를 반환하는지 테스트")
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void givenNoQueues_whenFindAll_thenReturnEmptyList() {
        // when: 큐가 없는 상태에서 모든 큐 조회
        List<Queue> queues = queueRepository.findAll();

        // then: 빈 리스트가 반환됨을 확인
        assertThat(queues).isEmpty();
    }

    @DisplayName("동시성 테스트: 여러 사용자가 동시에 대기열에 접근할 때 정상적으로 동작하는지 테스트")
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)      // 각 트랜잭션이 독립적으로 처리되도록 설정
    void whenMultipleUsersAccessQueueConcurrently_thenNoConflicts() throws InterruptedException {
        // given: 동시에 여러 유저가 대기열에 접근하는 상황 설정
        ExecutorService executor = Executors.newFixedThreadPool(10);  // 10개의 스레드 사용
        CountDownLatch latch = new CountDownLatch(10);  // 10개의 스레드가 모두 작업을 완료할 때까지 기다림

        for (int i = 0; i < 10; i++) {
            int userId = i + 1;
            executor.submit(() -> {
                try {
                    queueRepository.save(Queue.builder()
                            .userId((long) userId)
                            .queueToken("token" + userId)
                            .queuePosition(userId)
                            .status(QueueStatus.WAITING)
                            .expirationTime(LocalDateTime.now().plusMinutes(5))
                            .build());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();  // 모든 스레드가 작업을 완료할 때까지 기다림

        // then: 10개의 큐가 정상적으로 저장되었는지 확인
        List<Queue> savedQueues = queueRepository.findAll();
        assertThat(savedQueues).hasSize(10);
    }

    @DisplayName("존재하지 않는 유저 ID로 대기열 조회 시 빈 결과가 반환되는지 테스트")
    @Test
    void givenNonExistentUserId_whenFindActiveQueue_thenReturnEmpty() {
        // when: 존재하지 않는 유저 ID로 대기열 조회
        Optional<Queue> queueOptional = queueRepository.findActiveQueueByUserId(999L);  // 없는 유저 ID

        // then: 결과가 존재하지 않음
        assertThat(queueOptional).isEmpty();
    }

    @DisplayName("유저에게 중복된 토큰 발급 방지 테스트")
    @Test
    void givenUserWithToken_whenSaveDuplicateToken_thenThrowException() {
        // given: 같은 queueToken을 가진 Queue 엔티티 2개 생성
        Queue queue1 = Queue.builder()
                .userId(1L)
                .queueToken("token123") // 중복 토큰
                .queuePosition(1)
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        Queue queue2 = Queue.builder()
                .userId(2L)
                .queueToken("token123") // 중복 토큰
                .queuePosition(2)
                .status(QueueStatus.WAITING)
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .build();

        queueRepository.save(queue1); // 첫 번째 저장

        // when, then: 두 번째 저장 시 중복 토큰으로 인해 예외 발생 확인
        assertThrows(DataIntegrityViolationException.class, () -> {
            queueRepository.saveAndFlush(queue2); // 중복 토큰 저장 시도
        });
    }
}
