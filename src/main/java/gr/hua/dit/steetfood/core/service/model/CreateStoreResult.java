package gr.hua.dit.steetfood.core.service.model;

public record CreateStoreResult(
    boolean created,
    String reason,
    CreateStoreRequest  createStoreRequest
) {
    public static CreateStoreResult success (final CreateStoreRequest createStoreRequest) {
        if (createStoreRequest == null) throw new NullPointerException();
        return new CreateStoreResult(true, null, createStoreRequest);
    }

    public static CreateStoreResult fail(final String reason) {
        if (reason == null) throw new NullPointerException();
        if (reason.isBlank()) throw new IllegalArgumentException();
        return new CreateStoreResult(false, reason, null);
    }
}
