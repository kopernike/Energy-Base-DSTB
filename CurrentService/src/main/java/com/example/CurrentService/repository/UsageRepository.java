package com.example.CurrentService.repository;

import com.example.CurrentService.repository.UsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UsageRepository extends JpaRepository<UsageEntity, LocalDateTime> {
}
