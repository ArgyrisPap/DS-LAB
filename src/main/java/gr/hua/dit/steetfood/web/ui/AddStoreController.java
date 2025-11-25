package gr.hua.dit.steetfood.web.ui;

import gr.hua.dit.steetfood.core.model.StoreType;
import gr.hua.dit.steetfood.core.service.StoreService;

import gr.hua.dit.steetfood.core.service.model.CreateStoreRequest;

import gr.hua.dit.steetfood.core.service.model.CreateStoreResult;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AddStoreController {

    private final StoreService storeService;

    public AddStoreController(final StoreService storeService) {
        if (storeService == null) {throw new NullPointerException();}
        this.storeService = storeService;

    }

    @GetMapping("/addstore")
    public String showAddStoreForm(Model model) {
        //TODO AUTHENTICATION
        final CreateStoreRequest createStoreRequest = new CreateStoreRequest("",
            "", StoreType.GYROS,"");
        model.addAttribute("createStoreRequest", createStoreRequest);
        return "addstore";
    }

    @PostMapping ("/addstore")
    public String handleAddStoreForm(@ModelAttribute("createStoreRequest") final CreateStoreRequest createStoreRequest
    ,final Model model){
        //TODO AUTHENTICATION
        final CreateStoreResult createStoreResult = storeService.createStore(createStoreRequest);
        if (createStoreResult.created()){
            return "redirect:/login";
        }
        model.addAttribute ("createStoreRequest", createStoreRequest);
        model.addAttribute ("errorMessage", createStoreResult.reason());
        return "/addstore";

    }
}
