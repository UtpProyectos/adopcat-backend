package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser_UserId(UUID userId);  // Buscar órdenes por ID de usuario
}
