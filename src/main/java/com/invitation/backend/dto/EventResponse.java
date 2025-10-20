package com.invitation.backend.dto;

import com.invitation.backend.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String title;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String location;
    private Double locationLat;
    private Double locationLng;
    private String templateType;
    private String customContent;
    private String shareLink;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환
    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime())
                .location(event.getLocation())
                .locationLat(event.getLocationLat())
                .locationLng(event.getLocationLng())
                .templateType(event.getTemplateType())
                .customContent(event.getCustomContent())
                .shareLink(event.getShareLink())
                .viewCount(event.getViewCount())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}