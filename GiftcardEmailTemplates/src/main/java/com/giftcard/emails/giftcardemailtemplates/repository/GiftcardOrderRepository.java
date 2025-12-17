package com.giftcard.emails.giftcardemailtemplates.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giftcard.emails.giftcardemailtemplates.entity.GiftcardOrder;

public interface GiftcardOrderRepository extends JpaRepository<GiftcardOrder, String> {
//	Optional<GiftcardOrder> findTopByClientIdOrderByCreatedAtDesc(String clientId);
	List<GiftcardOrder> findByClientId(String clientId);

	// projection query if you prefer, but we use item + coupon repos below
	}
