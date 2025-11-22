package gr.hua.dit.steetfood.core.security;

import gr.hua.dit.steetfood.core.model.PersonType;

/**
 * @see CurrentUserProvider
 */
public record CurrentUser(long id, String emailAddress, PersonType type) {}
