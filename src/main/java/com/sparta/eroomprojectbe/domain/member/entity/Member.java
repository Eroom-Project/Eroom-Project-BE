package com.sparta.eroomprojectbe.domain.member.entity;

import com.sparta.eroomprojectbe.domain.member.dto.ProfileRequestDto;
import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
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

    private Boolean isSocialMember;

    public Member(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = MemberRoleEnum.USER;
        this.profileImageUrl ="https://files.slack.com/files-pri/T01L2TNGW3T-F06K5JP92N4/group_69.png";
    }

    public Member(String kakaoEmail, String password, String nickname, Long kakaoId, boolean b) {
        this.email = kakaoEmail;
        this.password = password;
        this.nickname = nickname;
        this.role = MemberRoleEnum.USER;
        this.kakaoId = kakaoId;
        this.isSocialMember = b;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public void updateProfile(ProfileRequestDto requestDto) {
        this.email = requestDto.getEmail();
        this.password = requestDto.getPassword();
        this.nickname = requestDto.getNickname();
        this.profileImageUrl = requestDto.getProfileImageUrl();
    }
}
