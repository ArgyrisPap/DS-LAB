package gr.hua.dit.steetfood.web.ui;


import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.model.StoreType;

import gr.hua.dit.steetfood.core.service.StoreService;

import gr.hua.dit.steetfood.core.service.model.CreateStoreRequest;
import gr.hua.dit.steetfood.core.service.model.CreateStoreResult;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        if (storeService == null) {throw new NullPointerException();}
        this.storeService = storeService;
    }

    @GetMapping("/showstores")
    public String showStore(Model model){
        //TODO DEN JERV EAN XREIAZETAI NA FTIAJW KATI TYPOY CREATESTOREREQUEST KAI RESULT
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
//TODO==============================================
    //APO EDW KAI KATW NOT CHECKED - TESTED

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

    @GetMapping("/deletestore")
    public String showDeleteStoreForm(Model model) {
        //TODO AUTHENTICATION
        model.addAttribute("storeId",null);
        return "deletestore";
    }

    @PostMapping ("/deletestore")
    public String handleDeleteStoreForm(@RequestParam Long storeId, final Model model){
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
