package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoDto {
    String email;
    boolean isAdmin;
}
