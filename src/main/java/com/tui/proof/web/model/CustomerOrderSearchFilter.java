package com.tui.proof.web.model;

import com.tui.proof.web.Order;
import com.tui.proof.web.Paging;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
        return Optional.ofNullable(StringUtils.stripToNull(firstName));
    }

    public Optional<String> getFirstNameEndsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(firstNameEndsWith));
    }

    public Optional<String> getFirstNameStartsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(firstNameStartsWith));
    }

    public Optional<String> getFirstNameContains() {
        return Optional.ofNullable(StringUtils.stripToNull(firstNameContains));
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(StringUtils.stripToNull(lastName));
    }

    public Optional<String> getLastNameStartsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(lastNameStartsWith));
    }

    public Optional<String> getLastNameEndsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(lastNameEndsWith));
    }

    public Optional<String> getLastNameContains() {
        return Optional.ofNullable(StringUtils.stripToNull(lastNameContains));
    }

    public Optional<String> getTelephone() {
        return Optional.ofNullable(StringUtils.stripToNull(telephone));
    }

    public Optional<String> getTelephoneStartsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(telephoneStartsWith));
    }

    public Optional<String> getTelephoneEndsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(telephoneEndsWith));
    }

    public Optional<String> getTelephoneContains() {
        return Optional.ofNullable(StringUtils.stripToNull(telephoneContains));
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(StringUtils.stripToNull(email));
    }

    public Optional<String> getEmailStartsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(emailStartsWith));
    }

    public Optional<String> getEmailEndsWith() {
        return Optional.ofNullable(StringUtils.stripToNull(emailEndsWith));
    }

    public Optional<String> getEmailContains() {
        return Optional.ofNullable(StringUtils.stripToNull(emailContains));
    }
}
