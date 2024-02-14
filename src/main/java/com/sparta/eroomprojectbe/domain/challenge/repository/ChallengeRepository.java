package com.sparta.eroomprojectbe.domain.challenge.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    /**
     * 인기순으로 정렬하기 위한 명령어
     * @return Challenge를 등록한 유저의 숫자에 따라 정렬한다.
     */
    @Query("SELECT c FROM Challenge c " +
            "LEFT join c.challengers ch " +
            "GROUP BY c " +
            "ORDER BY COUNT(ch) DESC")
    List<Challenge> findChallengesOrderedByPopularity();

    /**
     * 카테고리별 정렬을 위한 명령어
     * @param str IT, 외국어, 수학, 과학, 인문, 예체능
     * @return param으로 받은 값과 일치하는 카테고리를 가진 챌린지
     */
    List<Challenge> findByCategory(String str);

    /**
     * 카테고리,타이틀,설명에 해당 키워드가 있으면 해당 챌린지를 조회하는 명령어
     * @param category IT, 외국어, 수학, 과학, 인문, 예체능
     * @param title 챌린지 제목
     * @param description 챌린지 설명
     * @return param으로 받은 값을 포함하는 챌린지
     */
    List<Challenge> findByCategoryContainingOrTitleContainingOrDescriptionContaining(String category,
                                                                                     String title,
                                                                                     String description);

    /**
     * 작성한 날짜로 정렬하는 명령어
     * @return 작성날짜를 기준으로 내림차순
     */
    List<Challenge> findByOrderByCreatedAtDesc();

}
