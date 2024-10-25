package org.example.hhplusconcertreservationservice.reservations.interfaces.dto.request;
import lombok.*;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor // 모든 필드에 대한 생성자
public class TokenRequest {

    @NonNull
    private final String token;
}
