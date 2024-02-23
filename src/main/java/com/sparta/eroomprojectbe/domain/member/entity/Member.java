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

    private Long bricksCount;

    public Member(String email, String password, String nickname){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = MemberRoleEnum.USER;
        this.isSocialMember = false;
        this.profileImageUrl ="https://github.com/Eroom-Project/Eroom-Project-FE/assets/151543350/e026bdc5-2a06-4578-b561-739d26097db2";
        this.bricksCount = 0L;
    }

    public Member(String kakaoEmail, String password, String nickname, Long kakaoId, String profileImageUrl) {
        this.email = kakaoEmail;
        this.password = password;
        this.nickname = nickname;
        this.role = MemberRoleEnum.USER;
        this.kakaoId = kakaoId;
        this.isSocialMember = true;
        this.profileImageUrl = profileImageUrl;
        this.bricksCount = 0L;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        this.isSocialMember = true;
        return this;
    }

    public void updateProfile(ProfileRequestDto requestDto, String password, String profileImageUrl) {
        this.email = requestDto.getEmail();
        this.password = password;
        this.nickname = requestDto.getNickname();
        this.profileImageUrl = profileImageUrl;
    }

    public String updateNickname(String nickname) {
        this.nickname = nickname;
        return nickname;
    }
}
