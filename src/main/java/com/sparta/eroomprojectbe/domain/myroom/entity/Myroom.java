package com.sparta.eroomprojectbe.domain.myroom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
public class Myroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private Long memberId;
    private String name;
    private LocalDate creationDate;
    private String floor;
    private String wall;

    public Myroom (){
        this.floor = "https://files.slack.com/files-tmb/T01L2TNGW3T-F06JYEWHGDC-76c0db4a17/brickbackground_480.jpg";
    }
}
