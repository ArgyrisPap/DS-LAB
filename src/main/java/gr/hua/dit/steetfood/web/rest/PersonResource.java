package gr.hua.dit.steetfood.web.rest;


import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.service.PersonService;

import gr.hua.dit.steetfood.core.service.model.CreatePersonRequest;

import gr.hua.dit.steetfood.core.service.model.CreatePersonResult;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@code Order} resource.
 */
@RestController
@RequestMapping(value = "/api/v1/persons", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonResource {
    private final PersonService personService;

    public PersonResource(final PersonService personService) {
        if (personService == null) throw new NullPointerException();
        this.personService = personService;
    }

    @PreAuthorize("hasRole('INTEGRATION_WRITE')")
    @PostMapping("")
    public ResponseEntity<?> register(@Valid @RequestBody CreatePersonRequest request) {
        if (request.type() != PersonType.OWNER)return ResponseEntity.badRequest().body("This endpoint only allows OWNER registration.");
        //Lazy-Last minute resort. Το σωστο θα ηταν να εφτιαχνα νεο record ιδιο με το createPersonRequest αλλα χωρις το type,
        // και να το εβαζα στο τελος χειροκινητα, και μετα convert σε createPersonRequest

        CreatePersonResult result = personService.createPerson(request, false);

        if (result.created()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.personView());
        } else {
            return ResponseEntity.badRequest().body(result.reason());
        }
    }

}
