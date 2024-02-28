package com.sparta.eroomprojectbe.domain.notification.dto;

import lombok.Getter;

@Getter
public class IsReadResponseDto {
    private Boolean isRead;

    public IsReadResponseDto(Boolean isRead) {
        this.isRead = isRead;
    }
}
