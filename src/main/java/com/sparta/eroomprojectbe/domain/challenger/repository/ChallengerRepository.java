package com.sparta.eroomprojectbe.domain.challenger.repository;

import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {
}
