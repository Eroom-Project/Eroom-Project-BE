package com.sparta.eroomprojectbe.domain.challenge.service;


import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.CreateResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengerRepository challengerRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ImageS3Service imageS3Service;

    @InjectMocks
    private ChallengeService challengeService;
    @Test
    @DisplayName("챌린지 생성 성공 테스트")
    void createChallenge_successful() throws IOException {
        // given
        //가짜 객체
        // 멤버 생성
        Member member = new Member(1L,"test@email.com","admin1234!","testNickname");
        // 챌린지 리퀘스트
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title", "category", "decription", "2024-02-28",
                "2024-03-04","주 1회",(short) 4,"아무거나");
        //파일
        MultipartFile file = mock(MultipartFile.class);
        // 챌린지 생성
        Challenge challenge = new Challenge(requestDto,"https://example.com/image.jpg");
        // 저장된 챌린지
        Challenge saveChallenge = new Challenge(1L, requestDto,"https://example.com/image.jpg");

        // 가짜 메서드 객체
        when(imageS3Service.saveFile(any(MultipartFile.class))).thenReturn("https://example.com/image.jpg");
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(challengeRepository.save(any(Challenge.class))).thenReturn(saveChallenge);

        // when
        CreateResponseDto responseDto = challengeService.createChallenge(requestDto, file, member);

        // then
        assertEquals("챌린지 이룸 생성 성공", responseDto.getMessage());
        assertEquals(HttpStatus.CREATED, responseDto.getStatus());

    }
    // Add more test cases as needed
}