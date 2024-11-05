package org.example.hhplusconcertreservationservice.reservations.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ConcertInfoResponse {
    private final String seatTypeName;
    private final double price;
    private final String concertTitle;
    private final String posterUrl;
    private final String location;
    private final double rating;
    private final List<String> imageUrls;
    private final LocalDateTime concertDate;
    private final int duration;
    private final LocalDateTime ticketStartTime;
    private final LocalDateTime ticketEndTime;
}
