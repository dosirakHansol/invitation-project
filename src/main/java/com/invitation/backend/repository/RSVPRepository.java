package com.invitation.backend.repository;

import com.invitation.backend.domain.Event;
import com.invitation.backend.domain.RSVP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, Long> {

    // 특정 이벤트의 모든 RSVP 조회
    List<RSVP> findByEvent(Event event);

    // 특정 이벤트의 RSVP 개수
    long countByEvent(Event event);
}