package gr.hua.dit.steetfood.web.ui;

import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.port.AddressPort;

import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.security.CurrentUser;
import gr.hua.dit.steetfood.core.security.CurrentUserProvider;
import gr.hua.dit.steetfood.core.service.PersonService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI controller for managing profile.
 */
@Controller
public class ProfileController {

    private final PersonService personService;
    private final AddressPort addressPort;
    private final PersonRepository personRepository;
    private final CurrentUserProvider currentUserProvider;

    public ProfileController(AddressPort addressPort, PersonService personService,  CurrentUserProvider currentUserProvider,
                             PersonRepository personRepository) {
        if (addressPort == null) {throw new NullPointerException();}
        if (personService == null) {throw new NullPointerException();}
        if (currentUserProvider == null) {throw new NullPointerException();}
        this.personService = personService;
        this.currentUserProvider = currentUserProvider;
        this.addressPort = addressPort;
        this.personRepository = personRepository;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        CurrentUser me = currentUserProvider.requireCurrentUser();
        Person person = personRepository.findById(me.id())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String formatedAddress = String.join("+", person.getRawAddress().trim().split("\\s+"));
        AddressResult address = this.addressPort.findAdress(formatedAddress).orElse(null);
        String staticMapUrl= this.addressPort.getStaticMap(address.lat(),address.lon());
        model.addAttribute("me", me);
        model.addAttribute("address", address);
        model.addAttribute("staticMapUrl", staticMapUrl);

        return "profile";
    }
}
