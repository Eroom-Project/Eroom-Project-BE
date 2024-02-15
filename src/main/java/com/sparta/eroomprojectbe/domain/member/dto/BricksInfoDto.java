package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.myroom.entity.Bricks;

import java.time.LocalDate;

public class BricksInfoDto {
    private Long brickId;
    private Long roomId;
    private String type;
    private LocalDate acquisitionDate;

    public BricksInfoDto(Bricks brick) {
        this.brickId = brick.getBricksId();
        this.roomId = brick.getRoomId();
        this.type = brick.getType();
        this.acquisitionDate = brick.getAcquisitionDate();
    }
}
