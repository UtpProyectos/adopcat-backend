package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.request.OrderRequest;
import com.adocat.adocat_api.api.dto.response.OrderResponse;
import com.adocat.adocat_api.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 🛒 Crear orden (checkout)
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request, Authentication authentication) {
        UUID userId = extractUserId(authentication);
        return orderService.createOrder(userId, request);
    }

    // 📦 Listar órdenes del usuario logueado
    @GetMapping
    public List<OrderResponse> getUserOrders(Authentication authentication) {
        UUID userId = extractUserId(authentication);
        return orderService.getOrdersByUser(userId);
    }

    // 🔍 Obtener una orden por su ID
    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    // Método auxiliar para extraer el userId del principal (token)
    private UUID extractUserId(Authentication authentication) {
        // 👇 Asumiendo que usas UUID como username (puedes adaptar si usas email)
        return UUID.fromString(authentication.getName());
    }
}
