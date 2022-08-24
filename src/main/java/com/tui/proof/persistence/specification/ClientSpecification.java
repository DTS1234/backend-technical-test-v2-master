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

    private final String EMAIL = "email";
    private final String FIRSTNAME = "firstName";
    private final String LASTNAME = "lastName";
    private final String TELEPHONE = "telephone";

    private final CustomerOrderSearchFilter filter;

    @Override
    public Predicate toPredicate(Root<ClientEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        addEmailPredicates(root, criteriaBuilder, predicates);
        addFirstNamePredicates(root, criteriaBuilder, predicates);
        addLastNamePredicates(root, criteriaBuilder, predicates);
        addTelephonePredicates(root, criteriaBuilder, predicates);

        return criteriaBuilder.and(Iterables.toArray(predicates, Predicate.class));
    }

    private void addTelephonePredicates(Root<ClientEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        filter.getTelephone().ifPresent(
                telephone -> predicates.add(criteriaBuilder.equal(root.get(TELEPHONE), telephone))
        );
        filter.getTelephoneContains().ifPresent(
                telephone -> predicates.add(criteriaBuilder.like(root.get(TELEPHONE), ("%" + filter.getTelephoneContains().get() + "%"))
                ));
        filter.getTelephoneEndsWith().ifPresent(
                telephone -> predicates.add(criteriaBuilder.like(root.get(TELEPHONE), ("%" + filter.getTelephoneEndsWith().get()))
                ));
        filter.getTelephoneStartsWith().ifPresent(
                telephone -> predicates.add(criteriaBuilder.like(root.get(TELEPHONE), (filter.getTelephoneStartsWith().get() + "%"))
                ));
    }

    private void addLastNamePredicates(Root<ClientEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        filter.getLastName().ifPresent(
                lastName -> predicates.add(criteriaBuilder.equal(root.get(LASTNAME), lastName))
        );
        filter.getLastNameContains().ifPresent(
                lastName -> predicates.add(criteriaBuilder.like(root.get(LASTNAME), ("%" + filter.getLastNameContains().get() + "%"))
                ));
        filter.getLastNameEndsWith().ifPresent(
                lastName -> predicates.add(criteriaBuilder.like(root.get(LASTNAME), ("%" + filter.getLastNameEndsWith().get()))
                ));
        filter.getLastNameStartsWith().ifPresent(
                lastName -> predicates.add(criteriaBuilder.like(root.get(LASTNAME), (filter.getLastNameStartsWith().get() + "%"))
                ));
    }

    private void addFirstNamePredicates(Root<ClientEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        filter.getFirstName().ifPresent(
                firstName -> predicates.add(criteriaBuilder.equal(root.get(FIRSTNAME), firstName))
        );
        filter.getFirstNameContains().ifPresent(
                firstName -> predicates.add(criteriaBuilder.like(root.get(FIRSTNAME), ("%" + filter.getFirstNameContains().get() + "%"))
                ));
        filter.getFirstNameEndsWith().ifPresent(
                firstName -> predicates.add(criteriaBuilder.like(root.get(FIRSTNAME), ("%" + filter.getFirstNameEndsWith().get()))
                ));
        filter.getFirstNameStartsWith().ifPresent(
                firstName -> predicates.add(criteriaBuilder.like(root.get(FIRSTNAME), (filter.getFirstNameStartsWith().get() + "%"))
                ));
    }

    private void addEmailPredicates(Root<ClientEntity> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        filter.getEmail().ifPresent(
                email -> predicates.add(criteriaBuilder.equal(root.get(EMAIL), email))
        );
        filter.getEmailContains().ifPresent(
                email -> predicates.add(criteriaBuilder.like(root.get(EMAIL), ("%" + filter.getEmailContains().get() + "%"))
                ));
        filter.getEmailEndsWith().ifPresent(
                email -> predicates.add(criteriaBuilder.like(root.get(EMAIL), ("%" + filter.getEmailEndsWith().get()))
                ));
        filter.getEmailStartsWith().ifPresent(
                email -> predicates.add(criteriaBuilder.like(root.get(EMAIL), (filter.getEmailStartsWith().get() + "%"))
                ));
    }
}
