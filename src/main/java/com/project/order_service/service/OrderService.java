package com.project.order_service.service;

import com.project.order_service.dto.OrderDTO;
import com.project.order_service.entity.Order;
import com.project.order_service.product.Product;
import com.project.order_service.product.ProductServiceGrpc;
import com.project.order_service.repository.OrderRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        // Create a gRPC channel to connect to the Product Service (running in Go)
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()  // Use plaintext if no TLS is used, otherwise configure SSL
                .build();

        // Create a blocking stub for calling the Product Service
        productServiceStub = ProductServiceGrpc.newBlockingStub(channel);

    }

    public Order save(OrderDTO orderDTO) {
        log.info("Saving order: {}", orderDTO);
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        // Get the authenticated user's ID from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Since we set the userId as the principal, retrieve it from the authentication object
        String userId = (String) authentication.getPrincipal();
        log.info("service layer calling UserId from authentication: {}", userId);
        String orderSn = userId + order.getOrderDate();
        order.setOrderSn(orderSn);
        order.setUserId(userId);
        order.setShippingAddress(orderDTO.getAddress());

        int productId = orderDTO.getProduct_id();
        Product.GetProductRequest productRequest = Product.GetProductRequest.newBuilder()
                .setId(productId)
                .build();

        Product.GetProductReply productReply = productServiceStub.getProductInfo(productRequest);
        log.info("grpc response: {}", productReply);
        order.setProductName(productReply.getName());
        order.setDescription(productReply.getDescription());
        order.setTotalAmount(BigDecimal.valueOf(orderDTO.getQuantity()));

        return orderRepository.save(order);
    }

    public List<Order> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("oderDate").descending());
        Page<Order> OrderPage = orderRepository.findAll(pageable);
        return OrderPage.getContent();
    }
}
