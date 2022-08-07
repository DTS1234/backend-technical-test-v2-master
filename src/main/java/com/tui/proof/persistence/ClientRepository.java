package com.tui.proof.persistence;

import com.tui.proof.persistence.model.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author akazmierczak
 * @created 08.08.2022
 */
@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findClientEntityByEmailOrTelephone(String email, String telephone);

}
