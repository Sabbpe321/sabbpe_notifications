package com.giftcard.emails.giftcardemailtemplates.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giftcard.emails.giftcardemailtemplates.entity.GiftcardOrderItem;

public interface GiftcardOrderItemRepository extends JpaRepository<GiftcardOrderItem, String> {
List<GiftcardOrderItem> findByOrderId(String orderId);
}