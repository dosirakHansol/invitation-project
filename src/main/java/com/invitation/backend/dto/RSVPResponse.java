package com.invitation.backend.dto;

import com.invitation.backend.domain.AttendanceType;
import com.invitation.backend.domain.RSVP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RSVPResponse {

    private Long id;
    private String guestName;
    private AttendanceType attendance;
    private Integer companionCount;
    private String phone;
    private String email;
    private String message;
    private LocalDateTime createdAt;

    // Entity를 DTO로 변환
    public static RSVPResponse from(RSVP rsvp) {
        return RSVPResponse.builder()
                .id(rsvp.getId())
                .guestName(rsvp.getGuestName())
                .attendance(rsvp.getAttendance())
                .companionCount(rsvp.getCompanionCount())
                .phone(rsvp.getPhone())
                .email(rsvp.getEmail())
                .message(rsvp.getMessage())
                .createdAt(rsvp.getCreatedAt())
                .build();
    }
}