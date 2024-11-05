package org.example.hhplusconcertreservationservice.users.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hhplusconcertreservationservice.users.domain.Queue;
import org.example.hhplusconcertreservationservice.users.infrastructure.QueueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        queueRepository.deleteAll();
    }

    @DisplayName("대기열 토큰 발급 API 성공 테스트")
    @Test
    void whenIssueToken_thenReturnsQueueResponse() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(post("/api/v1/queue/issue-token")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queueId").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.queueToken").exists())
                .andExpect(jsonPath("$.status").exists());

        // 데이터베이스에 대기열 정보가 저장되었는지 확인
        Optional<Queue> queueOptional = queueRepository.findActiveQueueByUserId(userId);
        assertThat(queueOptional).isPresent();
        assertThat(queueOptional.get().getUserId()).isEqualTo(userId);
    }

    @DisplayName("이미 토큰이 발급된 유저에 대한 토큰 발급 시도 시 오류 테스트")
    @Test
    void whenIssueTokenForExistingUser_thenReturnsError() throws Exception {
        // given
        Long userId = 1L;

        // 먼저 토큰 발급
        mockMvc.perform(post("/api/v1/queue/issue-token")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 동일한 유저로 다시 토큰 발급 시도
        mockMvc.perform(post("/api/v1/queue/issue-token")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 활성화된 토큰이 존재합니다."));
    }

    @DisplayName("대기열 상태 조회 API 성공 테스트")
    @Test
    void whenGetQueueStatus_thenReturnsQueueStatus() throws Exception {
        // given
        Long userId = 2L;

        // 토큰 발급
        mockMvc.perform(post("/api/v1/queue/issue-token")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(get("/api/v1/queue/queue-status")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.queuePosition").isNumber())
                .andExpect(jsonPath("$.estimatedWaitTime").exists());
    }

    @DisplayName("다수의 유저에 대한 대기열 토큰 발급 및 상태 조회 통합 테스트")
    @Test
    void whenMultipleUsersIssueTokenAndCheckStatus_thenAllOperationsSucceed() throws Exception {
        // given
        Long userId1 = 4L;
        Long userId2 = 5L;
        Long userId3 = 6L;

        // 유저 1 토큰 발급
        mockMvc.perform(post("/api/v1/queue/issue-token")
                        .param("userId", userId1.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 유저 2 토큰 발급
        mockMvc.perform(post("/api/v1/queue/issue-token")
                        .param("userId", userId2.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 유저 3 토큰 발급
        mockMvc.perform(post("/api/v1/queue/issue-token")
                        .param("userId", userId3.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 각 유저의 대기열 상태 조회 및 순번 확인
        mockMvc.perform(get("/api/v1/queue/queue-status")
                        .param("userId", userId1.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(1));

        mockMvc.perform(get("/api/v1/queue/queue-status")
                        .param("userId", userId2.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(2));

        mockMvc.perform(get("/api/v1/queue/queue-status")
                        .param("userId", userId3.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queuePosition").value(3));
    }
}