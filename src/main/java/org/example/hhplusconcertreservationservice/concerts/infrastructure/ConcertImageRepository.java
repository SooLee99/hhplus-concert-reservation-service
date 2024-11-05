package org.example.hhplusconcertreservationservice.concerts.infrastructure;

import org.example.hhplusconcertreservationservice.concerts.domain.ConcertImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertImageRepository extends JpaRepository<ConcertImage, Long> {
    /**
     * concertId를 기반으로 모든 이미지 URL을 조회하는 쿼리.
     *
     * @param concertId 공연 ID
     * @return List<String> 이미지 URL 리스트
     */
    @Query("SELECT ci.imageUrl FROM ConcertImage ci WHERE ci.concertId = :concertId")
    List<String> findImageUrlsByConcertId(@Param("concertId") Long concertId);

}