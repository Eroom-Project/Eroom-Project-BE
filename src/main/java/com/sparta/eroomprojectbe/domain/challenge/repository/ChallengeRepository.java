package com.sparta.eroomprojectbe.domain.challenge.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query("SELECT c FROM Challenge c " +
            "LEFT join c.challengers ch " +
            "GROUP BY c " +
            "ORDER BY COUNT(ch) DESC")
    List<Challenge> findChallengesOrderedByPopularity();
    List<Challenge> findByCategory(String str);
    List<Challenge> findByCategoryContainingOrTitleContainingOrDescriptionContaining(String category,
                                                                                     String title,
                                                                                     String description);
    List<Challenge> findByOrderByCreatedAtDesc();
}
