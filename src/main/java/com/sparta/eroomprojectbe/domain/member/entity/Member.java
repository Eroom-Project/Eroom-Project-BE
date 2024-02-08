package com.sparta.eroomprojectbe.domain.member.entity;

import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
import com.sparta.eroomprojectbe.global.rollenum.UserRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role;

    @Column
    private String profileImageUrl;

    private Long kakaoId;

    public Member(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = MemberRoleEnum.USER;
    }

    public Member(String email, String password, String nickname, Long kakaoId){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = MemberRoleEnum.USER;
        this.kakaoId = kakaoId;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}
