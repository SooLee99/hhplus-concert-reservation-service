package org.example.hhplusconcertreservationservice.users.application.facade;

import lombok.RequiredArgsConstructor;
import org.example.hhplusconcertreservationservice.users.application.dto.response.ApplicationQueueResponse;
import org.example.hhplusconcertreservationservice.users.application.usecase.IssueQueueTokenUseCase;
import org.example.hhplusconcertreservationservice.users.application.usecase.CheckQueuePositionUseCase;
import org.example.hhplusconcertreservationservice.users.interfaces.dto.response.InterfacesQueueResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Facade는 여러 서비스나 유스케이스를 조합하여 단순화된 인터페이스를 컨트롤러 레이어에 제공하는 역할을 합니다. **/
@Service
@RequiredArgsConstructor
public class QueueFacade {

    private final IssueQueueTokenUseCase issueQueueTokenUseCase;
    private final CheckQueuePositionUseCase checkQueuePositionUseCase;

    /**
     * [1. 토큰 발급 API]
     * - 유저에게 대기열 토큰을 발급하는 메서드.
     *
     * @param userId 유저 ID
     * @return InterfacesQueueResponse 대기열 응답
     */
    @Transactional
    public InterfacesQueueResponse issueToken(Long userId) {
        return new InterfacesQueueResponse(issueQueueTokenUseCase.execute(userId));
    }

    /**
     * [2. 대기열 조회 API - 폴링용 API]
     * 유저의 현재 대기열 위치를 조회하는 메서드.
     *
     * @param userId 유저 ID
     * @return QueueResponse 대기열 정보
     */
    public ApplicationQueueResponse getQueueStatus(Long userId) {
        return checkQueuePositionUseCase.execute(userId);
    }
}