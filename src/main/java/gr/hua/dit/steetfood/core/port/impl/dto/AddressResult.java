package gr.hua.dit.steetfood.core.port.impl.dto;

public record AddressResult(
    Double lon,
    Double lat,
    String display_name //To match the result of the API output
) {
}
