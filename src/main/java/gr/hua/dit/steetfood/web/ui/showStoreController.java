package gr.hua.dit.steetfood.web.ui;


import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.model.StoreType;
import gr.hua.dit.steetfood.core.repository.StoreRepository;

import gr.hua.dit.steetfood.core.service.StoreService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class showStoreController {

    private final StoreService storeService;

    public showStoreController(StoreService storeService) {
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
}
