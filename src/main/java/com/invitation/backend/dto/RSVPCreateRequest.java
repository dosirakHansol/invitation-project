package com.invitation.backend.dto;

import com.invitation.backend.domain.AttendanceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RSVPCreateRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String guestName;

    @NotNull(message = "참석 여부는 필수입니다")
    private AttendanceType attendance;

    @NotNull(message = "동반 인원 수는 필수입니다")
    @Min(value = 0, message = "동반 인원 수는 0 이상이어야 합니다")
    private Integer companionCount;

    @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
    private String phone;

    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;

    @Size(max = 500, message = "메시지는 500자 이하여야 합니다")
    private String message;
}