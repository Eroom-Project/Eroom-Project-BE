package com.sparta.eroomprojectbe.domain.myroom.repository;

import com.sparta.eroomprojectbe.domain.myroom.entity.Myroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyroomRepository extends JpaRepository<Myroom, Long> {
    Myroom findByMemberId(Long memberId);
}
