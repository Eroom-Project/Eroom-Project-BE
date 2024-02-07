package com.sparta.eroomprojectbe.domain.auth.entity;

import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Auth {

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
}
