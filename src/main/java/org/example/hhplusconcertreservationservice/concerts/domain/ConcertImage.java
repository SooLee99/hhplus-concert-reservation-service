package org.example.hhplusconcertreservationservice.concerts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_images")
/// ConcertImage 엔티티: 콘서트 이미지 정보를 저장하는 엔티티
public class ConcertImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "image_url", nullable = false, length = 300)
    private String imageUrl;

    @Builder
    public ConcertImage(Long concertId, String imageUrl) {
        this.concertId = concertId;
        this.imageUrl = imageUrl;
    }
}
