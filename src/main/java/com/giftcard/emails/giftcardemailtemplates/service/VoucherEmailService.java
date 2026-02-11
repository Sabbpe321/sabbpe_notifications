package com.giftcard.emails.giftcardemailtemplates.service;

import com.giftcard.emails.giftcardemailtemplates.entity.EmailTemplate;
import com.giftcard.emails.giftcardemailtemplates.repository.EmailTemplateRepository;
import com.giftcard.emails.giftcardemailtemplates.template.TemplateCache;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class VoucherEmailService {

    private final EmailTemplateRepository templateRepo;
    private final JavaMailSender mailSender;
    private final TemplateCache templateCache;
    private final Handlebars handlebars;

    public VoucherEmailService(EmailTemplateRepository templateRepo,
                               JavaMailSender mailSender,
                               TemplateCache templateCache,
                               Handlebars handlebars) {
        this.templateRepo = templateRepo;
        this.mailSender = mailSender;
        this.templateCache = templateCache;
        this.handlebars = handlebars;
    }
    
    

    /**
     * Render template (body + subject) from DB and send HTML email.
     *
     * @param templateName logical template name stored in DB (template_name column)
     * @param context      Handlebars context map
     */
    public void renderAndSend(String templateName, Map<String, Object> context) throws MessagingException {
        // Load template by templateName (not by a hard-coded string)
        EmailTemplate tmpl = templateRepo.findByTemplateName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateName));

        // Compile (or fetch cached compiled) body template
        Template compiledBody = templateCache.getOrCompile(tmpl.getEmailTemplateId(), tmpl.getTemplateBody());
        String renderedBody;
        try {
            renderedBody = compiledBody.apply(context);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to apply body template: " + tmpl.getEmailTemplateId(), ex);
        }

        // Render subject (subject may contain handlebars placeholders)
        String rawSubject = tmpl.getEmailSubject() != null ? tmpl.getEmailSubject() : templateName;
        String renderedSubject;
        try {
            Template subjTemplate = handlebars.compileInline(rawSubject);
            renderedSubject = subjTemplate.apply(context);
        } catch (IOException e) {
            // fallback: use raw subject if compilation fails
            renderedSubject = rawSubject;
        } catch (Exception ex) {
            // any other rendering error fallback to raw subject
            renderedSubject = rawSubject;
        }

        // Ensure recipient email present in context
        Object toObj = context.get("customer_email");
        if (toObj == null) throw new IllegalArgumentException("customer_email missing in context");
        String to = String.valueOf(toObj);

        sendHtmlEmail(to, renderedSubject, renderedBody);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setText(htmlBody, true);
        helper.setTo(to);
        helper.setSubject(subject);
        // helper.setFrom("no-reply@yourdomain.com");
        mailSender.send(message);
    }
}
