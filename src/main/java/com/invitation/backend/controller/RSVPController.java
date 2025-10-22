package com.invitation.backend.controller;

import com.invitation.backend.dto.RSVPCreateRequest;
import com.invitation.backend.dto.RSVPResponse;
import com.invitation.backend.service.RSVPService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RSVPController {

    private final RSVPService rsvpService;

    // 참석 응답 등록 (비회원 가능)
    @PostMapping("/api/events/share/{shareLink}/rsvp")
    public ResponseEntity<RSVPResponse> createRSVP(
            @PathVariable String shareLink,
            @Valid @RequestBody RSVPCreateRequest request,
            HttpServletRequest httpRequest) {
        RSVPResponse response = rsvpService.createRSVP(shareLink, request, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // RSVP 목록 조회 (작성자만)
    @GetMapping("/api/events/{eventId}/rsvp")
    public ResponseEntity<List<RSVPResponse>> getRSVPList(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long eventId) {
        List<RSVPResponse> responses = rsvpService.getRSVPList(userDetails.getUsername(), eventId);
        return ResponseEntity.ok(responses);
    }

    // RSVP 수정
    @PutMapping("/api/rsvp/{rsvpId}")
    public ResponseEntity<RSVPResponse> updateRSVP(
            @PathVariable Long rsvpId,
            @Valid @RequestBody RSVPCreateRequest request) {
        RSVPResponse response = rsvpService.updateRSVP(rsvpId, request);
        return ResponseEntity.ok(response);
    }

    // RSVP 삭제
    @DeleteMapping("/api/rsvp/{rsvpId}")
    public ResponseEntity<Void> deleteRSVP(@PathVariable Long rsvpId) {
        rsvpService.deleteRSVP(rsvpId);
        return ResponseEntity.noContent().build();
    }
}