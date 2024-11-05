package org.example.hhplusconcertreservationservice.concerts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concerts")
/// Concert 엔티티: 콘서트 정보를 저장하는 엔티티
public class Concert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long concertId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "poster_url", length = 200)
    private String posterUrl;

    private String location;

    private String rating;

    @Builder
    public Concert(Long concertId, String title, String posterUrl, String location, String rating) {
        this.concertId = concertId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.location = location;
        this.rating = rating;
    }
}
