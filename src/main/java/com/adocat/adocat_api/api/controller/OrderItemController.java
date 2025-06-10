package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.response.OrderItemResponse;
import com.adocat.adocat_api.domain.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;

    // 🔍 Obtener todos los ítems de una orden específica
    @GetMapping("/by-order/{orderId}")
    public List<OrderItemResponse> getItemsByOrder(@PathVariable UUID orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId).stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
    }
}
