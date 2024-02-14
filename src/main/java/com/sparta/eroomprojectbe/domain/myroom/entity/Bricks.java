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
@NoArgsConstructor
public class Bricks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bricksId;
    private Long roomId;
    private String type;
    private LocalDate acquisitionDate;
}
