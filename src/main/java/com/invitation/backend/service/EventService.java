package com.invitation.backend.service;

import com.invitation.backend.domain.Event;
import com.invitation.backend.domain.User;
import com.invitation.backend.dto.EventCreateRequest;
import com.invitation.backend.dto.EventResponse;
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

        return eventRepository.findByUserAndDeletedAtIsNull(user)
                .stream()
                .map(EventResponse::from)
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
}