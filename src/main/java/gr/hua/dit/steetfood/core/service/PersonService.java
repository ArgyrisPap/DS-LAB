package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.service.model.CreatePersonRequest;
import gr.hua.dit.steetfood.core.service.model.CreatePersonResult;

/**
 * Service for managing {@link gr.hua.dit.steetfood.core.model.Person}.
 */
public interface PersonService {

    CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest, final boolean notify);

    default CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest) {
        return this.createPerson(createPersonRequest, true);
    }
}
