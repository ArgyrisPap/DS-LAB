package gr.hua.dit.steetfood.core.port.impl.dto;

import gr.hua.dit.steetfood.core.model.PersonType;

/**
 * LookupResult DTO.
 */
public record LookupResult(
    String raw,
    boolean exists,
    String huaId,
    PersonType type
) {}
