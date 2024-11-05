package org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hhplusconcertreservationservice.reservations.domain.entity.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateReservationRequest {

    @NotNull
    private String token;  // 유저 토큰


    @NotNull
    private Long seatId;

    @Builder
    public CreateReservationRequest(String token, Long seatId) {
        this.token = token;
        this.seatId = seatId;
    }
}
