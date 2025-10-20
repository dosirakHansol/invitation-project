package com.invitation.backend.controller;

import com.invitation.backend.dto.EventCreateRequest;
import com.invitation.backend.dto.EventResponse;
import com.invitation.backend.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 초대장 생성
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EventCreateRequest request) {
        EventResponse response = eventService.createEvent(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 내 초대장 목록 조회
    @GetMapping
    public ResponseEntity<List<EventResponse>> getMyEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<EventResponse> responses = eventService.getMyEvents(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    // 초대장 상세 조회 (작성자용)
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long eventId) {
        EventResponse response = eventService.getEvent(userDetails.getUsername(), eventId);
        return ResponseEntity.ok(response);
    }

    // 공유 링크로 조회 (비회원 접근 가능)
    @GetMapping("/share/{shareLink}")
    public ResponseEntity<EventResponse> getEventByShareLink(@PathVariable String shareLink) {
        EventResponse response = eventService.getEventByShareLink(shareLink);
        return ResponseEntity.ok(response);
    }
}