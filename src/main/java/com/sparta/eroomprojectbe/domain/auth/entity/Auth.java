package com.sparta.eroomprojectbe.domain.auth.entity;

import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
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

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "modifiedAt")
    private LocalDateTime modifiedAt;

//    @ManyToOne
//    @JoinColumn(name = "challenger_id")
//    private Challenger challenger;
}
