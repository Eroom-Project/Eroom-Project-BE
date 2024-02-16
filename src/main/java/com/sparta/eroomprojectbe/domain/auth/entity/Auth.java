package com.sparta.eroomprojectbe.domain.auth.entity;

import com.sparta.eroomprojectbe.domain.auth.dto.AuthLeaderRequestDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthRequestDto;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.global.rollenum.AuthRole;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


//    public Auth(AuthRequestDto requestDto, Challenger challenger) {
//        this.authContents= requestDto.getAuthContents();
//        this.authImageUrl= requestDto.getAuthImageUrl();
//        this.authVideoUrl= requestDto.getAuthVideoUrl();
//        this.authStatus= (challenger.getRole()== ChallengerRole.LEADER)? String.valueOf(AuthRole.APPROVED) : requestDto.getAuthStatus();
//        this.challenger=challenger;
//    }

    public Auth(AuthRequestDto requestDto, String saveFile, Challenger challenger) {
        this.authContents= (requestDto.getAuthContents()!= null)?requestDto.getAuthContents():"비어있음";
        this.authImageUrl= saveFile;
        this.authVideoUrl= (requestDto.getAuthVideoUrl()!=null)?requestDto.getAuthVideoUrl():"비디오 Url 없음";
        this.authStatus= (challenger.getRole()== ChallengerRole.LEADER)? String.valueOf(AuthRole.APPROVED) : String.valueOf(AuthRole.WAITING);
        this.challenger=challenger;
    }

    public void update(AuthRequestDto requestDto, String updateFile, Challenger challenger) {
        this.authContents = (requestDto.getAuthContents()!= null)?requestDto.getAuthContents():"비어있음";
        this.authImageUrl = updateFile;
        this.authVideoUrl = (requestDto.getAuthVideoUrl()!=null)?requestDto.getAuthVideoUrl():"비디오 Url 없음";
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
