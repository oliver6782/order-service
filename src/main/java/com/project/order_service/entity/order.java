package com.project.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String userId;

    @Column
    private String orderSn;

    @Column
    private String productName;

    @Column
    private String description;

    @Column
    private BigDecimal totalAmount;

    @Column
    private LocalDateTime orderDate;

    @Column
    private String shippingAddress;


}
