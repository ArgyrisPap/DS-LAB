package gr.hua.dit.steetfood.core.port;

import gr.hua.dit.steetfood.core.port.impl.dto.PhoneNumberValidationResult;

/**
 * Port to external service for managing phone numbers.
 */
public interface PhoneNumberPort {

    PhoneNumberValidationResult validate(final String rawPhoneNumber);
}
