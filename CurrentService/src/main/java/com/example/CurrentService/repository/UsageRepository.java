package com.example.CurrentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface UsageRepository extends JpaRepository<UsageEntity, LocalDateTime> {}
