package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;

public record PersonProfileDTO(
    Person person,
    AddressResult addressResult,
    String mapUrl

) {
}
