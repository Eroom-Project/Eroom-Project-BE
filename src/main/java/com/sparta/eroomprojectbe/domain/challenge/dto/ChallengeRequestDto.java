package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 챌린지를 생성할때 요청하는 dto
 */

@Getter
public class ChallengeRequestDto {
    private String title;
    private String category;
    private String description;
    private String startDate;
    private String dueDate;
    private String frequency;
    private short limitAttendance;
    private String authExplanation;
    private MultipartFile file;
    // 테스트 코드를 위한 생성자
    public ChallengeRequestDto(String title, String category,String description,
                               String startDate, String dueDate, String frequency,
                               short limitAttendance, String authExplanation){
        this.title = title;
        this.category = category;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.frequency = frequency;
        this.limitAttendance = limitAttendance;
        this.authExplanation = authExplanation;
    }

}
