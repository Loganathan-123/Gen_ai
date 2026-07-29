package com.exam.repository;

import com.exam.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByUserId(Long userId);
    boolean existsByUserIdAndExamName(Long userId, String examName);
}