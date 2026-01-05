package gr.hua.dit.steetfood.web.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see ClientAuthResource
 */
public record ClientTokenRequest(
    @NotNull @NotBlank String clientId,
    @NotNull @NotBlank String clientSecret
) {}
