package com.giftcard.emails.giftcardemailtemplates.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data

@Entity
@Table(name = "giftcard_order_items")
public class GiftcardOrderItem {
@Id
@Column(name = "order_item_id")
private String orderItemId;


@Column(name = "order_id")
private String orderId;


@Column(name = "brand_id")
private String brandId;


@Column(name = "quantity")
private Integer quantity;


@Column(name = "unit_value")
private BigDecimal unitValue;


@Column(name = "line_total")
private BigDecimal lineTotal;


@Column(name = "meta", columnDefinition = "longtext")
private String meta;


@Column(name = "created_at")
private LocalDateTime createdAt;


// getters / setters
}
