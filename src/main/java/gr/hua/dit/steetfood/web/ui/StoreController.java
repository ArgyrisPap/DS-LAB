package gr.hua.dit.steetfood.web.ui;


import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.model.StoreType;

import gr.hua.dit.steetfood.core.service.PersonService;
import gr.hua.dit.steetfood.core.service.StoreService;

import gr.hua.dit.steetfood.core.service.model.CreateStoreRequest;
import gr.hua.dit.steetfood.core.service.model.CreateStoreResult;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StoreController {

    private final StoreService storeService;
    private final PersonService personService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreController.class);

    public StoreController(final StoreService storeService,
                           final PersonService personService) {
        if (storeService == null) {throw new NullPointerException();}
        if (personService == null) {throw new NullPointerException();}
        this.storeService = storeService;
        this.personService = personService;
    }

    @GetMapping("/showstores")
    public String showStore(Model model){
        List<Store> stores = this.storeService.getAllStores();
        //initial data for the form
        model.addAttribute("stores",stores);
        return "showstores";
    }

    @PostMapping("/showstores")
    public String filterStores(@ModelAttribute("stores") final StoreType type, final Model model){
        if (type == null) return "showstores";
        model.addAttribute("stores",this.storeService.findStoresByType(type));
        return "showstores";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/addstore")
    public String showAddStoreForm(final Authentication authentication,
                                   final Model model) {

        List <Person> owners = this.personService.findOwners();
        if (owners==null){throw new RuntimeException("we dont have owners");}

        //pass empty form
        final CreateStoreRequest createStoreRequest = new CreateStoreRequest("",
            "", null,"",0.0,null);
        model.addAttribute("createStoreRequest", createStoreRequest);
        model.addAttribute("owners",owners);
        model.addAttribute("storeTypes", StoreType.values());

        return "addstore";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping ("/addstore")
    public String handleAddStoreForm(final Authentication authentication,
       @Valid @ModelAttribute("createStoreRequest") final CreateStoreRequest createStoreRequest,
       final BindingResult bindingResult,
       final Model model){
        if (bindingResult.hasErrors()) {
            return "addstore";
        }

        final CreateStoreResult createStoreResult = storeService.createStore(createStoreRequest);
        if (createStoreResult.created()){
            LOGGER.info("FTIAXTHKE TO MAGAZI!");
            return "redirect:/showstores";
        }
        model.addAttribute ("createStoreRequest", createStoreRequest);
        model.addAttribute ("errorMessage", createStoreResult.reason());
        return "/addstore";

    }
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/profile/mystores")
    public String showMyStores (Authentication authentication,
                                Model model) {
        if (!AuthUtils.isAuthenticated(authentication)){
            return "redirect:/profile";
        }
        List <Store> stores = this.storeService.findMyStores();


        model.addAttribute ("stores",stores);
        //System.out.println("BRHKA APO TO CONTROLLER AUTA TA STORE:"+stores.toString());
        return "mystores"; //NOT READY YET
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/store/{id}/managestore")
    public String manageStore (@PathVariable Long id,
                               Model model,
                               Authentication authentication) {
        LOGGER.info("Opening managestore  page");
        if (!AuthUtils.isAuthenticated(authentication)) {
            LOGGER.warn("REDIRECTING UNAUTHORIZED TO LOGIN");
            return "redirect:/login";
        }
        Store store = this.storeService.isOwnerOfStore(id).orElse(null);
        if (store==null) throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Store not found or not owned");
        final List<FoodItem> menuItems = this.storeService.getFoodItemListByStoreId(id);

        if (menuItems.isEmpty()) {
            LOGGER.warn("Store {} has no menu items", id);
            model.addAttribute("errorMessage", "This store has no available items at the moment");

        }
        model.addAttribute("store", store);
        model.addAttribute("menuItems", menuItems);

        return "managestore"; //NOT READY YET
    }
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/store/{id}/togglestatus")
    public String toggleStoreStatus(@PathVariable Long id,
                                    Authentication authentication) {

        if (!AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/login";
        }

        Store store = storeService.isOwnerOfStore(id).orElse(null);
        if (store == null )throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Store not found or not owned");

        storeService.changeStoreStatus(store);

        return "redirect:/store/" + id + "/managestore";
    }

}
