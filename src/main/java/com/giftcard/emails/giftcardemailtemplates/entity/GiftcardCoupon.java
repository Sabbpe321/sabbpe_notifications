package com.giftcard.emails.giftcardemailtemplates.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "giftcard_coupons")
public class GiftcardCoupon {
@Id
@Column(name = "coupon_id")
private String couponId;


@Column(name = "order_item_id")
private String orderItemId;


@Column(name = "vd_raw_response", columnDefinition = "longtext")
private String vdRawResponse;


@Column(name = "status")
private String status;


@Column(name = "product_name")
private String productName;


@Column(name = "voucher_name")
private String voucherName;


@Column(name = "items", columnDefinition = "longtext")
private String items;


@Column(name = "wallet_balance")
private BigDecimal walletBalance;


@Column(name = "created_at")
private LocalDateTime createdAt;


@Column(name = "updated_at")
private LocalDateTime updatedAt;

}
// getters / setters