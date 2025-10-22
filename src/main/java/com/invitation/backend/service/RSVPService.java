package com.invitation.backend.service;

import com.invitation.backend.domain.Event;
import com.invitation.backend.domain.RSVP;
import com.invitation.backend.dto.RSVPCreateRequest;
import com.invitation.backend.dto.RSVPResponse;
import com.invitation.backend.repository.EventRepository;
import com.invitation.backend.repository.RSVPRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RSVPService {

    private final RSVPRepository rsvpRepository;
    private final EventRepository eventRepository;

    // 참석 응답 등록 (비회원 가능)
    @Transactional
    public RSVPResponse createRSVP(String shareLink, RSVPCreateRequest request, HttpServletRequest httpRequest) {
        // 1. 공유 링크로 이벤트 조회
        Event event = eventRepository.findByShareLink(shareLink)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 2. 삭제된 이벤트 체크
        if (event.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 초대장입니다");
        }

        // 3. IP 주소 추출
        String ipAddress = getClientIp(httpRequest);

        // 4. RSVP 생성
        RSVP rsvp = RSVP.builder()
                .event(event)
                .guestName(request.getGuestName())
                .attendance(request.getAttendance())
                .companionCount(request.getCompanionCount())
                .phone(request.getPhone())
                .email(request.getEmail())
                .message(request.getMessage())
                .ipAddress(ipAddress)
                .build();

        RSVP savedRSVP = rsvpRepository.save(rsvp);

        return RSVPResponse.from(savedRSVP);
    }

    // 이벤트의 RSVP 목록 조회 (작성자만)
    public List<RSVPResponse> getRSVPList(String username, Long eventId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 작성자 확인
        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("접근 권한이 없습니다");
        }

        return rsvpRepository.findByEvent(event)
                .stream()
                .map(RSVPResponse::from)
                .collect(Collectors.toList());
    }

    // RSVP 수정
    @Transactional
    public RSVPResponse updateRSVP(Long rsvpId, RSVPCreateRequest request) {
        RSVP rsvp = rsvpRepository.findById(rsvpId)
                .orElseThrow(() -> new IllegalArgumentException("응답을 찾을 수 없습니다"));

        rsvp.update(request.getAttendance(), request.getCompanionCount(), request.getMessage());

        return RSVPResponse.from(rsvp);
    }

    // RSVP 삭제
    @Transactional
    public void deleteRSVP(Long rsvpId) {
        RSVP rsvp = rsvpRepository.findById(rsvpId)
                .orElseThrow(() -> new IllegalArgumentException("응답을 찾을 수 없습니다"));

        rsvpRepository.delete(rsvp);
    }

    // 클라이언트 IP 추출
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}