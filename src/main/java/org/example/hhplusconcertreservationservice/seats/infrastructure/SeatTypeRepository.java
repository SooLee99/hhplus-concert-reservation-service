package org.example.hhplusconcertreservationservice.seats.infrastructure;

import org.example.hhplusconcertreservationservice.seats.domain.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Long> {
    List<SeatType> findByConcertId(Long concertId);

    Optional<SeatType> findBySeatTypeId(Long seatTypeId);
}