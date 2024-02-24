package com.sparta.eroomprojectbe.domain.challenger.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Challenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengerId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToMany(mappedBy = "challenger", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Auth> authList;

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
