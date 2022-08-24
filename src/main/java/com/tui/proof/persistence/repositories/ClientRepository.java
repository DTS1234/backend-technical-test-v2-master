package com.tui.proof.persistence.repositories;

import com.tui.proof.persistence.model.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author akazmierczak
 * @created 08.08.2022
 */
@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long>, JpaSpecificationExecutor<ClientEntity> {

    Optional<ClientEntity> findClientEntityByEmailOrTelephone(String email, String telephone);

    Optional<ClientEntity> findClientEntityByEmail(String email);
}
