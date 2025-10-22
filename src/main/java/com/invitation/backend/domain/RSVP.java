package com.invitation.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rsvps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RSVP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 50)
    private String guestName;  // 참석자 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceType attendance;  // 참석/불참

    @Column(nullable = false)
    @Builder.Default
    private Integer companionCount = 0;  // 동반 인원 수

    @Column(length = 20)
    private String phone;  // 연락처 (선택)

    @Column(length = 100)
    private String email;  // 이메일 (선택)

    @Column(length = 500)
    private String message;  // 메시지

    @Column(length = 45)
    private String ipAddress;  // 중복 방지용

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

    // RSVP 수정
    public void update(AttendanceType attendance, Integer companionCount, String message) {
        this.attendance = attendance;
        this.companionCount = companionCount;
        this.message = message;
    }
}