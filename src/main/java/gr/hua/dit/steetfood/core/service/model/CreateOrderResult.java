package gr.hua.dit.steetfood.core.service.model;

public record CreateOrderResult(
    boolean created,
    String reason,
    CreateOrderRequest createOrderRequest

) {
    public static CreateOrderResult success (final CreateOrderRequest createOrderRequest) {
        if (createOrderRequest == null) throw new NullPointerException();
        return new CreateOrderResult(true, null, createOrderRequest);
    }
    public static CreateOrderResult fail(final String reason) {
        if (reason == null) throw new NullPointerException();
        if (reason.isBlank()) throw new IllegalArgumentException();
        return new CreateOrderResult(false, reason, null);
    }
}
