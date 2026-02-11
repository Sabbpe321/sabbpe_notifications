package com.giftcard.emails.giftcardemailtemplates.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giftcard.emails.giftcardemailtemplates.entity.EmailTemplate;


public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, String> {
    // find by the Java property name (templateName)
    Optional<EmailTemplate> findByTemplateName(String templateName);
}