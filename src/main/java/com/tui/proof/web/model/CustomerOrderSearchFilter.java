package com.tui.proof.web.model;

import com.tui.proof.web.Order;
import com.tui.proof.web.Paging;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderSearchFilter extends Paging {

    private String firstName;
    private String firstNameEndsWith;
    private String firstNameStartsWith;
    private String firstNameContains;

    private String lastName;
    private String lastNameStartsWith;
    private String lastNameEndsWith;
    private String lastNameContains;

    private String telephone;
    private String telephoneStartsWith;
    private String telephoneEndsWith;
    private String telephoneContains;

    private String email;
    private String emailStartsWith;
    private String emailEndsWith;
    private String emailContains;

    @Builder(builderMethodName = "requestBuilder")
    public CustomerOrderSearchFilter(
            String firstName,
            String firstNameEndsWith,
            String firstNameStartsWith,
            String firstNameContains,
            String lastName,
            String lastNameStartsWith,
            String lastNameEndsWith,
            String lastNameContains,
            String telephone,
            String telephoneStartsWith,
            String telephoneEndsWith,
            String telephoneContains,
            String email,
            String emailStartsWith,
            String emailEndsWith,
            String emailContains,
            Integer pageNumber,
            Integer pageSize,
            String sort,
            Order order,
            Integer limitTo
    ) {
        super(pageNumber, pageSize, sort, order, limitTo);
        this.firstName = firstName;
        this.firstNameEndsWith = firstNameEndsWith;
        this.firstNameStartsWith = firstNameStartsWith;
        this.firstNameContains = firstNameContains;

        this.lastName = lastName;
        this.lastNameStartsWith = lastNameStartsWith;
        this.lastNameEndsWith = lastNameEndsWith;
        this.lastNameContains = lastNameContains;

        this.telephone = telephone;
        this.telephoneStartsWith = telephoneStartsWith;
        this.telephoneEndsWith = telephoneEndsWith;
        this.telephoneContains = telephoneContains;

        this.email = email;
        this.emailStartsWith = emailStartsWith;
        this.emailEndsWith = emailEndsWith;
        this.emailContains = emailContains;
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getFirstNameEndsWith() {
        return Optional.ofNullable(firstNameEndsWith);
    }

    public Optional<String> getFirstNameStartsWith() {
        return Optional.ofNullable(firstNameStartsWith);
    }

    public Optional<String> getFirstNameContains() {
        return Optional.ofNullable(firstNameContains);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getLastNameStartsWith() {
        return Optional.ofNullable(lastNameStartsWith);
    }

    public Optional<String> getLastNameEndsWith() {
        return Optional.ofNullable(lastNameEndsWith);
    }

    public Optional<String> getLastNameContains() {
        return Optional.ofNullable(lastNameContains);
    }

    public Optional<String> getTelephone() {
        return Optional.ofNullable(telephone);
    }

    public Optional<String> getTelephoneStartsWith() {
        return Optional.ofNullable(telephoneStartsWith);
    }

    public Optional<String> getTelephoneEndsWith() {
        return Optional.ofNullable(telephoneEndsWith);
    }

    public Optional<String> getTelephoneContains() {
        return Optional.ofNullable(telephoneContains);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getEmailStartsWith() {
        return Optional.ofNullable(emailStartsWith);
    }

    public Optional<String> getEmailEndsWith() {
        return Optional.ofNullable(emailEndsWith);
    }

    public Optional<String> getEmailContains() {
        return Optional.ofNullable(emailContains);
    }
}
