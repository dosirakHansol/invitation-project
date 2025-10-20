package com.invitation.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 작성자

    @Column(nullable = false, length = 100)
    private String title;  // 초대장 제목

    @Column(nullable = false)
    private LocalDate eventDate;  // 이벤트 날짜

    @Column(nullable = false)
    private LocalTime eventTime;  // 이벤트 시간

    @Column(length = 200)
    private String location;  // 장소명

    private Double locationLat;  // 위도

    private Double locationLng;  // 경도

    @Column(length = 50)
    private String templateType;  // 템플릿 종류

    @Column(columnDefinition = "TEXT")
    private String customContent;  // 커스텀 내용 (JSON)

    @Column(unique = true, length = 100)
    private String shareLink;  // 공유 링크

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;  // 조회수

    private LocalDateTime deletedAt;  // 소프트 삭제 시간

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 소프트 삭제
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 복원
    public void restore() {
        this.deletedAt = null;
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }
}