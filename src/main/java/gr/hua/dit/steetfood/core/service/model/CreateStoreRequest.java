package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.StoreType;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record CreateStoreRequest (
   @NotNull @NotEmpty String storeName,
   @NotNull @NotEmpty String storeAddress,
   @NotNull StoreType storeType,
   @NotNull @NotEmpty String phoneNumber,
    @Positive double minOrder,
    @NotNull Long ownerId

) {
}
