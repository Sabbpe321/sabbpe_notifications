package com.giftcard.emails.giftcardemailtemplates.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


import jakarta.persistence.*;

@Entity
@Table(name = "client_profile")
public class ClientProfile {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_name")
    private String clientName;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
}
