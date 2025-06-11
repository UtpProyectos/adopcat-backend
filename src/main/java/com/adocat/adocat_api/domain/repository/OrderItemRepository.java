package com.adocat.adocat_api.domain.repository;

import com.adocat.adocat_api.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrder_OrderId(UUID orderId);  // Buscar ítems por ID de orden
}
