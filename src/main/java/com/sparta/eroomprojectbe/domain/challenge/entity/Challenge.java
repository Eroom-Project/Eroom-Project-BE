package com.sparta.eroomprojectbe.domain.challenge.entity;

import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false)
    private int limitation;

    @Column(nullable = false)
    private String thumbnailImageUrl;


    public Challenge(ChallengeRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.startDate = LocalDate.parse(requestDto.getStartDate());
        this.dueDate = LocalDate.parse(requestDto.getDueDate());
        this.frequency = requestDto.getFrequency();
        this.limitation = requestDto.getLimitation();
        this.thumbnailImageUrl = requestDto.getThumbnailImageUrl();
    }
}
