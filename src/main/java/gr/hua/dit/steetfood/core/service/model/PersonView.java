package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.PersonType;

/**
 * PersonView (DTO) that includes only information to be exposed.
 */
public record PersonView(
    long id,
    String huaId,
    String firstName,
    String lastName,
    String mobilePhoneNumber,
    String emailAddress,
    PersonType type
) {
    public String fullName() {
        return this.firstName + " " + this.lastName;
    }
}
