package com.sparta.eroomprojectbe.domain.challenger.entity;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Challenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengerId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    private ChallengerRole role;

    public Challenger (Challenge challenge, Member member, ChallengerRole role){
        this.challenge = challenge;
        this.member = member;
        this.role = role;
    }

    public Challenger(Challenge challenge, ChallengerRole challengerRole) {
        this.challenge = challenge;
        this.role = challengerRole;
    }
}
