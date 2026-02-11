package com.giftcard.emails.giftcardemailtemplates.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giftcard.emails.giftcardemailtemplates.entity.GiftcardCoupon;

public interface GiftcardCouponRepository extends JpaRepository<GiftcardCoupon, String> {
List<GiftcardCoupon> findByOrderItemIdIn(List<String> orderItemIds);
}