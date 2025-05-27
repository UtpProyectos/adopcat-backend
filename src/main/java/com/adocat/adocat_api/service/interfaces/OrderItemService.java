package com.adocat.adocat_api.service.interfaces;

import com.adocat.adocat_api.api.dto.OrderItemDto;
import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    List<OrderItemDto> getItemsByOrder(UUID orderId);
    OrderItemDto createItem(OrderItemDto dto);
}
