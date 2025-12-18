package com.giftcard.emails.giftcardemailtemplates.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giftcard.emails.giftcardemailtemplates.entity.*;
import com.giftcard.emails.giftcardemailtemplates.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderContextBuilder {

    private final GiftcardOrderRepository orderRepo;
    private final GiftcardOrderItemRepository itemRepo;
    private final GiftcardCouponRepository couponRepo;
    private final ClientRepository clientRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderContextBuilder(GiftcardOrderRepository orderRepo,
                               GiftcardOrderItemRepository itemRepo,
                               GiftcardCouponRepository couponRepo,
                               ClientRepository clientRepo) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.couponRepo = couponRepo;
        this.clientRepo = clientRepo;
    }

    /* =========================================================
       ✅ NEW METHOD - clientId + orderId (RECOMMENDED)
       Only vouchers from THIS specific order
       ========================================================= */
    public Map<String, Object> buildContextForClientAndOrder(String clientId, String orderId) {

        // Validate client exists
        ClientProfile client = clientRepo.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found: " + clientId));

        // Get the specific order
        GiftcardOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Verify order belongs to this client
        if (!order.getClientId().equals(clientId)) {
            throw new RuntimeException("Order " + orderId + " does not belong to client " + clientId);
        }

        // Build context for THIS order only
        return buildContextFromOrder(order);
    }

    /* =========================================================
       ✅ EXISTING METHOD - All orders for client (backward compatible)
       ========================================================= */
    public Map<String, Object> buildContextForClient(String clientId) {

        List<GiftcardOrder> orders = orderRepo.findByClientId(clientId);

        if (orders == null || orders.isEmpty()) {
            throw new RuntimeException("No orders found for clientId: " + clientId);
        }

        // sort orders by createdAt DESC (latest first)
        orders.sort(Comparator.comparing(GiftcardOrder::getCreatedAt).reversed());

        GiftcardOrder latestOrder = orders.get(0);

        List<Map<String, Object>> allVouchers = new ArrayList<>();

        for (GiftcardOrder order : orders) {
            Map<String, Object> singleOrderCtx = buildContextFromOrder(order);

            Object vouchersObj = singleOrderCtx.get("vouchers");
            if (vouchersObj instanceof List) {
                allVouchers.addAll((List<Map<String, Object>>) vouchersObj);
            }
        }

        // ---- build final context ----
        Map<String, Object> ctx = buildContextFromOrder(latestOrder);

        // override vouchers with ALL vouchers
        ctx.put("vouchers", allVouchers);
        ctx.put("voucher_count", allVouchers.size());

        return ctx;
    }

    /* =========================================================
       INTERNAL: Build context from a single order
       ========================================================= */
    private Map<String, Object> buildContextFromOrder(GiftcardOrder order) {

        String orderId = order.getOrderId();

        List<GiftcardOrderItem> items = itemRepo.findByOrderId(orderId);
        List<String> itemIds = items.stream()
                .map(GiftcardOrderItem::getOrderItemId)
                .collect(Collectors.toList());

        List<GiftcardCoupon> coupons = itemIds.isEmpty()
                ? Collections.emptyList()
                : couponRepo.findByOrderItemIdIn(itemIds);

        Map<String, List<GiftcardCoupon>> couponsByItem =
                coupons.stream().collect(Collectors.groupingBy(GiftcardCoupon::getOrderItemId));

        List<Map<String, Object>> vouchers = new ArrayList<>();

        for (GiftcardOrderItem it : items) {
            List<GiftcardCoupon> cList =
                    couponsByItem.getOrDefault(it.getOrderItemId(), Collections.emptyList());

            if (cList.isEmpty()) {
                vouchers.add(defaultVoucherFromMeta(it));
            } else {
                for (GiftcardCoupon c : cList) {
                    vouchers.add(buildVoucherFromExactJson(it, c));
                }
            }
        }

        Map<String, Object> ctx = new HashMap<>();

        ctx.put("order_number", safeString(order.getOrderNumber()));
        ctx.put("order_date", order.getCreatedAt() == null
                ? ""
                : order.getCreatedAt().toLocalDate().toString());

        ctx.put("payment_method",
                Boolean.TRUE.equals(order.getWalletUsed()) ? "Wallet" : "Online Payment");

        ctx.put("transaction_id", "");
        ctx.put("voucher_count", vouchers.size());
        ctx.put("subtotal", order.getTotalAmount() != null
                ? order.getTotalAmount().toPlainString() : "0.00");

        ctx.put("discount", "0.00");
        ctx.put("total_amount", order.getTotalAmount() != null
                ? order.getTotalAmount().toPlainString() : "0.00");

        ClientProfile cp = clientRepo.findByClientId(order.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        ctx.put("customer_email", cp.getClientEmail());
        ctx.put("customer_name", cp.getClientName());

        ctx.put("order_details_url",
                "https://app.sabbpe.com/orders/" + safeString(order.getOrderNumber()));

        ctx.put("faq_url", "https://sabbpe.com/faq");
        ctx.put("support_url", "https://sabbpe.com/support");
        ctx.put("terms_url", "https://sabbpe.com/terms");
        ctx.put("website_url", "https://sabbpe.com");
        ctx.put("current_year", Calendar.getInstance().get(Calendar.YEAR));

        ctx.put("has_promo_coupon", false);
        ctx.put("vouchers", vouchers);

        return ctx;
    }

    /* ===================== JSON HELPERS (UNCHANGED) ===================== */

    private Map<String,Object> buildVoucherFromExactJson(GiftcardOrderItem it, GiftcardCoupon c) {
        Map<String,Object> v = new HashMap<>();

        BrandMeta meta = parseOrderItemMeta(it.getMeta());
        CouponItems ci = parseCouponItems(c.getItems());

        v.put("brand_name", meta.brandName != null ? meta.brandName : safeString(it.getBrandId()));
        v.put("voucher_type", meta.category != null ? meta.category : "E-Voucher");

        if (ci.balanceTotal != null) v.put("denomination", ci.balanceTotal);
        else if (c.getWalletBalance() != null) v.put("denomination", c.getWalletBalance().toPlainString());
        else if (it.getUnitValue() != null) v.put("denomination", it.getUnitValue().toPlainString());
        else v.put("denomination", "0.00");

        v.put("voucher_code", ci.voucherCode != null ? ci.voucherCode : c.getCouponId());
        v.put("pin_code", ci.pinCode);
        v.put("valid_from", ci.validFrom);
        v.put("expiry_date", ci.expiryDate);

        v.put("card_number", ci.voucherCode);
        v.put("brand_id", it.getBrandId());
        v.put("store_finder_enabled", false);
        v.put("store_finder_url", "https://sabbpe.com/store-finder");

        return v;
    }

    private Map<String,Object> defaultVoucherFromMeta(GiftcardOrderItem it) {
        Map<String,Object> v = new HashMap<>();
        BrandMeta meta = parseOrderItemMeta(it.getMeta());

        v.put("brand_name", meta.brandName != null ? meta.brandName : safeString(it.getBrandId()));
        v.put("voucher_type", meta.category != null ? meta.category : "E-Voucher");
        v.put("denomination", it.getUnitValue() != null ? it.getUnitValue().toPlainString() : "0.00");

        v.put("voucher_code", null);
        v.put("pin_code", null);
        v.put("valid_from", null);
        v.put("expiry_date", null);
        v.put("card_number", null);
        v.put("brand_id", it.getBrandId());

        return v;
    }

    private static class BrandMeta {
        String brandName;
        String category;
    }

    private BrandMeta parseOrderItemMeta(String metaJson) {
        BrandMeta m = new BrandMeta();
        if (metaJson == null) return m;
        try {
            JsonNode root = objectMapper.readTree(metaJson);
            if (root.has("brand_name")) m.brandName = nullIfEmpty(root.get("brand_name").asText());
            if (root.has("category")) m.category = nullIfEmpty(root.get("category").asText());
        } catch (Exception ignore) {}
        return m;
    }

    private static class CouponItems {
        String voucherCode;
        String pinCode;
        String expiryDate;
        String validFrom;
        String balanceTotal;
    }

    private CouponItems parseCouponItems(String itemsJson) {
        CouponItems ci = new CouponItems();
        if (itemsJson == null) return ci;
        try {
            JsonNode root = objectMapper.readTree(itemsJson);
            JsonNode node = root.isArray() && root.size() > 0 ? root.get(0) : root;

            if (node.has("getCardNo")) ci.voucherCode = node.get("getCardNo").asText();
            if (node.has("getCardPin")) ci.pinCode = node.get("getCardPin").asText();
            if (node.has("getExpiryDate")) ci.expiryDate = node.get("getExpiryDate").asText();
            if (node.has("balanceTotal")) ci.balanceTotal = node.get("balanceTotal").asText();
        } catch (Exception ignore) {}
        return ci;
    }

    private static String nullIfEmpty(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return s.trim();
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }
}
