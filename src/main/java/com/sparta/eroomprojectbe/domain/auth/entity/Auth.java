package com.sparta.eroomprojectbe.domain.auth.entity;

import com.sparta.eroomprojectbe.domain.auth.dto.AuthLeaderRequestDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.global.rollenum.AuthRole;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Auth extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column(nullable = false)
    private String authContents;

    @Column
    private String authImageUrl;

    @Column
    private String authVideoUrl;

    @Column
    private String authStatus;

    @ManyToOne
    @JoinColumn(name = "challenger_id")
    private Challenger challenger;


    public Auth(AuthRequestDto requestDto, Challenger challenger) {
        this.authContents= requestDto.getAuthContents();
        this.authImageUrl= requestDto.getAuthImageUrl();
        this.authVideoUrl= requestDto.getAuthVideoUrl();
        this.authStatus= (challenger.getRole()== ChallengerRole.LEADER)? String.valueOf(AuthRole.APPROVED) : requestDto.getAuthStatus();
        this.challenger=challenger;
    }

    public void update(AuthRequestDto requestDto, Challenger challenger) {
        this.authContents = requestDto.getAuthContents();
        this.authImageUrl = requestDto.getAuthImageUrl();
        this.authVideoUrl = requestDto.getAuthVideoUrl();
        this.authStatus= (challenger.getRole()== ChallengerRole.LEADER)? String.valueOf(AuthRole.APPROVED) : requestDto.getAuthStatus();
        this.challenger= challenger;
    }
    public void leaderUpdate(Auth auth, AuthLeaderRequestDto requestDto) {
        this.authContents = auth.getAuthStatus();
        this.authImageUrl = auth.authImageUrl;
        this.authVideoUrl = auth.authVideoUrl;
        this.challenger = auth.challenger;
        this.authStatus = requestDto.getAuthStatus();
    }
}
