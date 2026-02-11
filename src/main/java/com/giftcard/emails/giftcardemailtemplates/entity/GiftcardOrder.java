package com.giftcard.emails.giftcardemailtemplates.entity;




	import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
	import java.time.LocalDateTime;
@Data

	@Entity
	@Table(name = "giftcard_orders")
	public class GiftcardOrder {
	@Id
	@Column(name = "order_id")
	private String orderId;


	@Column(name = "client_id", nullable = false)
	private String clientId;


	@Column(name = "order_number")
	private String orderNumber;


	@Column(name = "total_amount")
	private BigDecimal totalAmount;


	@Column(name = "wallet_used")
	private Boolean walletUsed;


	@Column(name = "wallet_amount")
	private BigDecimal walletAmount;


	@Column(name = "currency")
	private String currency;


	@Column(name = "status")
	private String status;


	@Column(name = "created_at")
	private LocalDateTime createdAt;


	@Column(name = "paid_at")
	private LocalDateTime paidAt;


	// getters / setters
	// (omit for brevity — generate in your IDE or use Lombok)
	}


