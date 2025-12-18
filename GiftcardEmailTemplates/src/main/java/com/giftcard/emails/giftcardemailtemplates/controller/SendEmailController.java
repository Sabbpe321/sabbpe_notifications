package com.giftcard.emails.giftcardemailtemplates.controller;

import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.giftcard.emails.giftcardemailtemplates.service.OrderContextBuilder;
import com.giftcard.emails.giftcardemailtemplates.service.VoucherEmailService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SendEmailController {

    private final OrderContextBuilder contextBuilder;
    private final VoucherEmailService emailService;

    public SendEmailController(OrderContextBuilder contextBuilder, VoucherEmailService emailService) {
        this.contextBuilder = contextBuilder;
        this.emailService = emailService;
    }

    // ✅ NEW ENDPOINT - Both clientId and orderId
    @PostMapping("/send-by-client/{clientId}/order/{orderId}")
    public ResponseEntity<?> sendByClientAndOrder(
            @PathVariable String clientId,
            @PathVariable String orderId
    ) throws MessagingException {

        // Build context for specific client and order
        Map<String, Object> ctx = contextBuilder.buildContextForClientAndOrder(clientId, orderId);

        // Send email using "gift_vouchers" template
        emailService.renderAndSend("gift_vouchers", ctx);

        return ResponseEntity.ok(Map.of(
                "status", "EMAIL_SENT",
                "client_id", clientId,
                "order_id", orderId
        ));
    }

    // ✅ KEEP OLD ENDPOINT for backward compatibility (optional)
    @PostMapping("/send-by-client/{clientId}")
    public ResponseEntity<?> sendByClient(@PathVariable String clientId) throws MessagingException {

        Map<String, Object> ctx = contextBuilder.buildContextForClient(clientId);

        emailService.renderAndSend("gift_vouchers", ctx);

        return ResponseEntity.ok(Map.of(
                "status", "EMAIL_SENT",
                "client_id", clientId
        ));
    }
}
