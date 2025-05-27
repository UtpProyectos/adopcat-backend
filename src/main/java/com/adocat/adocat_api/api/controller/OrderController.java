package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.OrderDto;
import com.adocat.adocat_api.service.interfaces.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @GetMapping
    public List<OrderDto> getAll() {
        return service.getAllOrders();
    }

    @PostMapping
    public OrderDto create(@RequestBody OrderDto dto) {
        return service.createOrder(dto);
    }
}
