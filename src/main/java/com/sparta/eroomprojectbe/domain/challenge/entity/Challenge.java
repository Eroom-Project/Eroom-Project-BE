package com.sparta.eroomprojectbe.domain.challenge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.eroomprojectbe.domain.auth.entity.Timestamped;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Challenge extends Timestamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false, length = 25)
    private String authExplanation;

    @Column(nullable = false)
    private short limitAttendance;

    @Column(nullable = false)
    private short currentAttendance;

    @Column(nullable = false, length = 512)
    private String thumbnailImageUrl;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    @JsonBackReference
    private List<Challenger> challengers;

    public Challenge(ChallengeRequestDto requestDto, String file) {
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.description = requestDto.getDescription();
        this.startDate = LocalDate.parse(requestDto.getStartDate());
        this.dueDate = LocalDate.parse(requestDto.getDueDate());
        this.frequency = requestDto.getFrequency();
        this.authExplanation = requestDto.getAuthExplanation();
        this.limitAttendance = requestDto.getLimitAttendance();
        this.thumbnailImageUrl = file;
    }

    //테스트 코드
    public Challenge(long l, ChallengeRequestDto requestDto, String file) {
        this.challengeId = l;
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.description = requestDto.getDescription();
        this.startDate = LocalDate.parse(requestDto.getStartDate());
        this.dueDate = LocalDate.parse(requestDto.getDueDate());
        this.frequency = requestDto.getFrequency();
        this.authExplanation = requestDto.getAuthExplanation();
        this.limitAttendance = requestDto.getLimitAttendance();
        this.thumbnailImageUrl = file;
    }

    public void update(ChallengeRequestDto requestDto, String file) {
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.description = requestDto.getDescription();
        this.startDate = LocalDate.parse(requestDto.getStartDate());
        this.dueDate = LocalDate.parse(requestDto.getDueDate());
        this.frequency = requestDto.getFrequency();
        this.authExplanation = requestDto.getAuthExplanation();
        this.limitAttendance = requestDto.getLimitAttendance();
        this.thumbnailImageUrl = file;
    }


    public void incrementAttendance() {
        this.currentAttendance++;
    }

}