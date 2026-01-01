package gr.hua.dit.steetfood.web.rest.model;


/**
 * @see ClientAuthResource
 */
public record ClientTokenResponse(
    String accessToken,
    String tokenType,
    long expiresIn
) {}
