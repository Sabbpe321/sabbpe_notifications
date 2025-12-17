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


@PostMapping("/send-by-client/{clientId}")
public ResponseEntity<?> sendByClient(@PathVariable String clientId) throws MessagingException {

    Map<String,Object> ctx = contextBuilder.buildContextForClient(clientId);

    emailService.renderAndSend("gift_vouchers", ctx);

    return ResponseEntity.ok(Map.of(
        "status", "EMAIL_SENT",
        "client_id", clientId
    ));
}
}