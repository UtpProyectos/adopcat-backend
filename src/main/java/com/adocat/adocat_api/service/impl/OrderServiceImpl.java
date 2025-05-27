package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.OrderDto;
import com.adocat.adocat_api.domain.entity.Order;
import com.adocat.adocat_api.domain.repository.OrderRepository;
import com.adocat.adocat_api.service.interfaces.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository repository;

    @Override
    public List<OrderDto> getAllOrders() {
        return repository.findAll().stream().map(OrderDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public OrderDto createOrder(OrderDto dto) {
        Order order = dto.toEntity();
        order.setOrderId(UUID.randomUUID());
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(order.getCreatedAt());
        return OrderDto.fromEntity(repository.save(order));
    }
}
