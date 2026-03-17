package gr.hua.dit.steetfood.core.port.impl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressResult(

    @JsonProperty("lon")
    String lon,

    @JsonProperty("lat")
    String lat,

    @JsonProperty("display_name")
    String displayName
) {}
