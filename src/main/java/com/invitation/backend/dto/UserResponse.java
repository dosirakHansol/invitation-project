package com.invitation.backend.dto;

import com.invitation.backend.domain.AuthProvider;
import com.invitation.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private LocalDate birthDate;
    private AuthProvider provider;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .provider(user.getProvider())
                .build();
    }
}