package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM users u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT u FROM users u JOIN FETCH u.role WHERE u.userId = :userId")
    Optional<User> findByIdWithRole(@Param("userId") UUID userId);

    boolean existsByEmail(String email);

}