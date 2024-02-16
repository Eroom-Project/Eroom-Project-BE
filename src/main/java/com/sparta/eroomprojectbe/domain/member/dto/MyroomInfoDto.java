package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.myroom.entity.Myroom;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MyroomInfoDto {
    private Long roomId;
    private Long memberId;
    private String name;
    private LocalDate creationDate;
    private String floor;
    private String wall;

    public MyroomInfoDto(Myroom myroom) {
        this.roomId = myroom.getRoomId();
        this.memberId = myroom.getMemberId();
        this.name = myroom.getName();
        this.creationDate = myroom.getCreationDate();
        this.floor = myroom.getFloor();
        this.wall = myroom.getWall();
    }
}
