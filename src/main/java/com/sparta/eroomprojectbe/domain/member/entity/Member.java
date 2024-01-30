package com.sparta.eroomprojectbe.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String role;

    @Column
    private String profileImageURL;



}
