package gr.hua.dit.steetfood.web.ui;

import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.port.AddressPort;

import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.security.CurrentUser;
import gr.hua.dit.steetfood.core.security.CurrentUserProvider;
import gr.hua.dit.steetfood.core.service.PersonService;

import gr.hua.dit.steetfood.core.service.model.PersonProfileDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI controller for managing profile.
 */
@Controller
public class ProfileController {

    private final PersonService personService;
    private final CurrentUserProvider currentUserProvider;

    public ProfileController(PersonService personService, CurrentUserProvider currentUserProvider) {
        this.personService = personService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        CurrentUser me = currentUserProvider.requireCurrentUser();
        PersonProfileDTO profileData = personService.getProfileData(me.id());
        model.addAttribute("profile", profileData);

        return "profile";
    }
}
