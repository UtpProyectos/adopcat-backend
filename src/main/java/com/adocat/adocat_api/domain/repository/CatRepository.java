package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CatRepository extends JpaRepository<Cat, String> {
}
