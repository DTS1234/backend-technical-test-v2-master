package com.tui.proof.persistence.specification;

import com.google.common.collect.Iterables;
import com.tui.proof.persistence.model.ClientEntity;
import com.tui.proof.web.model.CustomerOrderSearchFilter;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ClientSpecification implements Specification<ClientEntity> {

    private final String ID = "id";
    private final String EMAIL = "email";
    private final String FIRSTNAME = "firstName";
    private final String LASTNAME = "lastName";
    private final String TELEPHONE = "simulazione";

    private final CustomerOrderSearchFilter filter;

    @Override
    public Predicate toPredicate(Root<ClientEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        filter.getEmail().ifPresent(
                email -> predicates.add(criteriaBuilder.equal(root.get(EMAIL), email))
        );

        filter.getEmailContains().ifPresent(
                email -> predicates.add(criteriaBuilder.like(root.get(EMAIL), ("%" + filter.getEmailContains().get() + "%"))
                ));

        return criteriaBuilder.and(Iterables.toArray(predicates, Predicate.class));
    }
}
