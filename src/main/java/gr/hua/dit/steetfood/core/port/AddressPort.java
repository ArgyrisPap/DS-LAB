package gr.hua.dit.steetfood.core.port;

import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;

import java.util.Optional;

public interface AddressPort {

    Optional<AddressResult> findAdress (String formattedAddress);

    String getStaticMap (Double latitude, Double longitude);


}
