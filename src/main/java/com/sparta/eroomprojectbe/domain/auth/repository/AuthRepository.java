package com.sparta.eroomprojectbe.domain.auth.repository;

import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {

}
