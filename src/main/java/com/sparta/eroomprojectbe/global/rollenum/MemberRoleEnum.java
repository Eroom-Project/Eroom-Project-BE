package com.sparta.eroomprojectbe.global.rollenum;

import lombok.Getter;

@Getter
public enum MemberRoleEnum {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    MemberRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }
}
