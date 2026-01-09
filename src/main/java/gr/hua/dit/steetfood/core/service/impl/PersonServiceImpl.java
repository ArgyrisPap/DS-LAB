package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.PersonLocation;
import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.port.AddressPort;
import gr.hua.dit.steetfood.core.port.LookupPort;
import gr.hua.dit.steetfood.core.port.PhoneNumberPort;
import gr.hua.dit.steetfood.core.port.SmsNotificationPort;
import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;
import gr.hua.dit.steetfood.core.port.impl.dto.PhoneNumberValidationResult;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.service.EmailService;
import gr.hua.dit.steetfood.core.service.PersonService;
import gr.hua.dit.steetfood.core.service.mapper.PersonMapper;
import gr.hua.dit.steetfood.core.service.model.CreatePersonRequest;
import gr.hua.dit.steetfood.core.service.model.CreatePersonResult;
import gr.hua.dit.steetfood.core.service.model.PersonProfileDTO;
import gr.hua.dit.steetfood.core.service.model.PersonView;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link PersonService}.
 */
@Service
public class PersonServiceImpl implements PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PhoneNumberPort phoneNumberPort;
    private final LookupPort lookupPort;
    private final SmsNotificationPort smsNotificationPort;
    private final EmailService emailService;
    private final AddressPort addressPort;;

    public PersonServiceImpl(final PasswordEncoder passwordEncoder,
                             final PersonRepository personRepository,
                             final PersonMapper personMapper,
                             final PhoneNumberPort phoneNumberPort,
                             final LookupPort lookupPort,
                             final SmsNotificationPort smsNotificationPort,
                             final EmailService emailService,
                             final AddressPort addressPort) {
        if (passwordEncoder == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (personMapper == null) throw new NullPointerException();
        if (phoneNumberPort == null) throw new NullPointerException();
        if (lookupPort == null) throw new NullPointerException();
        if (smsNotificationPort == null) throw new NullPointerException();
        if (emailService == null) throw new NullPointerException();
        if (addressPort == null) throw new NullPointerException();

        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.phoneNumberPort = phoneNumberPort;
        this.lookupPort = lookupPort;
        this.smsNotificationPort = smsNotificationPort;
        this.emailService = emailService;
        this.addressPort = addressPort;
    }

    @Transactional
    @Override
    public CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest, final boolean notify) {
        if (createPersonRequest == null) throw new NullPointerException();

        // Unpack (we assume valid `CreatePersonRequest` instance)
        // --------------------------------------------------

        final PersonType type = createPersonRequest.type();
        final String huaId = createPersonRequest.huaId().strip(); // remove whitespaces
        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        String mobilePhoneNumber = createPersonRequest.mobilePhoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();
        final String rawAddress = createPersonRequest.rawAddress();

        // Basic email address validation.
        // --------------------------------------------------

        if (!emailAddress.endsWith("@hua.gr")) {
            return CreatePersonResult.fail("Only academic email addresses (@hua.gr) are allowed");
        }

        // Advanced mobile phone number validation.
        // --------------------------------------------------

        final PhoneNumberValidationResult phoneNumberValidationResult
            = this.phoneNumberPort.validate(mobilePhoneNumber);
        if (!phoneNumberValidationResult.isValidMobile()) {
            return CreatePersonResult.fail("Mobile Phone Number is not valid");
        }
        mobilePhoneNumber = phoneNumberValidationResult.e164();

        // --------------------------------------------------

        if (this.personRepository.existsByHuaIdIgnoreCase(huaId)) {
            return CreatePersonResult.fail("HUA ID already registered");
        }

        if (this.personRepository.existsByEmailAddressIgnoreCase(emailAddress)) {
            return CreatePersonResult.fail("Email Address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            return CreatePersonResult.fail("Mobile Phone Number already registered");
        }

        // ----------------------PORTS!----------------------------
        /* PROS TO PARVN DEN ELEGXV MESV TOY NOC
        *
        final PersonType personType_lookup = this.lookupPort.lookup(huaId).orElse(null);
        if (personType_lookup == null) {
            return CreatePersonResult.fail("Invalid HUA ID");
        }
        if (personType_lookup != type) {
            return CreatePersonResult.fail("The provided person type does not match the actual one");
        }*/
        String formatedAddress = String.join("+", rawAddress.trim().split("\\s+"));
        Optional<AddressResult> result=this.addressPort.findAdress(formatedAddress);


        // --------------------------------------------------

        final String hashedPassword = this.passwordEncoder.encode(rawPassword);

        // Instantiate person.
        // --------------------------------------------------

        Person person = new Person();
        person.setId(null); // auto generated
        person.setHuaId(huaId);
        person.setType(type);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(emailAddress);
        person.setMobilePhoneNumber(mobilePhoneNumber);
        person.setPasswordHash(hashedPassword);
        //DEN ARXIKOPOIV TO ADDRESS, AN DEN TO BREI TO PORT EINAI EJARXHS NULL KAI DEN TO ALLAZV
        person.setRawAddress(rawAddress);
        if (!result.isEmpty()) {
            AddressResult addressResult = result.get(); //den rixnw kati, epeidh mporei apla na mhn petuxe h
            // anazhthsh: den einai aparaithta sfalma ths efarmoghs 'h toy xrhsth

            //Convert AdressResult to Address (manually)
            //Address address = new Address(null, addressResult.lat(), addressResult.lon(), addressResult.display_name());
            //person.setAddress(address);
        }
        person.setCreatedAt(null); // auto generated.

        // Persist person (save/insert to database)
        // --------------------------------------------------

        person = this.personRepository.save(person);

        // --------------------------------------------------

        if (notify) {
            final String content = String.format(
                "You have successfully registered for the Street food go application. " +
                    "Use your email (%s) to log in.", emailAddress);
            //String to = person.getEmailAddress();
            String to = "arg.papa97@gmail.com";
            String subject = "Registration Successful";
            String body = String.format("Thank you for your registration. Click here to login ");
            emailService.sendSimpleEmail(to, subject, body);
            final boolean sent = this.smsNotificationPort.sendSms(mobilePhoneNumber, content);
            if (!sent) {
                LOGGER.warn("SMS send to {} failed!", mobilePhoneNumber);
            }
        }

        //Always send email notification


        // Map `Person` to `PersonView`.
        // --------------------------------------------------

        final PersonView personView = this.personMapper.convertPersonToPersonView(person);

        // --------------------------------------------------

        return CreatePersonResult.success(personView);
    }
    @Transactional
    public void addLocationToPerson(String huaId, PersonLocation location) {
        /*
        Person person = personRepository.findByHuaId(huaId).orElseThrow();
        if (person.getLocations() == null) {
            person.setLocations(new ArrayList<>());
        }
        person.getLocations().add(location);
        System.out.println ("========after=======");
        System.out.println(person.toString());
        personRepository.save(person);*/
    }

    @Override
    public PersonProfileDTO getProfileData(Long personId) {
        if (personId==null) throw new NullPointerException();
        Person person = personRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String formattedAddress = String.join("+", person.getRawAddress().trim().split("\\s+"));

        AddressResult addressResult = this.addressPort.findAdress(formattedAddress).orElse(null);
        if (addressResult==null){
            return new PersonProfileDTO(person,null,null);
        }
        LOGGER.info("EKTELESTHKE TO GETPROFILEDATA");
        String mapUrl = this.addressPort.getStaticMap(addressResult.lat(),addressResult.lon());
        return new PersonProfileDTO(person, addressResult,mapUrl);
    }

    @Override
    public List<Person> findOwners() {
        return this.personRepository.findByType(PersonType.OWNER);
    }
}
