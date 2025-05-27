package com.adocat.adocat_api.service.impl;

import com.adocat.adocat_api.api.dto.OrderItemDto;
import com.adocat.adocat_api.domain.entity.OrderItem;
import com.adocat.adocat_api.domain.repository.OrderItemRepository;
import com.adocat.adocat_api.service.interfaces.OrderItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository repository;

    @Override
    public List<OrderItemDto> getItemsByOrder(UUID orderId) {
        return repository.findByOrderId(orderId).stream().map(OrderItemDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public OrderItemDto createItem(OrderItemDto dto) {
        OrderItem item = dto.toEntity();
        item.setItemId(UUID.randomUUID());
        return OrderItemDto.fromEntity(repository.save(item));
    }
}
