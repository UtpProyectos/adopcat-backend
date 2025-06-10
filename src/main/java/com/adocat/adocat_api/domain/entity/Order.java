package com.adocat.adocat_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;
    private String shippingAddress;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private String trackingCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
