package com.sparta.eroomprojectbe.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_VALID_FORMAT(HttpStatus.BAD_REQUEST, "NOT_VALID_FORMAT", "지정된 양식을 사용해주세요."),
    NOT_VALID_ACCESS(HttpStatus.BAD_REQUEST,"NOT_VALID_ACCESS","접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_001", "내부 서버 오류입니다."),

    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER_001", "중복된 이메일입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER_002", "이미 사용 중인 닉네임입니다."),
    NOT_VALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER_003", "비밀번호를 다시 확인해주세요."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "MEMBER_004", "찾을 수 없는 회원입니다."),

    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "VERIFICATION_001", "인증 시간이 초과되었습니다."),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "VERIFICATION_002", "인증 코드를 찾을 수 없습니다."),

    NOT_MATCH_REFRESHTOKEN(HttpStatus.NOT_ACCEPTABLE, "MEMBER_005", "Refresh Token이 일치하지 않습니다."),

    ACCESSTOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "JWT_001", "Access Token이 존재하지 않습니다."),
    INVALID_ACCESSTOKEN(HttpStatus.BAD_REQUEST, "JWT_002", "Access Token이 유효하지 않습니다."),
    EXPIRATION_ACCESSTOKEN(HttpStatus.UNAUTHORIZED, "JWT_003", "Access Token이 만료되었습니다"),
    ACCESSTOKEN_NOT_SUPPORT(HttpStatus.UNAUTHORIZED, "JWT_004", "지원하지 않는 Access Token입니다"),
    UNKNOWN_ACCESSTOKEN_ERROR(HttpStatus.UNAUTHORIZED, "JWT_005", "Access Token 에러입니다"),

    REFRESHTOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "JWT_006", "Refresh Token이 존재하지 않습니다."),
    INVALID_REFRESHTOKEN(HttpStatus.BAD_REQUEST, "JWT_007", "Refresh Token이 유효하지 않습니다."),
    EXPIRATION_REFRESHTOKEN(HttpStatus.BAD_REQUEST, "JWT_008", "Refresh Token이 만료되었습니다"),
    REFRESHTOKEN_NOT_SUPPORT(HttpStatus.UNAUTHORIZED, "JWT_009", "지원하지 않는 Refresh Token입니다"),
    UNKNOWN_REFRESHTOKEN_ERROR(HttpStatus.UNAUTHORIZED, "JWT_010", "Refresh Token 에러입니다"),
    NOT_AUTHORIZED_MEMBER(HttpStatus.BAD_REQUEST, "JWT_011", "인가되지 않은 사용자입니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "JWT_012", "알 수 없는 토큰 에러입니다"),

    SOCIAL_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "SOCIAL_001", "소셜 로그인 오류입니다"),
    DUPLICATED_SOCIAL_EMAIL(HttpStatus.NOT_ACCEPTABLE, "SOCIAL_002", "이미 가입된 계정입니다"),


    NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND,"NOTIFICATION_001","찾을 수 없는 알림입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
