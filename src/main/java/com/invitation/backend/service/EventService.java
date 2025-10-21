package com.invitation.backend.service;

import com.invitation.backend.domain.Event;
import com.invitation.backend.domain.User;
import com.invitation.backend.dto.EventCreateRequest;
import com.invitation.backend.dto.EventListProjection;
import com.invitation.backend.dto.EventResponse;
import com.invitation.backend.dto.EventUpdateRequest;
import com.invitation.backend.repository.EventRepository;
import com.invitation.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // 초대장 생성
    @Transactional
    public EventResponse createEvent(String username, EventCreateRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 2. 고유 공유 링크 생성
        String shareLink = generateUniqueShareLink();

        // 3. Event 엔티티 생성
        Event event = Event.builder()
                .user(user)
                .title(request.getTitle())
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .location(request.getLocation())
                .locationLat(request.getLocationLat())
                .locationLng(request.getLocationLng())
                .templateType(request.getTemplateType())
                .customContent(request.getCustomContent())
                .shareLink(shareLink)
                .build();

        // 4. 저장
        Event savedEvent = eventRepository.save(event);

        return EventResponse.from(savedEvent);
    }

    // 내 초대장 목록 조회
    public List<EventResponse> getMyEvents(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // Projection 사용으로 변경
        return eventRepository.findProjectedByUserAndDeletedAtIsNull(user)
                .stream()
                .map(this::projectionToResponse)
                .collect(Collectors.toList());
    }

    // 초대장 상세 조회 (작성자용)
    public EventResponse getEvent(String username, Long eventId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 작성자 확인
        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("접근 권한이 없습니다");
        }

        return EventResponse.from(event);
    }

    // 공유 링크로 조회 (비회원 접근 가능)
    @Transactional
    public EventResponse getEventByShareLink(String shareLink) {
        Event event = eventRepository.findByShareLink(shareLink)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 삭제된 이벤트 체크
        if (event.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 초대장입니다");
        }

        // 조회수 증가
        event.increaseViewCount();

        return EventResponse.from(event);
    }

    // 고유 공유 링크 생성
    private String generateUniqueShareLink() {
        String shareLink;
        do {
            shareLink = UUID.randomUUID().toString().substring(0, 8);
        } while (eventRepository.findByShareLink(shareLink).isPresent());

        return shareLink;
    }

    // 초대장 수정
    @Transactional
    public EventResponse updateEvent(String username, Long eventId, EventUpdateRequest request) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 작성자 확인
        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("접근 권한이 없습니다");
        }

        // 수정
        event.update(request);

        return EventResponse.from(event);
    }

    // 초대장 삭제 (소프트 삭제)
    @Transactional
    public void deleteEvent(String username, Long eventId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 작성자 확인
        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("접근 권한이 없습니다");
        }

        // 소프트 삭제
        event.delete();
    }

    // 휴지통 목록 조회
    public List<EventResponse> getTrashedEvents(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // Projection 사용으로 변경
        return eventRepository.findProjectedByUserAndDeletedAtIsNotNull(user)
                .stream()
                .map(this::projectionToResponse)
                .collect(Collectors.toList());
    }

    // 초대장 복원
    @Transactional
    public EventResponse restoreEvent(String username, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 작성자 확인
        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("접근 권한이 없습니다");
        }

        // 이미 복원된 상태인지 확인
        if (event.getDeletedAt() == null) {
            throw new IllegalArgumentException("이미 복원된 초대장입니다");
        }

        // 복원
        event.restore();

        return EventResponse.from(event);
    }

    // 영구 삭제
    @Transactional
    public void permanentDeleteEvent(String username, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("초대장을 찾을 수 없습니다"));

        // 작성자 확인
        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("접근 권한이 없습니다");
        }

        // 휴지통에 있는지 확인
        if (event.getDeletedAt() == null) {
            throw new IllegalArgumentException("휴지통에 있는 초대장만 영구 삭제할 수 있습니다");
        }

        // 영구 삭제
        eventRepository.delete(event);
    }

    // Projection을 EventResponse로 변환
    private EventResponse projectionToResponse(EventListProjection projection) {
        return EventResponse.builder()
                .id(projection.getId())
                .title(projection.getTitle())
                .eventDate(projection.getEventDate())
                .eventTime(projection.getEventTime())
                .location(projection.getLocation())
                .locationLat(null)
                .locationLng(null)
                .templateType(null)
                .customContent(null)
                .shareLink(projection.getShareLink())
                .viewCount(projection.getViewCount())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .build();
    }
}