package gr.hua.dit.steetfood.web.ui;

import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.model.StoreType;
import gr.hua.dit.steetfood.core.service.PersonService;
import gr.hua.dit.steetfood.core.service.StoreService;
import gr.hua.dit.steetfood.core.service.model.CreatePersonRequest;
import gr.hua.dit.steetfood.core.service.model.CreatePersonResult;

import gr.hua.dit.steetfood.core.service.model.CreateStoreRequest;

import gr.hua.dit.steetfood.core.service.model.CreateStoreResult;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class DeleteStoreController {

    private final StoreService storeService;

    public DeleteStoreController(final StoreService storeService) {
        if (storeService == null) {throw new NullPointerException();}
        this.storeService = storeService;

    }

    @GetMapping("/deletestore")
    public String showDeleteStoreForm(Model model) {
        //TODO AUTHENTICATION
        model.addAttribute("storeId",null);
        return "deletestore";
    }

    @PostMapping ("/deletestore")
    public String handleDeleteStoreForm(@RequestParam Long storeId,final Model model){
        //TODO AUTHENTICATION
        final CreateStoreResult createStoreResult = storeService.deleteStore(storeId);
        if (createStoreResult.created()){
            model.addAttribute("message","Store deleted successfully");
            return "deletestore";
        }
        model.addAttribute ("errorMessage", createStoreResult.reason());
        return "deletestore";

    }
}
