package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.PersonType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for requesting the creation (registration) of a Person.
 */
public record CreatePersonRequest(
    @NotNull PersonType type,
    @NotNull @NotEmpty String huaId,
    @NotNull @NotEmpty String firstName,
    @NotNull @NotEmpty String lastName,
    @NotNull @NotEmpty String emailAddress,
    @NotNull @NotEmpty String mobilePhoneNumber,
    @NotNull @NotEmpty String rawPassword,
    @NotNull @NotEmpty String rawAddress
) {}
