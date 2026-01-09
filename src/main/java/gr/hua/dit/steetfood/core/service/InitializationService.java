package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.model.Client;
import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.PersonLocation;
import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.port.impl.AddressPortImpl;
import gr.hua.dit.steetfood.core.repository.ClientRepository;
import gr.hua.dit.steetfood.core.repository.PersonLocationRepository;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.service.model.CreatePersonRequest;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Initializes application.
 */
@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final PersonService personService;
    private final PersonRepository personRepository;
    private final AddressPortImpl addressPortImpl;
    private final ClientRepository clientRepository;

    public InitializationService(final PersonService personService,
                                 final PersonRepository personRepository,
                                 AddressPortImpl addressPortImpl,
                                 ClientRepository clientRepository) {
        if (personService == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException(); //SBHSIMO
        if (clientRepository == null)throw new NullPointerException();
        this.personService = personService;
        this.personRepository = personRepository; //SBHSBIMO
        //this.initialized = new AtomicBoolean(true); //TODO CREATE-DROP & FIRST TIME UPDATE=FALSE, UPDATE=TRUE
        this.addressPortImpl = addressPortImpl;
        this.clientRepository = clientRepository;
    }

    @PostConstruct
    public void populateDatabaseWithInitialData() {
        if (clientRepository.count() > 0 || personRepository.count() > 0) {
            LOGGER.warn("Database initialization skipped: initial data has already been populated.");
            return;
        }

        LOGGER.info("Starting database initialization with initial data...");
        final List<Client> clientList = List.of(
            new Client(null, "client01", "s3cr3t", "INTEGRATION_READ,INTEGRATION_WRITE"),
            new Client(null, "client02", "s3cr3t", "INTEGRATION_READ")
        );
        //LOGGER.info("DELETE ME, "+ clientList.toString());
        this.clientRepository.saveAll(clientList);
        final List<CreatePersonRequest> createPersonRequestList = List.of(
            new CreatePersonRequest(
                PersonType.ADMIN,
                "admin01",
                "Argyris",
                "Pap",
                "admin@hua.gr",
                "+306911111111",
                "1234",
                "Ελ. Βενιζέλου 70 Καλλιθέα"
            ),
            new CreatePersonRequest(
                PersonType.OWNER,
                "t0001",
                "Dimitris",
                "Gkoulis",
                "gkoulis@hua.gr",
                "+306900000000",
                "1234",
                "Θησεως 70 Καλλιθεα"
            ),
            new CreatePersonRequest(
                PersonType.USER,
                "it2023001",
                "Test 1",
                "Test 1",
                "it2023001@hua.gr",
                "+306900000001",
                "1234",
                "Εβρου 60 Αιγαλεω"
            ),
            new CreatePersonRequest(
                PersonType.USER,
                "it2023002",
                "Test 2",
                "Test 2",
                "it2023002@hua.gr",
                "+306900000002",
                "1234",
                "Ομηρου 9 Ταυρος"
            ),
            new CreatePersonRequest(
                PersonType.OWNER,
                "t0002",
                "Test 2",
                "Test 2",
                "teacher1@hua.gr",
                "+306900000003",
                "1234",
                "Ομηρου 15 Ταυρος"
            )
        );
        for (final var createPersonRequest : createPersonRequestList) {
            this.personService.createPerson(createPersonRequest, false); // do not send SMS
        }
        LOGGER.info("Database initialization completed successfully.");
    }
}
