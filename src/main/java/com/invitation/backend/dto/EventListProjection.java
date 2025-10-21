package com.invitation.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface EventListProjection {
    Long getId();
    String getTitle();
    LocalDate getEventDate();
    LocalTime getEventTime();
    String getLocation();
    String getShareLink();
    Long getViewCount();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}