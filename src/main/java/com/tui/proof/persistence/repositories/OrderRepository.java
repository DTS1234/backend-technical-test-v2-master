package com.tui.proof.persistence.repositories;

import com.tui.proof.persistence.model.ClientEntity;
import com.tui.proof.persistence.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author akazmierczak
 * @created 07.08.2022
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
