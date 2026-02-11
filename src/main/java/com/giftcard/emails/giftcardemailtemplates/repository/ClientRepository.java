package com.giftcard.emails.giftcardemailtemplates.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;




import com.giftcard.emails.giftcardemailtemplates.entity.ClientProfile;


public interface ClientRepository extends JpaRepository<ClientProfile, String> {

    Optional<ClientProfile> findByClientId(String clientId);
    


}
