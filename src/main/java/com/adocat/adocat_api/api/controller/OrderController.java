package com.adocat.adocat_api.api.controller;

import com.adocat.adocat_api.api.dto.request.OrderItemRequest;
import com.adocat.adocat_api.api.dto.request.OrderRequest;
import com.adocat.adocat_api.api.dto.response.OrderResponse;
import com.adocat.adocat_api.config.MailService;
import com.adocat.adocat_api.domain.repository.UserRepository;
import com.adocat.adocat_api.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MailService mailService;
    private final UserRepository userRepository;

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
    /*
    // 🧪 Prueba de envío de correo
    @PostMapping("/test-email")
    public ResponseEntity<Void> testOrderEmail() {
        OrderRequest request = new OrderRequest();
        request.setFullName("Usuario de prueba");
        request.setEmail("tucorreo@dominio.com"); // reemplaza por uno real
        request.setPhone("999999999");
        request.setShippingAddress("Av. Gato Feliz 123");
        request.setPaymentMethod("Yape");
        request.setTotal(85.50);

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setName("Collar Antipulgas");
        item1.setQuantity(1);
        item1.setProductId(UUID.randomUUID().toString()); // ✅ Convertir UUID a String

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setName("Juguete Ratón");
        item2.setQuantity(2);
        item2.setProductId(UUID.randomUUID().toString()); // ✅ Convertir UUID a String

        request.setItems(List.of(item1, item2));

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", request.getFullName());
        variables.put("email", request.getEmail());
        variables.put("phone", request.getPhone());
        variables.put("items", request.getItems());
        variables.put("total", request.getTotal());

        mailService.sendHtmlEmail(
                request.getEmail(),
                "📦 Confirmación de tu pedido en AdoCat (Prueba)",
                "order-confirmation",
                variables
        );

        return ResponseEntity.ok().build();
    }
    */
    // 🔐 Extrae el UUID del usuario autenticado por su email en el token
    private UUID extractUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getUserId();
    }
}
