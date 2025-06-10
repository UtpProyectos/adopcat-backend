package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.request.OrderRequest;
import com.adocat.adocat_api.api.dto.request.OrderItemRequest;
import com.adocat.adocat_api.api.dto.response.OrderResponse;
import com.adocat.adocat_api.domain.entity.Order;
import com.adocat.adocat_api.domain.entity.OrderItem;
import com.adocat.adocat_api.domain.entity.User;
import com.adocat.adocat_api.domain.repository.OrderRepository;
import com.adocat.adocat_api.domain.repository.ProductRepository;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BigDecimal shippingCost = BigDecimal.valueOf(10.00); // fijo o calculable
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            var product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            BigDecimal price = product.getPrice();
            BigDecimal discount = product.getDiscountPct() != null ? product.getDiscountPct() : BigDecimal.ZERO;
            BigDecimal discountAmount = price.multiply(discount).divide(BigDecimal.valueOf(100));
            BigDecimal unitPrice = price.subtract(discountAmount);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .discountApplied(discountAmount)
                    .subtotal(subtotal)
                    .build();

            items.add(item);
            totalAmount = totalAmount.add(subtotal);
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .shippingCost(shippingCost)
                .totalAmount(totalAmount.add(shippingCost))
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("Pendiente")
                .status("Recibido")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .items(new ArrayList<>()) // temporal, se setea abajo
                .build();

        // Asociar ítems con la orden antes de guardar
        for (OrderItem item : items) {
            item.setOrder(order);
        }
        order.setItems(items);

        return new OrderResponse(orderRepository.save(order));
    }

    @Override
    public List<OrderResponse> getOrdersByUser(UUID userId) {
        return orderRepository.findByUser_UserId(userId).stream()
                .map(OrderResponse::new)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(OrderResponse::new)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }
}
