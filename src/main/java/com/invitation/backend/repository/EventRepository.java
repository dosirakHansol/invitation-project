package com.invitation.backend.repository;

import com.invitation.backend.domain.Event;
import com.invitation.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // 공유 링크로 조회
    Optional<Event> findByShareLink(String shareLink);

    // 사용자의 이벤트 목록 (삭제되지 않은 것만)
    List<Event> findByUserAndDeletedAtIsNull(User user);

    // 사용자의 삭제된 이벤트 목록 (휴지통)
    List<Event> findByUserAndDeletedAtIsNotNull(User user);

    // 특정 이벤트 조회 (삭제되지 않은 것만)
    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}