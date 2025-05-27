package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
