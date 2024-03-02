package com.sparta.eroomprojectbe.domain.auth.service;

import com.sparta.eroomprojectbe.domain.auth.dto.*;
import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.auth.repository.AuthRepository;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenge.service.ImageS3Service;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.domain.notification.dto.NotificationRequestDto;
import com.sparta.eroomprojectbe.domain.notification.service.NotificationService;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;
    @Mock
    private ChallengerRepository challengerRepository;
    @Mock
    private ChallengeRepository challengeRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ImageS3Service imageS3Service;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("챌린지 신청 성공 테스트")
    void testCreateChallenger_Success() {
        // Given
        Long challengeId = 1L;
        String file = "testImageUrl";
        Member loginMember = new Member(1L, "test@email.com", "password", "testNickname");
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title1", "IT", "description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge = new Challenge(1L,requestDto,file);
        Member member = new Member("test@email.com", "password", "testNickname");
        Challenger saveChallenger = new Challenger(1L, challenge,member, ChallengerRole.CHALLENGER);
        // Stubbing repository methods
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(memberRepository.findById(loginMember.getMemberId())).thenReturn(java.util.Optional.of(member));
        when(challengerRepository.existsByChallengeAndMember(challenge, member)).thenReturn(false);
        when(challengerRepository.save(any(Challenger.class))).thenReturn(saveChallenger);

        // When
        CreateResponseDto responseDto = authService.createChallenger(challengeId, loginMember);

        // Then
        assertEquals("챌린지 신청 성공", responseDto.getMessage());
        assertEquals(HttpStatus.CREATED, responseDto.getStatus());

        // Verify
        verify(challengeRepository, times(1)).findById(challengeId);
        verify(memberRepository, times(1)).findById(loginMember.getMemberId());
        verify(challengerRepository, times(1)).existsByChallengeAndMember(challenge, member);
        verify(challengerRepository, times(1)).save(any(Challenger.class));
        verify(notificationService, times(1)).send(any(NotificationRequestDto.class));
    }

    // Additional tests for other scenarios (e.g., when challenge is full, member has already applied, etc.) can be added here


    @Test
    @DisplayName("챌린지 인증 등록 성공 테스트")

    void testCreateMemberAuth_Success() {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto("testContents","testVideoUrl","testStatus");
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{});
        Long challengeId = 1L;
        Member loginMember = new Member(1L, "test@email.com", "password", "testNickname");
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title1", "IT", "description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge = new Challenge(1L,requestDto,file.getOriginalFilename());
        Challenger challenger = new Challenger(1L,challenge, loginMember,ChallengerRole.CHALLENGER);
        Auth saveAuth = new Auth(1L,authRequestDto, file.getOriginalFilename(),challenger);
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(memberRepository.findById(loginMember.getMemberId())).thenReturn(Optional.of(loginMember));
        when(challengerRepository.findByChallengeAndMember(challenge, loginMember)).thenReturn(Optional.of(challenger));
        when(authRepository.save(any(Auth.class))).thenReturn(saveAuth);

        // When
        CreateResponseDto responseDto = authService.createMemberAuth(authRequestDto, file, challengeId, loginMember);

        // Then
        assertEquals("챌린지 인증 등록 성공", responseDto.getMessage());
        assertEquals(HttpStatus.CREATED, responseDto.getStatus());

        // Verify
        verify(challengeRepository, times(1)).findById(challengeId);
        verify(memberRepository, times(1)).findById(loginMember.getMemberId());
        verify(challengerRepository, times(1)).findByChallengeAndMember(challenge, loginMember);
        verify(authRepository, times(1)).save(any(Auth.class));
    }

    @Test
    @DisplayName("챌린지 인증 전체 조회 성공 테스트")
    void testGetChallengerAuthList_Success() {
        // Given
        Long challengeId = 1L;
        String file = "testImageUrl";
        Member loginMember = new Member(1L, "test@email.com", "password", "testNickname");
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title1", "IT", "description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge = new Challenge(1L,requestDto,file);
        Challenger challenger = new Challenger(1L,challenge, loginMember, ChallengerRole.CHALLENGER);
        AuthRequestDto authRequestDto1 = new AuthRequestDto("testContents1","testVideoUrl1","testStatus1");
        AuthRequestDto authRequestDto2 = new AuthRequestDto("testContents2","testVideoUrl2","testStatus2");
        Auth auth1 = new Auth(authRequestDto1, "location1", challenger);
        Auth auth2 = new Auth(authRequestDto2, "location2", challenger);

        // Stubbing repository methods
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(memberRepository.findById(loginMember.getMemberId())).thenReturn(Optional.of(loginMember));
        when(challengerRepository.findByChallengeAndMember(challenge, loginMember)).thenReturn(Optional.of(challenger));
        when(authRepository.findByChallenger_ChallengeOrderByModifiedAtDesc(challenge)).thenReturn(List.of(auth1, auth2));

        // When
        AuthAllResponseDto responseDto = authService.getChallengerAuthList(challengeId, loginMember);

        // Then
        assertEquals("인증 전체 조회 성공", responseDto.getMessage());
        assertEquals(HttpStatus.OK, responseDto.getStatus());

        // Verify
        verify(challengeRepository, times(1)).findById(challengeId);
        verify(memberRepository, times(1)).findById(loginMember.getMemberId());
        verify(challengerRepository, times(1)).findByChallengeAndMember(challenge, loginMember);
        verify(authRepository, times(1)).findByChallenger_ChallengeOrderByModifiedAtDesc(challenge);
    }

    @Test
    @DisplayName("챌린지 인증 승인,거부 성공 테스트")
    void updateLeaderAuth() {
        //given
        Long challengeId = 1L;
        String file = "testImageUrl";
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title1", "IT", "description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge = new Challenge(1L,requestDto,file);
        Member loginMember = new Member(1L, "test@email.com", "password", "testNickname");
        Challenger challenger = new Challenger(1L,challenge, loginMember, ChallengerRole.LEADER);
        AuthLeaderRequestDto leaderUpdateRequestDto = new AuthLeaderRequestDto("APPROVED");
        AuthRequestDto authRequestDto = new AuthRequestDto("testContents1","testVideoUrl1","testStatus1");
        Auth auth = new Auth(authRequestDto, file, challenger);
        //Stubbing repository methods
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(authRepository.findById(auth.getAuthId())).thenReturn(Optional.of(auth));
        when(memberRepository.findById(loginMember.getMemberId())).thenReturn(Optional.of(loginMember));
        when(challengerRepository.findByChallengeAndMember(challenge,loginMember)).thenReturn(Optional.of(challenger));
        //when
        AuthDataResponseDto authDataResponseDto = authService.updateLeaderAuth(leaderUpdateRequestDto,challengeId,auth.getAuthId(),loginMember);
        //then
        assertEquals("챌린지 상태 수정 성공",authDataResponseDto.getMessage());
        assertEquals(HttpStatus.OK, authDataResponseDto.getStatus());
        assertEquals(1L,loginMember.getBricksCount());
        assertEquals("APPROVED",authDataResponseDto.getData().getAuthStatus());
        //Verify
        verify(challengeRepository,times(1)).findById(challengeId);
        verify(authRepository,times(1)).findById(auth.getAuthId());
        verify(memberRepository, times(1)).findById(loginMember.getMemberId());
        verify(challengerRepository,times(1)).findByChallengeAndMember(challenge,loginMember);

    }

    @Test
    @DisplayName("챌린지 인증 수정 성공 테스트")
    void updateMemberAuth() throws IOException {
        AuthRequestDto authRequestDto = new AuthRequestDto("authTestContents","authTestVideoUrl",
                "authTestStatus");
        String file = "testFile";
        ChallengeRequestDto requestDto = new ChallengeRequestDto("title1", "IT", "description", "2024-02-28",
                "2024-03-04", "주 1회", (short) 4, "아무거나");
        Challenge challenge = new Challenge(1L,requestDto,file);
        Member loginMember = new Member(1L, "test@email.com", "password", "testNickname");
        Challenger challenger = new Challenger(1L,challenge, loginMember, ChallengerRole.LEADER);
        AuthRequestDto updateAuth = new AuthRequestDto("updateContents","updateVideoUrl","WAITING");
        Auth auth = new Auth(1L,authRequestDto,file,challenger);
        MultipartFile updateImage = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{});

        //Stubbing repository methods
        when(authRepository.findById(anyLong())).thenReturn(Optional.of(auth));
        when(challengeRepository.findById(anyLong())).thenReturn(Optional.of(challenge));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(loginMember));
        when(imageS3Service.updateFile(file,updateImage)).thenReturn(updateImage.getOriginalFilename());
        when(challengerRepository.findByChallengeAndMember(challenge,loginMember)).thenReturn(Optional.of(challenger));
        //when
        AuthDataResponseDto authDataResponseDto = authService.updateMemberAuth(updateAuth,updateImage,1L,1L,loginMember);
        //then
        assertEquals("챌린지 인증 수정 성공",authDataResponseDto.getMessage());
        assertEquals(HttpStatus.OK,authDataResponseDto.getStatus());
        //Verify
        verify(authRepository, times(1)).findById(anyLong());
        verify(challengeRepository, times(1)).findById(anyLong());
        verify(memberRepository, times(1)).findById(anyLong());
        verify(imageS3Service, times(1)).updateFile(file,updateImage);
        verify(challengerRepository, times(1)).findByChallengeAndMember(challenge,loginMember);

    }

    @Test
    @DisplayName("챌린지 인증 삭제 성공 테스트")
    void deleteAuth() {
        //given
        String file = "testFile";
        Member loginMember = new Member(1L, "test@email.com","admin1234!","testNickname");
        ChallengeRequestDto challengeRequestDto = new ChallengeRequestDto("test1","IT",
                "testDescription","2024-03-02","2024-03-30","주 3회",
                (short) 4, "아무거나");
        Challenge challenge = new Challenge(1L, challengeRequestDto,file);
        Challenger challenger = new Challenger(1L,challenge,loginMember,ChallengerRole.CHALLENGER);
        AuthRequestDto requestDto = new AuthRequestDto("testContents","testVideoUrl","testStatus");
        Auth auth = new Auth(1L,requestDto, file,challenger);
        //stubbing repository methods
        when(authRepository.findById(anyLong())).thenReturn(Optional.of(auth));
        when(challengeRepository.findById(anyLong())).thenReturn(Optional.of(challenge));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(loginMember));
        when(challengerRepository.findByChallengeAndMember(challenge,loginMember)).thenReturn(Optional.of(challenger));
        doNothing().when(imageS3Service).deleteFile(anyString());
        //when
        CreateResponseDto createResponseDto = authService.deleteAuth(1L,1L,loginMember);
        //then
        assertEquals("챌린지 인증 삭제 성공",createResponseDto.getMessage());
        assertEquals(HttpStatus.OK,createResponseDto.getStatus());
        //verify
        verify(authRepository, times(1)).delete(auth);
        verify(imageS3Service, times(1)).deleteFile(anyString());
    }
}