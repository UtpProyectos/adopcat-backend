package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.OrderItemDto;
import com.adocat.adocat_api.service.interfaces.OrderItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService service;

    @GetMapping("/{orderId}")
    public List<OrderItemDto> getByOrder(@PathVariable UUID orderId) {
        return service.getItemsByOrder(orderId);
    }

    @PostMapping
    public OrderItemDto create(@RequestBody OrderItemDto dto) {
        return service.createItem(dto);
    }
}
