package com.invitation.backend.service;

import com.invitation.backend.domain.AuthProvider;
import com.invitation.backend.domain.User;
import com.invitation.backend.dto.SignupRequest;
import com.invitation.backend.dto.UserResponse;
import com.invitation.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        // 1. 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다");
        }

        // 2. User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))  // 비밀번호 암호화
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .provider(AuthProvider.GENERAL)
                .build();

        // 3. DB 저장
        User savedUser = userRepository.save(user);

        // 4. DTO로 변환해서 반환
        return UserResponse.from(savedUser);
    }
}