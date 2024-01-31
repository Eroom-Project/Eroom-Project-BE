package com.sparta.eroomprojectbe.domain.challenger.entity;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenger.Role.ChallengerRole;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;

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

}
