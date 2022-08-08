package com.tui.proof.persistence.repositories;

import com.tui.proof.persistence.model.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    Optional<AddressEntity> findAddressEntityByCityAndAndCountryAndPostcodeAndStreet(String city, String country, String postCode, String street);

}
