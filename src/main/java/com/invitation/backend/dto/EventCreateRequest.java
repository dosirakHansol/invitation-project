package com.invitation.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EventCreateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다")
    private String title;

    @NotNull(message = "이벤트 날짜는 필수입니다")
    private LocalDate eventDate;

    @NotNull(message = "이벤트 시간은 필수입니다")
    private LocalTime eventTime;

    @Size(max = 200, message = "장소는 200자 이하여야 합니다")
    private String location;

    private Double locationLat;

    private Double locationLng;

    @Size(max = 50, message = "템플릿 타입은 50자 이하여야 합니다")
    private String templateType;

    private String customContent;  // JSON 형태
}