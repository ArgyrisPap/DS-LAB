package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.PersonLocation;
import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.port.impl.AddressPortImpl;
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
    private final AtomicBoolean initialized;
    private final PersonRepository personRepository; //TODO SBHSIMO
    private final PersonLocationRepository personLocationRepository;
    private final AddressPortImpl addressPortImpl;

    public InitializationService(final PersonService personService,
                                 final PersonRepository personRepository,
                                 PersonLocationRepository personLocationRepository,
                                 AddressPortImpl addressPortImpl) {
        if (personService == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException(); //SBHSIMO
        this.personService = personService;
        this.personRepository = personRepository; //SBHSBIMO
        this.initialized = new AtomicBoolean(false);
        this.personLocationRepository = personLocationRepository;
        this.addressPortImpl = addressPortImpl;
    }

    @PostConstruct
    public void populateDatabaseWithInitialData() {
        final boolean alreadyInitialized = this.initialized.getAndSet(true);
        if (alreadyInitialized) {
            LOGGER.warn("Database initialization skipped: initial data has already been populated.");
            return;
        }

        LOGGER.info("Starting database initialization with initial data...");
        final List<CreatePersonRequest> createPersonRequestList = List.of(
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
        PersonLocation personLocation = new PersonLocation();
        personLocation.setId(null);
        personLocation.setZipCode(18900);
        personLocation.setStreet("Epidaurou");
        personLocation.setStreetNumber("125");
        personLocation.setCity("Peristeri");
        personLocation.setState("ATTIKH");



        Person person = this.personRepository.findByHuaId("t0001").orElse(null);
        personLocation.setPerson(person);
        //System.out.println ("========before=======");
        //System.out.println(person.toString());
        this.personService.addLocationToPerson("t0001",personLocation);

        LOGGER.info("Database initialization completed successfully.");
        this.addressPortImpl.findAdress("Πατησιων 76 Αθηνα");
    }
}
