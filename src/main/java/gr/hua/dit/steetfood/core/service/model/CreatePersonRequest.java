package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.PersonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for requesting the creation (registration) of a Person.
 */
public record CreatePersonRequest(
    @NotNull PersonType type,
    @NotBlank  String huaId,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull @NotEmpty String emailAddress,
    @NotBlank String mobilePhoneNumber,
    @NotBlank  String rawPassword,
    @NotBlank String rawAddress
) {}
