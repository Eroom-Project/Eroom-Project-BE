package com.sparta.eroomprojectbe.domain.challenge.service;


import com.sparta.eroomprojectbe.domain.challenge.dto.*;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.Role.CategoryRole;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebAppConfiguration
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
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");
        // 챌린지 리퀘스트
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title", "category", "decription", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        //파일
        MultipartFile file = mock(MultipartFile.class);
        // 챌린지 생성
        Challenge challenge = new Challenge(requestDto, "https://example.com/image.jpg");
        // 저장된 챌린지
        Challenge saveChallenge = new Challenge(1L, requestDto, "https://example.com/image.jpg");
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

    @Test
    @DisplayName("선택한 챌린지 조회 성공 테스트")
    void getChallenge_successful() {
        // given
        Long challengeId = 1L;
        String loginMemberId = "1L";
        String loginMemberProfileImageUrl = "No members logged in";
        String loginMemberNickname = "No members logged in";
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title", "category", "decription", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge = new Challenge(1L, requestDto, "https://example.com/image.jpg");
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(challengerRepository.findCreatorMemberByChallengeId(challengeId)).thenReturn(Optional.of(member));
        when(challengerRepository.findMemberIdsByChallenge(challenge)).thenReturn(Arrays.asList(1L, 2L, 3L));
        // when
        ChallengeLoginResponseDto responseDto = challengeService.getChallenge(challengeId, loginMemberId, loginMemberProfileImageUrl, loginMemberNickname);
        // then
        assertNotNull(responseDto);
        assertNotNull(responseDto.getResponseDto().getChallengeId());
        assertEquals(challengeId, responseDto.getResponseDto().getChallengeId());
        assertEquals(loginMemberId, responseDto.getLoginMemberId());
    }

    @Test
    @DisplayName("인기순으로 챌린지 조회 성공 테스트")
    void getPopularChallenge_successful() {
        // given
        int page = 0;
        int size = 12;
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title", "category", "decription", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge1 = new Challenge(1L, requestDto, "https://example.com/image.jpg");
        Challenge challenge2 = new Challenge(2L, requestDto, "https://example.com/image.jpg");
        List<Challenge> popularChallenges = Arrays.asList(challenge1, challenge2);

        Pageable pageable = PageRequest.of(page, size);
        Page<Challenge> pageDto = new PageImpl<>(popularChallenges);

        when(challengeRepository.findChallengesOrderedByPopularity(pageable)).thenReturn(pageDto);
        when(challengerRepository.findCreatorMemberByChallengeId(anyLong())).thenReturn(Optional.of(member));
        when(challengerRepository.findMemberIdsByChallenge(any(Challenge.class))).thenReturn(Arrays.asList(1L, 2L, 3L));
        // when
        AllResponseDto responseDto = challengeService.getPopularChallenge(page, size);
        // then
        assertEquals("챌린지 인기순으로 조회 성공", responseDto.getMessage());
        assertEquals(HttpStatus.OK, responseDto.getStatus());
        assertNotNull(responseDto.getData());
    }

    @Test
    @DisplayName("카테고리별 챌린지 조회 성공 테스트")
    void getCategoryChallenge_successful() {
        // given
        int page = 0;
        int size = 12;
        CategoryRole categoryRole = CategoryRole.IT;
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");
        ChallengeRequestDto requestDto1 = new ChallengeRequestDto("title1", "IT", "decription", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        ChallengeRequestDto requestDto2 = new ChallengeRequestDto("title1", "cate", "decription", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge1 = new Challenge(1L, requestDto1, "https://example.com/image1.jpg");
        Challenge challenge2 = new Challenge(2L, requestDto2, "https://example.com/image2.jpg");
        List<Challenge> categoryChallenges = new ArrayList<>();
        if (challenge1.getCategory().equals("IT")) {
            categoryChallenges.add(challenge1);
        }
        if (challenge2.getCategory().equals("IT")) {
            categoryChallenges.add(challenge2);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Challenge> pageDto = new PageImpl<>(categoryChallenges);

        when(challengeRepository.findByCategory(anyString(), any(Pageable.class))).thenReturn(pageDto);
        when(challengerRepository.findCreatorMemberByChallengeId(anyLong())).thenReturn(Optional.of(member));

        // when
        AllResponseDto responseDto = challengeService.getCategoryChallenge(categoryRole, page, size);

        // then
        assertEquals("카테고리별로 조회 성공", responseDto.getMessage());
        assertEquals(HttpStatus.OK, responseDto.getStatus());

        Page<ChallengeResponseDto> challengeResponseDtoList = responseDto.getData();
        assertEquals(1, challengeResponseDtoList.getContent().size());
        // Verify
        verify(challengeRepository, times(1)).findByCategory("IT", pageable);
    }

    @Test
    @DisplayName("키워드로 챌린지 조회 성공 테스트")
    void getQueryChallenge_successful() {
        // given
        int page = 0;
        int size = 12;
        String keyword = "1";
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");
        ChallengeRequestDto requestDto1 = new ChallengeRequestDto("title1", "IT", "description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        ChallengeRequestDto requestDto2 = new ChallengeRequestDto("title2", "category", "description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge1 = new Challenge(1L, requestDto1, "https://example.com/image1.jpg");
        Challenge challenge2 = new Challenge(2L, requestDto2, "https://example.com/image2.jpg");

        // 특정 조건을 충족하는 챌린지만 추가
        List<Challenge> queryChallenges = new ArrayList<>();
        if (requestDto1.getTitle().contains(keyword) || requestDto1.getCategory().contains(keyword) || requestDto1.getDescription().contains(keyword)) {
            queryChallenges.add(challenge1);
        }
        if (requestDto2.getTitle().contains(keyword) || requestDto2.getCategory().contains(keyword) || requestDto2.getDescription().contains(keyword)) {
            queryChallenges.add(challenge2);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Challenge> pageDto = new PageImpl<>(queryChallenges);

        // Stubbing
        when(challengeRepository.findByCategoryContainingOrTitleContainingOrDescriptionContaining(keyword, keyword, keyword, pageable))
                .thenReturn(pageDto);
        when(challengerRepository.findCreatorMemberByChallengeId(anyLong())).thenReturn(Optional.of(member));
        when(challengerRepository.findMemberIdsByChallenge(any(Challenge.class))).thenReturn(Arrays.asList(1L, 2L, 3L));

        // when
        AllResponseDto responseDto = challengeService.getQueryChallenge(keyword, page, size);

        // then
        assertEquals("키워드로 챌린지 조회 성공", responseDto.getMessage());
        assertEquals(HttpStatus.OK, responseDto.getStatus());

        Page<ChallengeResponseDto> challengeResponseDtoList = responseDto.getData();
        assertEquals(queryChallenges.size(), challengeResponseDtoList.getContent().size());
    }

    @Test
    @DisplayName("최신순으로 챌린지 조회 성공 테스트")
    void getLatestChallenge_successful() {
        // given
        int page = 0;
        int size = 12;
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title", "category", "decription", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge1 = new Challenge(1L, requestDto, "https://example.com/image.jpg");
        Challenge challenge2 = new Challenge(2L, requestDto, "https://example.com/image.jpg");

        // 현재 시간과 같게 설정 (가장 최근에 생성됨)
        challenge1.setCreatedAt(LocalDateTime.now());

        // 현재 시간보다 1일 전으로 설정
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        challenge2.setCreatedAt(yesterday);

        List<Challenge> latestChallenges = new ArrayList<>();
        latestChallenges.add(challenge1);
        latestChallenges.add(challenge2);
        latestChallenges.sort(Comparator.comparing(Challenge::getCreatedAt).reversed());

        Pageable pageable = PageRequest.of(page, size);
        Page<Challenge> pageDto = new PageImpl<>(latestChallenges);

        when(challengeRepository.findByOrderByCreatedAtDesc(pageable)).thenReturn(pageDto);
        when(challengerRepository.findCreatorMemberByChallengeId(anyLong())).thenReturn(Optional.of(member));
        when(challengerRepository.findMemberIdsByChallenge(any(Challenge.class))).thenReturn(Arrays.asList(1L, 2L, 3L));

        // when
        AllResponseDto responseDto = challengeService.getLatestChallenge(page, size);

        // then
        assertEquals("챌린지 최신순으로 조회 성공", responseDto.getMessage());
        assertEquals(HttpStatus.OK, responseDto.getStatus());

        Page<ChallengeResponseDto> challengeResponseDtoList = responseDto.getData();
        assertEquals(latestChallenges.size(), challengeResponseDtoList.getContent().size());
        // 최신순으로 정렬되어있는지 확인
        for (int i = 0; i < latestChallenges.size(); i++) {
            assertEquals(latestChallenges.get(i).getChallengeId(), challengeResponseDtoList.getContent().get(i).getChallengeId());
        }
    }

    @Test
    @DisplayName("챌린지 수정 성공 테스트")
    void updateChallenge_successful() throws IOException {
        // given
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");

        // 기존 챌린지 정보
        Challenge existingChallenge = new Challenge(1L, new ChallengeRequestDto("Old Title", "Old Category", "Old Description",
                "2024-02-28", "2024-03-04", "주 1회", (short) 4, "Old 아무거나"), "https://example.com/old-image.jpg");

        // 새로운 챌린지 정보
        ChallengeRequestDto newRequestDto = new ChallengeRequestDto("New Title", "New Category", "New Description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "New 아무거나");

        MultipartFile newFile = new MockMultipartFile("new-image.jpg", "new-image.jpg", "image/jpeg", "new image content".getBytes());

        // 가짜 메서드 호출
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(existingChallenge));
        when(challengerRepository.findCreatorMemberByChallengeId(anyLong())).thenReturn(Optional.of(member));
        when(imageS3Service.updateFile(anyString(), any(MultipartFile.class))).thenReturn("https://example.com/new-image.jpg");

        // when
        ChallengeUpdateDto updateDto = challengeService.updateChallenge(1L, newRequestDto, newFile, member);

        // then
        assertNotNull(updateDto);
        assertEquals("챌린지 수정 성공", updateDto.getMessage());
        assertEquals(HttpStatus.OK, updateDto.getStatus());

        ChallengeLoginResponseDto loginResponseDto = updateDto.getData();
        assertNotNull(loginResponseDto);

        ChallengeResponseDto responseDto = loginResponseDto.getResponseDto();
        assertNotNull(responseDto);
        assertEquals("New Title", responseDto.getTitle());
        assertEquals("New Category", responseDto.getCategory());
        assertEquals("New Description", responseDto.getDescription());
        assertEquals("https://example.com/new-image.jpg", responseDto.getThumbnailImageUrl());
        // 다른 필드도 필요에 따라 추가

        assertEquals("1", loginResponseDto.getLoginMemberId());  // member.getMemberId()를 문자열로 변환하여 확인
    }

    @Test
    @DisplayName("챌린지 삭제 성공 테스트")
    void deleteChallenge_successful() {
        // given
        Member member = new Member(1L, "test@email.com", "admin1234!", "testNickname");
        Challenge Challenge = new Challenge(1L, new ChallengeRequestDto("Title", "Category", "Description",
                "2024-02-28", "2024-03-04", "주 1회", (short) 4, "아무거나"), "https://example.com/image.jpg");

        when(challengeRepository.findById(anyLong())).thenReturn(Optional.of(Challenge));
        when(challengerRepository.findCreatorMemberByChallengeId(anyLong())).thenReturn(Optional.of(member));
        doNothing().when(imageS3Service).deleteFile(anyString());

        // when
        CreateResponseDto responseDto = challengeService.deleteChallenge(1L, member);

        // then
        assertEquals("챌린지 이룸 삭제 성공", responseDto.getMessage());
        assertEquals(HttpStatus.OK, responseDto.getStatus());

        // Verify
        verify(challengeRepository, times(1)).delete(Challenge);
        verify(imageS3Service, times(1)).deleteFile(anyString());
    }
}