package com.sparta.eroomprojectbe.domain.myroom.repository;

import com.sparta.eroomprojectbe.domain.myroom.entity.Bricks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BricksRepository extends JpaRepository<Bricks, Long> {
    List<Bricks> findByRoomId(Long roomId);
}
