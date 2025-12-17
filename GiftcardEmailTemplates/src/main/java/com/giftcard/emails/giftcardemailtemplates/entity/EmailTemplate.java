package com.giftcard.emails.giftcardemailtemplates.entity;




import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "email_templates")
public class EmailTemplate {

    @Id
    @Column(name = "email_template_id")
    private String emailTemplateId;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(name = "template_body", columnDefinition = "TEXT", nullable = false)
    private String templateBody;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
